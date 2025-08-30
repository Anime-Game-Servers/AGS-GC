package emu.grasscutter.scripts.scriptlib_handlers.other;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.anime_game_servers.lua.engine.LuaTable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class LoggingHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.other.LoggingScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public void printGroupWarning(GroupEventLuaContext luaContext, @NotNull String msg) {
        var group = luaContext.getCurrentGroup();
        logger.debug("[LUA] PrintContextLog {} {}", group.getGroupInfo().getId(), msg);

    }

    @Override
    public int markPlayerAction(@NotNull GroupEventLuaContext context, int var1, int var2, int var3) {
        logger.debug("[LUA] Call MarkPlayerAction with {},{},{}",
            var1, var2, var3);

        return 0;
    }

    @Override
    public int markGroupLuaAction(@NotNull GroupEventLuaContext context, @NotNull String action, @NotNull String transaction, @NotNull LuaTable log) {
        return handleUnimplemented(action, transaction, printTable(log));
        //TODO implement log contains many sting/int key/value pairs
    }

}
