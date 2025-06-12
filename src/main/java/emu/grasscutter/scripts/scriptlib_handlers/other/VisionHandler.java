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

public class VisionHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.VisionScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int addPlayerGroupVisionType(@NotNull GroupEventLuaContext groupEventLuaContext, @NotNull int[] ints, @NotNull int[] ints1) {
        return 0;
    }

    @Override
    public int delPlayerGroupVisionType(@NotNull GroupEventLuaContext groupEventLuaContext, @NotNull int[] ints, @NotNull int[] ints1) {
        return 0;
    }

    @Override
    public int setPlayerGroupVisionType(@NotNull GroupEventLuaContext groupEventLuaContext, @NotNull int[] ints, @NotNull int[] ints1) {
        return 0;
    }

    @Override
    public int revertPlayerRegionVision(@NotNull GroupEventLuaContext groupEventLuaContext, int i) {
        return 0;
    }

    @Override
    public int forbidPlayerRegionVision(@NotNull GroupEventLuaContext groupEventLuaContext, int i) {
        return 0;
    }

    @Override
    public int setPlayerEyePointStream(@NotNull GroupEventLuaContext groupEventLuaContext, int i, int i1, boolean b) {
        return 0;
    }

    @Override
    public int setPlayerEyePoint(@NotNull GroupEventLuaContext groupEventLuaContext, int i, int i1) {
        return 0;
    }

    @Override
    public int setPlayerEyePointLOD(@NotNull GroupEventLuaContext groupEventLuaContext, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public int clearPlayerEyePoint(@NotNull GroupEventLuaContext groupEventLuaContext, int i) {
        return 0;
    }

    @Override
    public int moveAvatarByPointArray(@NotNull GroupEventLuaContext groupEventLuaContext, int i, int i1, @NotNull int[] ints, float v, @NotNull String s) {
        return 0;
    }

    @Override
    public int moveAvatarByPointArrayWithTemplate(@NotNull GroupEventLuaContext groupEventLuaContext, int i, int i1, @NotNull int[] ints, int i2, float v) {
        return 0;
    }

    @Override
    public int setMonsterBattleByGroup(GroupEventLuaContext context, int configId, int groupId) {
        logger.debug("[LUA] Call SetMonsterBattleByGroup with {} {}",
            configId,groupId);
        // TODO implement scene50008_group250008057.lua uses incomplete group numbers

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        // -> MonsterForceAlertNotify
        var entity = scene.getEntityByConfigId(configId, actualGroupId);
        if(entity != null && entity instanceof EntityMonster monster) {
            scene.broadcastPacket(new PacketMonsterForceAlertNotify(monster.getId()));
        }

        return 0;
    }
}
