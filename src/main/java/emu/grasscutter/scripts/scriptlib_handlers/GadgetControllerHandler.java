package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.parameter.KillByConfigIdParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

public class GadgetControllerHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.GadgetControllerHandler<EntityGadget, ControllerLuaContext> {
    @Getter
    private static final Logger logger = BaseHandler.getLogger();

    /**
     * Methods used in EntityControllers/using ControllerLuaContext
     */

    @Override
    public int setGadgetState(@NotNull ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        gadget.updateState(gadgetState);
        return 0;
    }

    @Override
    public int setGadgetStateByConfigId(@NotNull ControllerLuaContext context, int configId, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;
        val groupId = gadget.getGroupId();
        val targetEntity = gadget.getScene().getEntityByConfigId(configId, groupId);
        if (!(targetEntity instanceof EntityBaseGadget)) {
            return -2;
        }

        ((EntityBaseGadget) targetEntity).updateState(gadgetState);
        return 0;
    }

    @Override
    public int getGadgetState(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getState();
    }

    @Override
    public int resetGadgetState(@NotNull ControllerLuaContext context, int gadgetState) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        gadget.getPosition().set(gadget.getBornPos());
        gadget.getRotation().set(gadget.getBornRot());
        gadget.setStartValue(0);
        gadget.setStopValue(0);
        gadget.updateState(gadgetState);
        return 0;
    }

    @Override
    public int setGearStartValue(@NotNull ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        gadget.setStartValue(startValue);
        return 0;
    }

    @Override
    public int getGearStartValue(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getStartValue();
    }

    @Override
    public int setGearStopValue(@NotNull ControllerLuaContext context, int startValue) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        gadget.setStopValue(startValue);
        return 0;
    }

    @Override
    public int getGearStopValue(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getStopValue();
    }

    @Override
    public int getGadgetStateBeginTime(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getTicksSinceStateChange();
    }

    @Override
    public int getContextGadgetConfigId(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getConfigId();
    }

    @Override
    public int getContextGroupId(@NotNull ControllerLuaContext context) {
        EntityGadget gadget = context.getEntity();
        if (gadget == null) return -1;

        return gadget.getGroupId();
    }

    @Override
    public int setGadgetEnableInteract(@NotNull ControllerLuaContext context, int groupId, int configId, boolean enable) {
        val gadget = context.getEntity();
        if (gadget.getGroupId() != groupId || gadget.getConfigId() != configId) return -1;

        gadget.setInteractEnabled(enable);

        return 0;
    }

    @Override
    public int dropSubfield(@NotNull ControllerLuaContext context, @NotNull String subfieldName) {
        val gadget = context.getEntity();

        return gadget.dropSubfield(subfieldName) ? 0 : -1;
    }

    @Override
    public List<Integer> getGatherConfigIdList(@NotNull ControllerLuaContext context) {
        val gadget = context.getEntity();
        return gadget.getChildren()
            .stream()
            .<EntityBaseGadget>mapMulti((it, s) -> {
                if (it instanceof EntityBaseGadget gadgetChild) {
                    s.accept(gadgetChild);
                }
            })
            .filter(it -> it.getState() == 0)
            .map(GameEntity::getConfigId)
            .toList();
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
    public List<Integer> getGadgetArguments(@NotNull ControllerLuaContext controllerLuaContext) {
        val gadgetArguments = controllerLuaContext.getGadget().getSpawnConfig().getArguments();
        return gadgetArguments != null ? gadgetArguments : Collections.emptyList();
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
