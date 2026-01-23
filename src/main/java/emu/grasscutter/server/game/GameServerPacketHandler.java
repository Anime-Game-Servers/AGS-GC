package emu.grasscutter.server.game;

import static emu.grasscutter.config.Configuration.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.event.game.ReceivePacketEvent;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Grasscutter.ServerDebugMode;
import emu.grasscutter.server.game.GameSession.SessionState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kotlin.Pair;
import lombok.val;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class GameServerPacketHandler {
    private final Int2ObjectMap<PacketHandler> handlers;
    private final Map<String, PacketHandler> versionHandlers;

    public GameServerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        this.handlers = new Int2ObjectOpenHashMap<>();
        this.versionHandlers = new HashMap<>();

        this.registerHandlers(handlerClass);
    }

    public void registerTypedPacketHandler(Class<TypedPacketHandler<?>> handlerClass) {
        try {
            Class<?> modelClass = TypedPacketHandler.getStaticClass(handlerClass);
            if(modelClass == null){
                return;
            }
            PacketHandler packetHandler = handlerClass.getDeclaredConstructor().newInstance();

            this.versionHandlers.put(modelClass.getSimpleName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerTypedPairPacketHandler(Class<TypedPacketPairHandler<?,?>> handlerClass) {
        try {
            Pair<Class<?>,Class<?>> modelClassPairs = TypedPacketPairHandler.getStaticClasses(handlerClass);
            if(modelClassPairs == null){
                return;
            }
            val modelClass = modelClassPairs.getFirst();
            PacketHandler packetHandler = handlerClass.getDeclaredConstructor().newInstance();

            this.versionHandlers.put(modelClass.getSimpleName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerHandlers(Class<? extends PacketHandler> handlerClass) {
        Set<Class<? extends PacketHandler>> handlerClasses = (Set<Class<? extends PacketHandler>>)(Set<?>) Grasscutter.reflector.getSubTypesOf(handlerClass);

        for (Class<? extends PacketHandler> obj : handlerClasses) {
            if(TypedPacketHandler.class.isAssignableFrom(obj)){
                this.registerTypedPacketHandler((Class<TypedPacketHandler<?>>) obj);
            }
            if(TypedPacketPairHandler.class.isAssignableFrom(obj)){
                this.registerTypedPairPacketHandler((Class<TypedPacketPairHandler<?,?>>) obj);
            }
        }

        // Debug
        Grasscutter.getLogger().debug("Registered {} version handlers and {} legacy handlers for {}", this.versionHandlers.size(), this.handlers.size(), handlerClass.getSimpleName() + "s");
    }

    @Nullable
    private PacketHandler getHandler(String packageName, int opcode) {
        PacketHandler handler = this.versionHandlers.get(packageName);
        return handler!= null ? handler : this.handlers.get(opcode);
    }

    public void handle(GameSession session, int opcode, byte[] header, byte[] payload) {
        val provider = session.getPackageIdProvider();
        if (provider == null) {
            Grasscutter.getLogger().warn("No package id provider found for version {}", session.getVersion());
            return;
        }
        String packageName = provider.getPacketName(opcode);
        PacketHandler handler = getHandler(packageName, opcode);

        if (handler != null) {
            try {
                // Make sure session is ready for packets
                SessionState state = session.getState();

                if ("PingReq".equals(packageName)) {
                    // Always continue if packet is ping request
                } else if ("GetPlayerTokenReq".equals(packageName)) {
                    if (state != SessionState.WAITING_FOR_TOKEN) {
                        return;
                    }
                } else if (state == SessionState.ACCOUNT_BANNED) {
                    session.close();
                    return;
                } else if ("PlayerLoginReq".equals(packageName)) {
                    if (state != SessionState.WAITING_FOR_LOGIN) {
                        return;
                    }
                } else if ("SetPlayerBornDataReq".equals(packageName)) {
                    if (state != SessionState.PICKING_CHARACTER) {
                        return;
                    }
                } else {
                    if (state != SessionState.ACTIVE) {
                        return;
                    }
                }

                // Invoke event.
                ReceivePacketEvent event = new ReceivePacketEvent(session, opcode, payload);
                event.call();
                if (!event.isCanceled()) // If event is not canceled, continue.
                    handler.handle(session, header, event.getPacketData());
            } catch (Exception ex) {
                // TODO Remove this when no more needed
                ex.printStackTrace();
            }
            return; // Packet successfully handled
        }

        // Log unhandled packets
        if (DEBUG_MODE_INFO.logPackets == ServerDebugMode.MISSING || DEBUG_MODE_INFO.logPackets == ServerDebugMode.ALL) {
            Grasscutter.getLogger().warn("Unhandled packet ({}): {}", opcode, PacketOpcodesUtils.getOpcodeName(opcode, session));
        }
    }
}
