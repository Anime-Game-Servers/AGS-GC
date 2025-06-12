package emu.grasscutter.scripts.scriptlib_handlers.gadget;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.platform.ConfigRoute;
import emu.grasscutter.game.entity.gadget.platform.PointArrayRoute;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.PacketPlatformChangeRouteNotify;
import emu.grasscutter.utils.Location;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.gadget.PlatformArrayInfo;
import org.anime_game_servers.gi_lua.script_lib.handler.gadget.SetPlatformPointArrayParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class PlatformHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.gadget.PlatformScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public @NotNull PlatformArrayInfo getPlatformArrayInfoByPointId(@NotNull GroupEventLuaContext groupEventLuaContext, int arrayId, int pointId) {
        handleUnimplemented(arrayId, pointId);
        return new PlatformArrayInfo(-1 , new Position(), new Position());
    }

    @Override
    public List<Integer> getPlatformPointArray(@NotNull GroupEventLuaContext groupEventLuaContext, int configId) {
        handleUnimplemented(configId);
        return List.of();
    }

    @Override
    public int setPlatformRouteIndexToNext(@NotNull GroupEventLuaContext groupEventLuaContext, int configId) {
        return handleUnimplemented(configId);
    }

    /**
     * TODO properly implement
     * @param context
     * @param entityConfigId
     * @param pointArrayId
     * @param pointList
     * @param params
     * @return
     */
    @Override
    public int setPlatformPointArray(@NotNull GroupEventLuaContext context, int entityConfigId, int pointArrayId,
                                     @NotNull List<Integer> pointList, @NotNull SetPlatformPointArrayParams params) {
        logger.warn("[LUA] Call half implemented SetPlatformPointArray with {} {} {} {}", entityConfigId, pointArrayId, pointList, params);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(routeConfig instanceof PointArrayRoute pointArrayRoute) {
            if(pointArrayRoute.getPointArrayId() == pointArrayId){
                return -1;
            }
            pointArrayRoute.setPointArrayId(pointArrayId);
        } else {
            routeConfig = new PointArrayRoute(entityGadget.getRotation(), pointArrayId);
            entityGadget.setRouteConfig(routeConfig);
        }

        if (!entityGadget.scheduleArrayPoints(pointArrayId, pointList)) {
            return -1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int setPlatformRouteId(@NotNull GroupEventLuaContext context, int entityConfigId, int routeId) {
        logger.info("[LUA] Call SetPlatformRouteId {} {}", entityConfigId, routeId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(routeConfig instanceof ConfigRoute configRoute){
            if(configRoute.getRouteId() == routeId){
                return 0;
            }
            configRoute.setRouteId(routeId);
        } else {
            routeConfig = new ConfigRoute(entityGadget.getRotation().clone(), routeId);
            entityGadget.setRouteConfig(routeConfig);
        }

        routeConfig.setStartSceneTime(scene.getSceneTime());
        routeConfig.setStartIndex(0);
        entityGadget.schedulePlatform();
        scene.broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int startPlatform(@NotNull GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StartPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.startPlatform() ? 0 : 2;
    }

    @Override
    public int stopPlatform(@NotNull GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StopPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.stopPlatform() ? 0 : 2;
    }
}
