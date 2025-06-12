package emu.grasscutter.scripts.scriptlib_handlers.entities;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.content.GadgetWorktop;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.PacketWorktopOptionNotify;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.script_lib.handler.entites.CreateGadgetParameters;
import org.anime_game_servers.gi_lua.script_lib.handler.entites.RemainGadgetCountParameters;
import org.anime_game_servers.lua.engine.LuaTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;

import static org.anime_game_servers.gi_lua.script_lib.ScriptLibErrors.INVALID_PARAMETER;

public class GroupGadgetHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.entites.GroupGadgetHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int changeGroupGadget(GroupEventLuaContext context, int configId, int state) {
        logger.debug("[LUA] Call ChangeGroupGadget with {} {}", configId, state);

        val scene = context.getSceneScriptManager().getScene();
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val entity = scene.getEntityByConfigId(configId, groupId);

        if (entity == null) {
            return 1;
        }

        if (entity instanceof EntityGadget entityGadget) {
            entityGadget.updateState(state);
            return 0;
        }

        return 1;
    }

    @Override
    public int executeGadgetLua(@Nonnull GroupEventLuaContext groupEventLuaContext, int groupId, int gadgetCfgId, int activityType, int var4, int var5) {
        return handleUnimplemented(groupId, gadgetCfgId, activityType, var4, var5);
    }

    @Override
    public int getGadgetConfigId(@NotNull GroupEventLuaContext groupEventLuaContext, int gadgetEid) {
        logger.debug("[LUA] Call GetGadgetConfigId with {}", gadgetEid);
        val entity = groupEventLuaContext.getSceneScriptManager().getScene().getEntityById(gadgetEid);
        if (entity == null) {
            return INVALID_PARAMETER.getValue();
        }
        return entity.getConfigId();
    }

    @Override
    public int getGadgetHpPercent(@NotNull GroupEventLuaContext context, int groupId, int configId) {

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (entity == null) {
            return INVALID_PARAMETER.getValue();
        }
        if (entity.hasFightProperty(FightProperty.FIGHT_PROP_HP_PERCENT)) {
            return (int) entity.getFightProperty(FightProperty.FIGHT_PROP_HP_PERCENT);
        }
        if (entity.hasFightProperty(FightProperty.FIGHT_PROP_CUR_HP) && entity.hasFightProperty(FightProperty.FIGHT_PROP_MAX_HP)) {
            val curHp = entity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
            val maxHp = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            return (int) (curHp * 100 / maxHp);
        }
        return -1;
    }

    @Override
    public int getGadgetIdByEntityId(GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if (!(entity instanceof EntityBaseGadget)) {
            return 0;
        }
        return ((EntityBaseGadget) entity).getGadgetId();
    }

    @Override
    public int getGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId) {
        logger.debug("[LUA] Call GetGadgetStateByConfigId with {},{}",
            groupId, configId);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val gadget = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(gadget instanceof EntityGadget)) {
            return -1;
        }
        return ((EntityGadget) gadget).getState();
    }

    @Override
    public int setGadgetStateByConfigId(GroupEventLuaContext context, int configId, int gadgetState) {
        logger.debug("[LUA] Call SetGadgetStateByConfigId with {},{}",
            configId, gadgetState);

        val scene = context.getSceneScriptManager().getScene();
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val entity = scene.getEntityByConfigId(configId, groupId);

        if (!(entity instanceof EntityGadget)) {
            return 1;
        }

        ((EntityGadget) entity).updateState(gadgetState);
        return 0;
    }

    @Override
    public int setGroupGadgetStateByConfigId(GroupEventLuaContext context, int groupId, int configId, int gadgetState) {
        logger.debug("[LUA] Call SetGroupGadgetStateByConfigId with {},{},{}",
            groupId, configId, gadgetState);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(entity instanceof EntityGadget)) {
            return -1;
        }
        ((EntityGadget) entity).updateState(gadgetState);

        return 0;
    }

    @Override
    public int createGadget(@NotNull GroupEventLuaContext context, int configId) {
        logger.debug("[LUA] Call CreateGadget with {}", configId);
        val group = context.getCurrentGroup();

        createGadget(context.getSceneScriptManager(), configId, group);
        return 0;
    }

    @Override
    public int createGadgetByConfigIdByPos(@NotNull GroupEventLuaContext context, int configId, @Nullable Vector pos, @Nullable Vector rot) {
        return handleUnimplemented(configId, pos, rot);
    }

    @Override
    public int createGadgetByParamTable(@NotNull GroupEventLuaContext context, @Nullable CreateGadgetParameters creationParams) {
        return handleUnimplemented(creationParams);
    }

    @Override
    public int createGadgetWithGlobalValue(GroupEventLuaContext context, int configId, Map<String, ? extends Number> sgv) {
        return handleUnimplemented(configId, sgv);
        //TODO implement Sgv contains ["SGV_BDShootType"] or sometimes nothing.
    }

    @Override
    public int createGadgetWave(GroupEventLuaContext context, int areaId, int suitId, int offset, Vector boxSize, Vector gadgetSize) {
        return handleUnimplemented(areaId, suitId, offset, boxSize, gadgetSize);
    }

    @Override
    public int setGadgetEnableInteract(@NotNull GroupEventLuaContext context, int groupId, int configId, boolean enable) {
        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);
        if (entity == null) return -1;

        if ((entity instanceof EntityGadget gadget)) {
            gadget.setInteractEnabled(enable);
            return 0;
        }

        return -2;
    }

    @Override
    public int setGadgetTalkByConfigId(@NotNull GroupEventLuaContext context, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public int setGadgetHp(@NotNull GroupEventLuaContext context, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public int setWorktopOptionsByGroupId(@NotNull GroupEventLuaContext context, int groupId, int configId, @NotNull List<Integer> options) {
        logger.debug("[LUA] Call SetWorktopOptionsByGroupId with {},{},{}", groupId, configId, options);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 2;
        }

        worktop.addWorktopOptions(options);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

        return 0;
    }

    @Override
    public int setWorktopOptions(@NotNull GroupEventLuaContext context, @NotNull List<Integer> options) {
        logger.debug("[LUA] Call SetWorktopOptions with {}", options);
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if (callParams == null || group == null) {
            return 1;
        }
        val eid = callParams.getSourceEntityId();
        val entity = scene.getEntityById(eid);


        if (!(entity instanceof EntityGadget gadget) || options.isEmpty()) {
            return 2;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 3;
        }

        worktop.addWorktopOptions(options);
        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        }, 1);
        return 0;
    }

    @Override
    public int delWorktopOptionByGroupId(@NotNull GroupEventLuaContext context, int groupId, int configId, int option) {
        logger.debug("[LUA] Call DelWorktopOptionByGroupId with {},{},{}", groupId, configId, option);

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 1;
        }

        worktop.removeWorktopOption(option);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketWorktopOptionNotify(gadget));

        return 0;
    }

    @Override
    public int delWorktopOption(@NotNull GroupEventLuaContext context, int var1) {
        logger.debug("[LUA] Call DelWorktopOption with {}", var1);
        val callParams = context.getArgs();
        val group = context.getCurrentGroup();
        val scene = context.getSceneScriptManager().getScene();
        if (callParams == null || group == null) {
            return 1;
        }
        var eid = callParams.getSourceEntityId();
        var entity = scene.getEntityById(eid);
        if (!(entity instanceof EntityGadget gadget)) {
            return 1;
        }

        if (!(gadget.getContent() instanceof GadgetWorktop worktop)) {
            return 2;
        }

        worktop.removeWorktopOption(callParams.getParam2());

        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            scene.broadcastPacket(new PacketWorktopOptionNotify(gadget));
        }, 1);

        return 0;
    }

    @Override
    public int checkRemainGadgetCountByGroupId(@NotNull GroupEventLuaContext context, @NotNull RemainGadgetCountParameters parameters) {
        logger.debug("[LUA] Call CheckRemainGadgetCountByGroupId with {}", parameters);
        val actualGroupId = getGroupIdOrCurrentId(context, parameters.getGroupId());

        var stream = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(g -> g instanceof EntityBaseGadget entityGadget && entityGadget.getGroupId() == actualGroupId)
            .map(g -> (EntityGadget) g);

        if(parameters.getGadgetIds()!=null){
            stream = stream.filter(entityGadget -> parameters.getGadgetIds().contains(entityGadget.getGadgetId()));
        }

        var count = stream.count();
        return (int)count;
    }
}
