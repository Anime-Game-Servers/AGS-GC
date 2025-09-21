package emu.grasscutter.scripts.scriptlib_handlers.activity;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.ActivityOpenAndCloseTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ActivityHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.activity.GeneralActivityScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public @Nullable ActivityOpenAndCloseTime getActivityOpenAndCloseTimeByScheduleId(@NotNull GroupEventLuaContext context, int scheduleId) {
        val activityConfig = ActivityManager.getScheduleActivityConfigMap().get(scheduleId);

        if (activityConfig == null)
            return null;

        return new ActivityOpenAndCloseTime(activityConfig.getBeginTime(), activityConfig.getEndTime());
    }

    @Override
    public int tryRecordActivityPushTips(@NotNull GroupEventLuaContext context, int pushTipId) {
        return handleUnimplemented(pushTipId);
    }
}
