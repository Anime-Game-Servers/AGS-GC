package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.managers.blossom.BlossomSchedule;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.PacketBlossomChestCreateNotify;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.models.constants.ScriptGadgetState;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.RefreshBlossomGroupParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

public class BlossomScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.BlossomScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int createBlossomChestByGroupId(@NotNull GroupEventLuaContext context, int groupId, int chestConfigId) {
        logger.debug("[LUA] Call check CreateBlossomChestByGroupId with {} {}", groupId, chestConfigId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val currentGroup = getGroupOrCurrent(context, groupId);
        if (currentGroup == null) return 1;

        val gadget = currentGroup.getGadgets().get(chestConfigId);
        val chestGadget = context.getSceneScriptManager().createGadget(gadget);
        if (chestGadget == null) return 1;

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val blossomSchedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (blossomSchedule == null) return 1;

        blossomManager.getSpawnedChest().put(chestGadget.getConfigId(), actualGroupId);
        context.getSceneScriptManager().addEntity(chestGadget);
        context.getSceneScriptManager().getScene().broadcastPacket(
            new PacketBlossomChestCreateNotify(blossomSchedule.getRefreshId(), blossomSchedule.getCircleCampId()));
        return 0;
    }

    @Override
    public int getBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomScheduleStateByGroupId with {}", groupId);
        if (context.getCurrentGroup() == null) return -1;

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getState).orElse(-1);
    }

    @Override
    public int setBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId, int state) {
        logger.debug("[LUA] Call check SetBlossomScheduleStateByGroupId with {} {}", groupId, state);

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val result = blossomManager.setBlossomState(actualGroupId, state);
        if (result && state == 1) { // there should only be one gadget of this blossom at this point, which is the operator
            context.getSceneScriptManager().getScene().getEntities().values().stream()
                .filter(entity -> entity.getGroupId() == actualGroupId).filter(EntityGadget.class::isInstance)
                .map(EntityGadget.class::cast).findFirst().ifPresent(gadget -> gadget.updateState(ScriptGadgetState.GearAction2));
        }
        return result ? 0 : 1;
    }

    @Override
    public int refreshBlossomGroup(@NotNull GroupEventLuaContext context, @NotNull RefreshBlossomGroupParams params) {
        logger.debug("[LUA] Call check RefreshBlossomGroup with {}", params);

        val actualGroupId = getGroupIdOrCurrentId(context, params.getGroupId());
        val group = getGroupOrCurrent(context, params.getGroupId());
        if (group == null) return 1;

        val groupInstance = context.getSceneScriptManager().getGroupInstanceById(actualGroupId);
        int suiteIndex = params.getSuiteId();
        val suite = group.getSuiteByIndex(suiteIndex);
        if (suite == null || groupInstance == null) return 1;

        context.getSceneScriptManager().refreshGroup(groupInstance, suiteIndex, params.getExcludePrev());
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val schedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (schedule == null) return 0;

        val spawnedChest = blossomManager.getSpawnedChest().values().stream()
            .filter(gid -> gid == schedule.getGroupId()).findFirst().orElse(null);
        context.getSceneScriptManager().callEvent(new ScriptArgs(
            actualGroupId, spawnedChest == null ? EventType.EVENT_GROUP_REFRESH : EventType.EVENT_BLOSSOM_PROGRESS_FINISH));
        return 0;
    }

    @Override
    public int refreshBlossomDropRewardByGroupId(@NotNull GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int addBlossomScheduleProgressByGroupId(@NotNull GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check AddBlossomScheduleProgressByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return blossomManager.addBlossomProgress(actualGroupId) ? 0 : 1;
    }

    @Override
    public int getBlossomRefreshTypeByGroupId(@NotNull GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomRefreshTypeByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getRefreshType).map(BlossomRefreshType::getValue).orElse(2);
    }

}
