package emu.grasscutter.net.packet;

import emu.grasscutter.server.game.GameSession;
import kotlin.Pair;
import lombok.val;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.core.interfaces.ProtoModel;
import org.anime_game_servers.multi_proto.gi.messages.packet_head.PacketHead;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Typed handler for Packet pairs, normally a Req and Rsp packet. It handles sending the answer to the client, and expects
 * the handle implementation to properly prepare the Rsp packet for that reason
 * @param <REQ> ProtoModel class for the req packet
 * @param <RSP> ProtoModel class for the rsp packet
 */
public abstract class TypedPacketPairHandler<REQ extends ProtoModel, RSP extends ProtoModel> extends PacketHandler {
    private final Method parseReqMethod;
    private final Constructor<RSP> rspConstructor;

    @Nullable
    public static Pair<Class<?>,Class<?>> getStaticClasses(Class<? extends TypedPacketPairHandler> handlerClass) {
        if(!TypedPacketPairHandler.class.isAssignableFrom(handlerClass)){
            return null;
        }
        Type superClassType = handlerClass.getGenericSuperclass();
        if(!(superClassType instanceof ParameterizedType superClass)){
            return null;
        }

        if(superClass.getActualTypeArguments().length <= 1){
            return null;
        }
        return new Pair<>((Class<?>) superClass.getActualTypeArguments()[0],(Class<?>) superClass.getActualTypeArguments()[1]);
    }

    public TypedPacketPairHandler() {
        Pair<Class<REQ>, Class<RSP>> modelClassPair = (Pair<Class<REQ>, Class<RSP>>) (Pair<?,?>)getStaticClasses(getClass());
        if(modelClassPair == null){
            throw new RuntimeException("Could not find model class for " + getClass().getName());
        }
        try {
            val reqClass = modelClassPair.getFirst();
            parseReqMethod = reqClass.getMethod("parseBy", byte[].class, Version.class);
            if(!parseReqMethod.getReturnType().isAssignableFrom(reqClass))
                throw new RuntimeException("parseBy method does not return " + reqClass.getName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            val rspClass = modelClassPair.getSecond();
            rspConstructor = rspClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        val req = (REQ) parseReqMethod.invoke(null, payload, session.getVersion());
        val rsp = rspConstructor.newInstance();
        val shouldSend = handle(session, header, req, rsp);
        if(shouldSend)
            sendRsp(session, rsp);
    }

    /**
     * Function that does the actual sending of the response packet.
     * If something specific needs to be done with the packet, like forcing the dispatch key or
     * passing special arguments like shouldBuildHeader or client sequence, or changing the target,
     * this can be done by overwriting this function.
     * @param session Game session where the req came from.
     * @param response response model filled by teh handle function for sending.
     */
    public void sendRsp(GameSession session, RSP response){
        session.send(new BaseTypedPacket<>(response) {});
    }

    /**
     * this handles the incoming packet, calls into the corresponding systems, and prepares the response.
     * @param session Game session where the req came from.
     * @param header Raw bytes send as the header part of the packet. Most of the time either null or an encoded [PacketHead]
     * @param request Already parsed request send by the client
     * @param response Response that will be sent after the return, that can be filled with info
     * @return if true the rsp is set, if false the client doesn't send any response
     */
    public abstract boolean handle(GameSession session, byte[] header, REQ request, RSP response);
}
