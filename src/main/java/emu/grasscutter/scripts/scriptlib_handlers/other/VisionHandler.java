package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.PacketMonsterForceAlertNotify;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class VisionHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.VisionScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int addPlayerGroupVisionType(@NotNull GroupEventLuaContext context, @NotNull List<Integer> uids, @NotNull List<Integer> visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int delPlayerGroupVisionType(@NotNull GroupEventLuaContext context, @NotNull List<Integer> uids, @NotNull List<Integer> visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int setPlayerGroupVisionType(@NotNull GroupEventLuaContext context, @NotNull List<Integer> uids, @NotNull List<Integer> visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int revertPlayerRegionVision(@NotNull GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int forbidPlayerRegionVision(@NotNull GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int setPlayerEyePointStream(@NotNull GroupEventLuaContext context, int targetRegionConfigId, int relatedRegionConfigId, boolean isStream) {
        return handleUnimplemented(targetRegionConfigId, relatedRegionConfigId, isStream);
    }

    @Override
    public int setPlayerEyePoint(@NotNull GroupEventLuaContext context, int targetRegionConfigId, int relatedRegionConfigId) {
        return handleUnimplemented(targetRegionConfigId, relatedRegionConfigId);
    }

    @Override
    public int setPlayerEyePointLOD(@NotNull GroupEventLuaContext context, int targetRegionConfigId, int relatedRegionConfigId, int fixLodLevel) {
        return handleUnimplemented(targetRegionConfigId, relatedRegionConfigId, fixLodLevel);
    }

    @Override
    public int clearPlayerEyePoint(@NotNull GroupEventLuaContext context, int targetRegionConfigId) {
        return handleUnimplemented(targetRegionConfigId);
    }

    @Override
    public int moveAvatarByPointArray(@NotNull GroupEventLuaContext context, int uid, int pointArrayId, @NotNull List<Integer> routeList, float speed, @NotNull String clientParams) {
        return handleUnimplemented(uid, pointArrayId, routeList, speed, clientParams);
    }

    @Override
    public int moveAvatarByPointArrayWithTemplate(@NotNull GroupEventLuaContext context, int uid, int pointArrayId, @NotNull List<Integer> routeList, int templateId, float speed) {
        return handleUnimplemented(uid, pointArrayId, routeList, templateId, speed);
    }

    @Override
    public int setMonsterBattleByGroup(GroupEventLuaContext context, int configId, int groupId) {
        logger.debug("[LUA] Call SetMonsterBattleByGroup with {} {}",
            configId, groupId);
        // TODO implement scene50008_group250008057.lua uses incomplete group numbers

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        // -> MonsterForceAlertNotify
        var entity = scene.getEntityByConfigId(configId, actualGroupId);
        if (entity instanceof EntityMonster monster) {
            scene.broadcastPacket(new PacketMonsterForceAlertNotify(monster.getId()));
        }

        return 0;
    }
}
