package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class TowerHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.TowerScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int towerCountTimeStatus(@NotNull GroupEventLuaContext context, int isDone) {
        return handleUnimplemented(isDone);
    }

    @Override
    public int towerMirrorTeamSetUp(@NotNull GroupEventLuaContext context, int team) {
        logger.debug("[LUA] Call TowerMirrorTeamSetUp with {}", team);

        context.getSceneScriptManager().unloadCurrentMonsterTide();
        context.getSceneScriptManager().getScene().getPlayers().get(0).getTowerManager().mirrorTeamSetUp(team - 1);

        return 0;
    }
}
