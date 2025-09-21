package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.anime_game_servers.core.gi.models.Vector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class DungeonScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.DungeonScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public int causeDungeonFail(@NotNull GroupEventLuaContext context) {
        logger.debug("[LUA] Call CauseDungeonFail with");

        var scriptManager = context.getSceneScriptManager();
        if (scriptManager == null) {
            return 1;
        }

        var dungeonManager = scriptManager.getScene().getDungeonManager();
        if (dungeonManager == null) {
            return 2;
        }

        dungeonManager.failDungeon();
        return 0;
    }

    @Override
    public int causeDungeonSuccess(@NotNull GroupEventLuaContext context) {
        logger.debug("[LUA] Call CauseDungeonSuccess with");

        var scriptManager = context.getSceneScriptManager();
        if (scriptManager == null) {
            return 1;
        }

        var dungeonManager = scriptManager.getScene().getDungeonManager();
        if (dungeonManager == null) {
            return 2;
        }

        dungeonManager.finishDungeon();
        return 0;
    }

    @Override
    public int enterPersistentDungeon(@NotNull GroupEventLuaContext context, int dungeonId, int var2, @NotNull Vector pos, @NotNull Vector rot) {
        return handleUnimplemented(dungeonId, var2, pos, rot);
    }

    @Override
    public int activateDungeonCheckPoint(@NotNull GroupEventLuaContext context, int pointId) {
        logger.debug("[LUA] Call ActivateDungeonCheckPoint with {}", pointId);
        var dungeonManager = context.getSceneScriptManager().getScene().getDungeonManager();
        if (dungeonManager == null) {
            return 1;
        }
        return dungeonManager.activateRespawnPoint(pointId) ? 0 : 2;
    }

    @Override
    public int getDungeonTeamPlayerNum(@NotNull GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int getDungeonTransaction(@NotNull GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public @NotNull List<Integer> getOpeningDungeonListByRosterId(@NotNull GroupEventLuaContext context, int rosterId) {
        logger.warn("[LUA] Call unimplemented GetOpeningDungeonListByRosterId with {}", rosterId);
        return List.of();
    }
}
