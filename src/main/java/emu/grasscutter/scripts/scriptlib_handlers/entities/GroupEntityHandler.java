package emu.grasscutter.scripts.scriptlib_handlers.entities;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.models.constants.EntityType;
import org.anime_game_servers.gi_lua.models.constants.GroupKillPolicy;
import org.anime_game_servers.gi_lua.models.scene.group.SceneObject;
import org.anime_game_servers.gi_lua.script_lib.handler.parameter.KillByConfigIdParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;

import static org.anime_game_servers.gi_lua.models.constants.GroupKillPolicy.*;
import static org.anime_game_servers.gi_lua.models.constants.GroupKillPolicy.GROUP_KILL_ALL;

public class GroupEntityHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.entites.GroupEntityHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int delAllSubEntityByOriginOwnerConfigId(@NotNull GroupEventLuaContext context, int configId) {
        return 0;
    }

    @Override
    public int getEntityIdByConfigId(@NotNull GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call GetEntityIdByConfigId with {}", configId);
        //TODO check
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        var entity = scene.getEntityByConfigId(configId, groupId);
        return entity != null ? entity.getId() : 0;
    }

    @Override
    public int getTeamEntityIdByUid(@NotNull GroupEventLuaContext context, int uid) {
        return 0;
    }

    @Override
    public int getAvatarEntityIdByUid(@NotNull GroupEventLuaContext context, int uid) {
        logger.warn("[LUA] Call unchecked GetAvatarEntityIdByUid with {}", uid);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityAvatar && ((EntityAvatar) e).getPlayer().getUid() == uid)
            .findFirst();
        return entity.map(GameEntity::getId).orElse(0);
    }

    @Override
    public int getConfigIdByEntityId(@NotNull GroupEventLuaContext context, int entityID) {
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityID);
        return entity != null ? entity.getConfigId() : 0;
    }

    @Override
    public int getTeamUidByEntityId(@NotNull GroupEventLuaContext context, int entityID) {
        return handleUnimplemented(entityID);
    }

    @Override
    public int getUidByTeamEntityId(@NotNull GroupEventLuaContext context, int entityID) {
        return handleUnimplemented(entityID);
    }

    @Override
    public @Nullable Vector getPosByEntityId(@NotNull GroupEventLuaContext context, int entityId) {
        logger.warn("[LUA] Call unchecked GetPosByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return entity != null ? entity.getPosition() : null;
    }

    @Override
    public @Nullable Vector getRotationByEntityId(@NotNull GroupEventLuaContext context, int entityId) {
        logger.debug("[LUA] Call unchecked GetRotationByEntityId with {}", entityId);
        //TODO check
        var entity = context.getSceneScriptManager().getScene().getEntityById(entityId);
        return entity != null ? entity.getRotation() : null;
    }

    @Override
    public int[] getSurroundUidList(@NotNull GroupEventLuaContext context, int configId, int radius) {
        return new int[] {handleUnimplemented(configId, radius)};
    }

    @Override
    public int killEntityByConfigId(GroupEventLuaContext groupEventLuaContext, KillByConfigIdParams killByConfigIdParams) {
        logger.debug("[LUA] Call KillEntityByConfigId with {}", killByConfigIdParams);
        SceneScriptManager scriptManager = groupEventLuaContext.getSceneScriptManager();

        int groupId = getGroupIdOrCurrentId(groupEventLuaContext, killByConfigIdParams.getGroupId());
        var entity = scriptManager.getScene().getEntityByConfigId(killByConfigIdParams.getConfigId(), groupId);
        if (entity == null) {
            return 0;
        }
        scriptManager.getScene().killEntity(entity, 0);
        return 0;
    }


    @Override
    public int killGroupEntityByCfgIds(GroupEventLuaContext context, int groupId, int[] monsters, int[] gadgets) {
        val sceneManager = context.getSceneScriptManager();

        val group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 10;
        }
        int[] targets = new int[monsters.length + gadgets.length];
        int targetsIndex = 0;
        for (int i = 0; i < monsters.length; i++, targetsIndex++) {
            targets[targetsIndex] = monsters[i];
        }
        for (int i = 0; i < gadgets.length; i++, targetsIndex++) {
            targets[targetsIndex] = gadgets[i];
        }

        // kill targets if exists
        for(int cfgId : targets){
            var entity = sceneManager.getScene().getEntityByConfigId(cfgId, group.getGroupInfo().getId());
            if (entity == null || cfgId == 0) {
                continue;
            }
            sceneManager.getScene().killEntity(entity, 0);
        }
        return 0;
    }

    @Override
    public int killGroupEntityByPolicy(GroupEventLuaContext context, int groupId, @NotNull GroupKillPolicy policy) {
        val sceneManager = context.getSceneScriptManager();

        val group = getGroupOrCurrent(context, groupId);
        if (group == null) {
            return 10;
        }

        var targets = new ArrayList<SceneObject>();
        if(policy==GROUP_KILL_MONSTER || policy == GROUP_KILL_ALL){
            val monsters = group.getMonsters();
            if(monsters != null)
                targets.addAll(monsters.values());
        }
        if(policy == GROUP_KILL_GADGET || policy == GROUP_KILL_ALL) {
            val gadgets = group.getGadgets();
            if(gadgets != null)
                targets.addAll(gadgets.values());
        }

        // kill targets if exists
        targets.forEach(o -> {
            var entity = sceneManager.getScene().getEntityByConfigId(o.getConfigId(), group.getGroupInfo().getId());
            if (entity == null) {
                return;
            }
            sceneManager.getScene().killEntity(entity, 0);
        });
        return 0;
    }


    @Override
    public int removeEntityByConfigId(GroupEventLuaContext context, int groupId, EntityType entityType, int configId) {
        logger.debug("[LUA] Call RemoveEntityByConfigId");

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, actualGroupId);

        if(entity == null || !entity.getEntityType().name().toUpperCase().equals(entityType.name())){
            return 1;
        }

        context.getSceneScriptManager().getScene().removeEntity(entity);

        return 0;
    }


    @Override
    public int tryReallocateEntityAuthority(GroupEventLuaContext context, int uid, int configId, int regionConfigId) {
        return handleUnimplemented(uid, configId, regionConfigId);
    }

    @Override
    public int forceRefreshAuthorityByConfigId(GroupEventLuaContext context, int configId, int uid) {
        return handleUnimplemented(configId, uid);
    }

}
