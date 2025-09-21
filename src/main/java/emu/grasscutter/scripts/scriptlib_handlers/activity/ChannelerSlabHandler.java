package emu.grasscutter.scripts.scriptlib_handlers.activity;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ChannelerSlabHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.activity.ChannelerSlabScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int createChannellerSlabCampRewardGadget(GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call unimplemented CreateChannellerSlabCampRewardGadget {}", configId);
        var group = context.getCurrentGroup();
        if (group == null) {
            return 1;
        }
        createGadget(context.getSceneScriptManager(), configId, group);
        //TODO implement fully
        return 0;
    }

    @Override
    public boolean isChannellerSlabLoopDungeonConditionSelected(@NotNull GroupEventLuaContext context, int conditionId) {
        return false;
    }

    @Override
    public int getChannellerSlabLoopDungeonLimitTime(@NotNull GroupEventLuaContext context) {
        return handleUnimplemented();
    }
}
