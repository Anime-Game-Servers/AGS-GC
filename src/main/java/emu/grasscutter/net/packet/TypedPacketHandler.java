package emu.grasscutter.net.packet;

import emu.grasscutter.Loggers;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.core.interfaces.ProtoModel;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypedPacketHandler<T extends ProtoModel> extends PacketHandler {
    final private Method parseMethod;
    final private MethodHandle parseMethodHandle;

    @Nullable
    public static Class<?> getStaticClass(Class<? extends TypedPacketHandler> handlerClass) {
        if(!TypedPacketHandler.class.isAssignableFrom(handlerClass)){
            return null;
        }
        Type superClassType = handlerClass.getGenericSuperclass();
        if(superClassType == null || !(superClassType instanceof ParameterizedType)){
            return null;
        }
        ParameterizedType superClass = (ParameterizedType) superClassType;

        if(superClass.getActualTypeArguments().length == 0){
            return null;
        }
        return (Class<?>) superClass.getActualTypeArguments()[0];
    }

    public TypedPacketHandler() {
        Class<?> modelClass = getStaticClass(getClass());
        if(modelClass == null){
            throw new RuntimeException("Could not find model class for " + getClass().getName());
        }
        try {
            parseMethod = modelClass.getMethod("parseBy", byte[].class, Version.class);
            if(!parseMethod.getReturnType().isAssignableFrom(modelClass))
                throw new RuntimeException("parseBy method does not return " + modelClass.getName());
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            val originalParseMethodHandle = lookup.unreflect(parseMethod);
            val newParseMethodType = originalParseMethodHandle.type().changeReturnType(ProtoModel.class);
            parseMethodHandle = originalParseMethodHandle.asType(newParseMethodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        T model;
        try {
            model = (T) parseMethodHandle.invokeExact(payload, session.getVersion());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        try {
            handle(session, header, model);
        } catch (Throwable ex){
            Loggers.getDefaultLogger().error("Unhandled exception in TypedPacketHandler of type {}\n\tModel data: {}",
                this.getClass().getSimpleName(), model.toString(), ex);
        }
    }

    public abstract void handle(GameSession session, byte[] header, T payload) throws Exception;
}
