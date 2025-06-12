package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.other.GameTime;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class TimeHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.TimeScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    // ServerTime

    @Override
    public long getServerTime(@NotNull GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTime");
        //TODO check
        return new Date().getTime();
    }

    @Override
    public long getServerTimeByWeek(@NotNull GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTimeByWeek");
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }


    // GameTime

    @Override
    public int getGameHour(@NotNull GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getGameTimeHours();
    }

    @Override
    public @NotNull GameTime getGameTimePassed(@NotNull GroupEventLuaContext context) {
        val world = context.getSceneScriptManager().getScene().getWorld();
        val hours = world.getGameTimeHours();
        val minutes = world.getGameTimeMinutes();
        return new GameTime(hours, minutes);
    }

    @Override
    public int skipTeyvatTime(@NotNull GroupEventLuaContext context, int time, int rate) {
        return handleUnimplemented();
    }

    // Scenetime

    @Override
    public int getSceneTimeSeconds(@NotNull GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getSceneTimeSeconds();
    }
}
