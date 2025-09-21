package emu.grasscutter.scripts.scriptlib_handlers.activity;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class SummerTimeHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int unlockFloatSignal(@NotNull GroupEventLuaContext groupEventLuaContext, int groupId, int signalGadgetConfigId) {
        return handleUnimplemented(groupId, signalGadgetConfigId);
    }
}
