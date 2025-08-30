package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.ModifyClimatePolygonParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class WeatherHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.WeatherScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int setWeatherAreaState(@NotNull GroupEventLuaContext context, int weatherAreaId, boolean openWeather) {
        logger.warn("[LUA] Call unimplemented SetWeatherAreaState with {} {}", weatherAreaId, openWeather);
        if (openWeather) {
            return context.getSceneScriptManager().getScene().addWeatherArea(weatherAreaId) ? 0 : 1;
        } else {
            return context.getSceneScriptManager().getScene().removeWeatherArea(weatherAreaId) ? 0 : 1;
        }
    }

    @Override
    public int enterWeatherArea(@NotNull GroupEventLuaContext context, int weatherAreaId) {
        context.getSceneScriptManager().getScene().getPlayers().forEach(p -> {
            if (p.getWeatherAreaId() != weatherAreaId) p.updateWeather(p.getScene());
        });

        return 0;
    }

    @Override
    public int modifyClimatePolygonParamTable(@NotNull GroupEventLuaContext groupEventLuaContext, int i, @NotNull ModifyClimatePolygonParams params) {
        return handleUnimplemented();
    }
}
