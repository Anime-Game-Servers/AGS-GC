package emu.grasscutter.scripts.scriptlib_handlers.entities;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.script_lib.handler.entites.MonsterFaceAvatarParameters;
import org.anime_game_servers.lua.engine.LuaTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class GroupMonsterHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.entites.GroupMonsterHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int createMonster(@NotNull GroupEventLuaContext context, @Nullable LuaTable table) {
        logger.debug("[LUA] Call CreateMonster with {}",
            printTable(table));
        val configId = table.getInt("config_id");
        val delayTime = table.getInt("delay_time");
        val group = context.getCurrentGroup();

        context.getSceneScriptManager().spawnMonstersByConfigId(group, configId, delayTime);
        return 0;
    }

    @Override
    public int createMonsterWithGlobalValue(@NotNull GroupEventLuaContext context, int configId, @Nullable Map<String, ? extends Number> sgvTable) {
        return handleUnimplemented(configId, sgvTable);
    }

    @Override
    public int createMonsterByConfigIdByPos(@NotNull GroupEventLuaContext context, int configId, @NotNull Vector pos, @Nullable Vector rot) {
        return handleUnimplemented(configId, pos, rot);
    }

    @Override
    public int createMonsterFaceAvatar(@NotNull GroupEventLuaContext context, @NotNull MonsterFaceAvatarParameters params) {
        return handleUnimplemented(params);
    }

    @Override
    public int createMonstersFromMonsterPool(@NotNull GroupEventLuaContext context, @NotNull String poolName) {
        return handleUnimplemented(poolName);
    }

    @Override
    public int getMonsterIdByEntityId(@NotNull GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if (!(entity instanceof EntityMonster)) {
            return 0;
        }
        return ((EntityMonster) entity).getMonsterData().getId();
    }

    @Override
    public int getMonsterConfigId(@NotNull GroupEventLuaContext context, int entityId) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        if (!(entity instanceof EntityMonster)) {
            return 0;
        }
        return entity.getConfigId();
    }

    @Override
    public int getMonsterHpPercent(@NotNull GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId);
    }

    @Override
    public @NotNull List<Integer> getMonsterAffixListByConfigId(@NotNull GroupEventLuaContext context, int groupId, int configId) {
        logger.warn("[LUA] Call unimplemented getMonsterAffixListByConfigId with {}, {}", groupId, configId);
        return List.of();
    }

    @Override
    public int lockMonsterHp(@NotNull GroupEventLuaContext context, int configId) {
        return handleUnimplemented(configId);
    }

    @Override
    public int unlockMonsterHp(@NotNull GroupEventLuaContext context, int configId) {
        return handleUnimplemented(configId);
    }

    @Override
    public int setMonsterHp(@NotNull GroupEventLuaContext context, int groupId, int configId, int hpPercent) {
        return handleUnimplemented(groupId, configId, hpPercent);
    }

    @Override
    public int setMonsterAIByGroup(@NotNull GroupEventLuaContext context, int aiId, int configId, int groupId) {
        return handleUnimplemented(aiId, configId, groupId);
    }
}
