
package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.ScriptUtils;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.PacketBeginCameraSceneLookNotify;
import emu.grasscutter.server.packet.send.PacketCanUseSkillNotify;
import emu.grasscutter.server.packet.send.PacketCutsceneBeginNotify;
import emu.grasscutter.server.packet.send.PacketPlayerEnterSceneNotify;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.models.constants.CurveType;
import org.anime_game_servers.gi_lua.models.constants.EntityType;
import org.anime_game_servers.gi_lua.models.constants.VehicleType;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.BeginCameraSceneLookParams;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.BeginCameraSceneLookTemplateParams;
import org.anime_game_servers.lua.engine.LuaTable;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;
import org.anime_game_servers.multi_proto.gi.messages.scene.camera.KeepRotType;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

import static emu.grasscutter.game.props.EnterReason.Lua;
import static emu.grasscutter.game.props.EnterReason.LuaSkipUi;
import static org.anime_game_servers.gi_lua.utils.ScriptUtils.luaToPos;

public class ScenePlayerHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.ScenePlayerScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public boolean isPlayerTransmittable(@NotNull GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid) == 0;
    }


    @Override
    public boolean isPlayerAllAvatarDie(@NotNull GroupEventLuaContext context, int sceneUid) {
        logger.warn("[LUA] Call unimplemented IsPlayerAllAvatarDie {}", sceneUid);
        var playerEntities = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e.getEntityType().name().toUpperCase().equals(EntityType.AVATAR.name()))
            .map(EntityAvatar.class::cast)
            .filter(EntityAvatar::isAlive)
            .toList();

        //TODO check
        return playerEntities.isEmpty();
    }
    @Override
    public VehicleType getPlayerVehicleType(@NotNull GroupEventLuaContext context, int uid) {
        handleUnimplemented(uid);
        return VehicleType.NONE;
    }

    @Override
    public int enterCurve(@NotNull GroupEventLuaContext groupEventLuaContext, int uid, int curveId, int pointId, @NotNull CurveType curveType) {
        return handleUnimplemented(uid, curveId, pointId, curveType);
    }

    @Override
    public boolean isWidgetEquipped(GroupEventLuaContext context, int hostUid, int widgetId) {
        return handleUnimplemented(hostUid, widgetId) == 0;
    }
    @Override
    public int setWidgetClientDetectorCoolDown(GroupEventLuaContext context, int configId, boolean isSucc) {
        return handleUnimplemented(configId, isSucc);
    }

    @Override
    public int movePlayerToPos(@NotNull GroupEventLuaContext context, int[] targetUIds, @NotNull Vector pos, @NotNull Vector rot, int radius, boolean isSkipUi) {
        logger.warn("[LUA] Call unchecked MovePlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);
        //TODO implement var1 contains int[] uid_list, Position pos, int radius, Position rot
        return transPlayerToPos(context, targetUIds, pos, rot, radius, isSkipUi, -1); // todo this is probably not a full scene reload
    }

    @Override
    public int transPlayerToPos(@NotNull GroupEventLuaContext context, int[] targetUIds, @NotNull Vector pos, @NotNull Vector rot, int radius, boolean isSkipUi, int sceneId) {
        logger.warn("[LUA] Call unchecked TransPlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);

        var scriptManager = context.getSceneScriptManager();
        if (scriptManager == null) {
            return 2;
        }

        var scene = scriptManager.getScene();
        val reason = isSkipUi ? LuaSkipUi : Lua;
        scene.getPlayers().stream().filter(p -> ArrayUtils.contains(targetUIds, p.getUid())).forEach(p -> {
            scene.removePlayer(p);
            scene.addPlayer(p);
            val playerPos = p.getPosition().set(pos);

            // Teleport packet
            p.sendPacket(new PacketPlayerEnterSceneNotify(p, EnterType.ENTER_GOTO, reason, scene.getId(), playerPos));
        });
        return 0;
    }

    @Override
    public int playCutScene(@NotNull GroupEventLuaContext context, int cutsceneId, int waitTime) {
        logger.warn("[LUA] Call untested PlayCutScene with {} {}", cutsceneId, waitTime);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCutsceneBeginNotify(cutsceneId));
        //TODO implement
        return 0;
    }


    @Override
    public int playCutSceneWithParam(@NotNull GroupEventLuaContext context, int cutsceneId, int waitTime, @NotNull List<? extends List<Double>> paramList) {
        return handleUnimplemented();
    }

    @Override
    public int beginCameraSceneLookWithTemplate(@NotNull GroupEventLuaContext groupEventLuaContext, int templateId, @NotNull BeginCameraSceneLookTemplateParams camParam) {
        return handleUnimplemented();
    }

    @Override
    public int beginCameraSceneLook(@NotNull GroupEventLuaContext context, @NotNull BeginCameraSceneLookParams sceneLookParams) {
        logger.debug("[LUA] Call BeginCameraSceneLook with {}", sceneLookParams);
        val scene = context.getSceneScriptManager().getScene();
        val luaLookPos = sceneLookParams.getLookPos();

        val cameraParams = new PacketBeginCameraSceneLookNotify.CameraSceneLookNotify();
        var targetPos = luaLookPos != null ? new emu.grasscutter.utils.Position(luaLookPos) : new Position();
        if(sceneLookParams.getLookConfigId() != 0){
            val groupId = context.getCurrentGroup().getGroupInfo().getId();
            val entity = scene.getEntityByConfigId(sceneLookParams.getLookConfigId(), groupId);
            if(entity == null){
                logger.warn("[LUA] lookConfigId set but entity doesn't exist {}", sceneLookParams);
                return 1;
            }
            targetPos = entity.getPosition();
            cameraParams.setEntityId(entity.getId());
        }
        cameraParams.setLookPos(targetPos);

        cameraParams.setDuration(sceneLookParams.getDuration());
        cameraParams.setForce(sceneLookParams.isForce());
        cameraParams.setAllowInput(sceneLookParams.isAllowInput());
        cameraParams.setForceWalk(sceneLookParams.isForceWalk());
        cameraParams.setChangePlayMode(sceneLookParams.isChangePlayMode());
        cameraParams.setRecoverKeepCurrent(sceneLookParams.isRecoverKeepCurrent());
        cameraParams.setScreenXY(sceneLookParams.isSetScreenXY());
        if (sceneLookParams.isSetScreenXY()) {
            cameraParams.setScreenX(sceneLookParams.getScreenX());
            cameraParams.setScreenY(sceneLookParams.getScreenX());
        }
        cameraParams.setSetFollowPos(sceneLookParams.isSetFollowPos());
        if (sceneLookParams.isSetFollowPos() && sceneLookParams.getFollowPos() != null) {
            cameraParams.setFollowPos(new Position(sceneLookParams.getFollowPos()));
        }
        if(sceneLookParams.getOtherParams() != null) {
            cameraParams.setOtherParams(sceneLookParams.getOtherParams());
        }
        try {
            val keepRotType = KeepRotType.valueOf(sceneLookParams.getKeepRotType().name());
            cameraParams.setKeepRotType(keepRotType);
        } catch (IllegalArgumentException ex){
            logger.warn("[LUA] failed to convert keepRotType {}", sceneLookParams.getKeepRotType());
        }
        cameraParams.setCustomRadius(sceneLookParams.getCustomRadius());
        cameraParams.setDisableProtect(sceneLookParams.getDisableProtect());
        cameraParams.setSetFollowPos(sceneLookParams.getDisableProtect());
        cameraParams.setBlendType(sceneLookParams.getBlendType());
        cameraParams.setBlendDuration(sceneLookParams.getBlendDuration());
        cameraParams.setAbsFollowPos(sceneLookParams.isAbsFollowPos());


        // todo handle delay with timer

        val packet = new PacketBeginCameraSceneLookNotify(cameraParams);
        if(sceneLookParams.isBroadcast())
            scene.broadcastPacket(packet);
        else if(context.uid()!=0){
            scene.getPlayers().stream().filter(player -> player.getUid() == context.uid()).forEach(
                player -> player.sendPacket(packet)
            );
        } else {
            scene.getWorld().getHost().sendPacket(packet);
        }
        return 0;
    }


    @Override
    public int setIsAllowUseSkill(GroupEventLuaContext context, int canUse) {
        logger.debug("[LUA] Call SetIsAllowUseSkill with {}",
            canUse);

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCanUseSkillNotify(canUse == 1));
        return 0;
    }
}
