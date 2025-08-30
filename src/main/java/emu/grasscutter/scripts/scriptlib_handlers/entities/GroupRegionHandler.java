package emu.grasscutter.scripts.scriptlib_handlers.entities;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.constants.EntityType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class GroupRegionHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.entites.GroupRegionScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int getRegionEntityCount(GroupEventLuaContext context, int regionEid, EntityType entityType) {
        logger.debug("[LUA] Call GetRegionEntityCount with {} {}", regionEid, entityType.name());

        var region = context.getSceneScriptManager().getRegionById(regionEid);

        if (region == null) {
            return 0;
        }

        return (int) region.getEntities().stream().filter(e -> e.getEntityType().name().toUpperCase().equals(entityType.name())).count();
    }

    @Override
    public int getRegionConfigId(GroupEventLuaContext context, int regionEid) {
        logger.debug("[LUA] Call GetRegionConfigId with {}", regionEid);
        val region = context.getSceneScriptManager().getRegionById(regionEid);
        if (region == null) {
            return -1;
        }
        return region.getConfigId();
    }

    @Override
    public boolean isInRegion(@NotNull GroupEventLuaContext context, int uid, int regionId) {
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        var region = context.getSceneScriptManager().getRegionByConfigId(groupId, regionId);
        return region.getEntities().stream()
            .anyMatch(e -> e instanceof EntityAvatar && ((EntityAvatar) e).getPlayer().getUid() == uid);
    }
}
