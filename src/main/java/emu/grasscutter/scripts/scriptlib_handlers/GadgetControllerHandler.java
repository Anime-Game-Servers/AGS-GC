package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.parameter.KillByConfigIdParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class GadgetControllerHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.GadgetControllerHandler<EntityGadget, ControllerLuaContext> {
    @Getter
    private static final Logger logger = BaseHandler.getLogger();

    /**
     * Methods used in EntityControllers/using ControllerLuaContext
     */

    @Override
    public int setGadgetState(ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.updateState(gadgetState);
        return 0;
    }

    @Override
    public int getGadgetState(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getState();
    }

    @Override
    public int resetGadgetState(ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.getPosition().set(gadget.getBornPos());
        gadget.getRotation().set(gadget.getBornRot());
        gadget.setStartValue(0);
        gadget.setStopValue(0);
        gadget.updateState(gadgetState);
        return 0;
    }

    @Override
    public int setGearStartValue(ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.setStartValue(startValue);
        return 0;
    }

    @Override
    public int getGearStartValue(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getStartValue();
    }

    @Override
    public int setGearStopValue(ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        gadget.setStopValue(startValue);
        return 0;
    }

    @Override
    public int getGearStopValue(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getStopValue();
    }

    @Override
    public int getGadgetStateBeginTime(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getTicksSinceStateChange();
    }

    @Override
    public int getContextGadgetConfigId(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getConfigId();
    }

    @Override
    public int getContextGroupId(ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if(gadget == null) return -1;

        return gadget.getGroupId();
    }

    @Override
    public int setGadgetEnableInteract(ControllerLuaContext context, int groupId, int configId, boolean enable) {
        val gadget = context.getEntity();
        if(gadget.getGroupId() != groupId || gadget.getConfigId() != configId) return -1;

        gadget.setInteractEnabled(enable);

        return 0;
    }

    @Override
    public int dropSubfield(ControllerLuaContext context, @NotNull String subfieldName) {
        val gadget = context.getEntity();

        return gadget.dropSubfield(subfieldName) ? 0 : -1;
    }

    @Override
    public int[] getGatherConfigIdList(ControllerLuaContext context) {
        val gadget = context.getEntity();
        val children = gadget.getChildren();

        val configIds = new int[children.size()];
        for(int i = 0; i < children.size(); i++) {
            configIds[i] = children.get(i).getConfigId();
        }

        return configIds;
    }


    @Override
    public int killEntityByConfigId(@NotNull ControllerLuaContext controllerLuaContext, @NotNull KillByConfigIdParams killByConfigIdParams) {
        logger.debug("[LUA] Call KillEntityByConfigId with {}", killByConfigIdParams);
        SceneScriptManager scriptManager = controllerLuaContext.getGadget().getScene().getScriptManager();

        int groupId = killByConfigIdParams.getGroupId() != 0 ? killByConfigIdParams.getGroupId() : controllerLuaContext.getGadget().getGroupId();
        var entity = scriptManager.getScene().getEntityByConfigId(killByConfigIdParams.getConfigId(), groupId);
        if (entity == null) {
            return 0;
        }
        scriptManager.getScene().killEntity(entity, 0);
        return 0;
    }

    @NotNull
    @Override
    public int[] getGadgetArguments(@NotNull ControllerLuaContext controllerLuaContext) {
        val gadgetArguments = controllerLuaContext.getGadget().getSpawnConfig().getArguments();
        return gadgetArguments != null ? gadgetArguments.stream().mapToInt(Integer::intValue).toArray() : new int[0];
    }

    @Override
    public int getContextGadgetEntityId(@NotNull ControllerLuaContext controllerLuaContext) {
        return controllerLuaContext.getEntity().getId();
    }

    @Override
    public int gadgetLuaNotifyGroup(@NotNull ControllerLuaContext controllerLuaContext, int var1, int var2, int var3) {
        return handleUnimplemented(var1, var2, var3);
    }
}
