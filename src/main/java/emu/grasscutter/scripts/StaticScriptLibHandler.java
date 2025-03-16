package emu.grasscutter.scripts;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.props.EntityIdType;
import lombok.Getter;
import org.anime_game_servers.gi_lua.script_lib.LuaContext;
import org.anime_game_servers.gi_lua.script_lib.handler.ScriptLibStaticHandler;
import org.slf4j.Logger;

public class StaticScriptLibHandler implements ScriptLibStaticHandler {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();
    @Override
    public void PrintLog(String msg) {
        logger.debug("[LUA] PrintLog: {}", msg);
    }

    @Override
    public int GetEntityType(LuaContext luaContext, int entityId) {
        return EntityIdType.fromEntityId(entityId).getType().getValue();
    }

    @Override
    public void PrintContextLog(LuaContext luaContext, String msg) {
        if(luaContext instanceof emu.grasscutter.scripts.lua_engine.GroupEventLuaContext){
            var group = ((emu.grasscutter.scripts.lua_engine.GroupEventLuaContext) luaContext).getCurrentGroup();
            logger.debug("[LUA] PrintContextLog {} {}", group.getGroupInfo().getId(), msg);
            return;
        } else {
            logger.debug("[LUA] PrintContextLog {}", msg);
        }
    }
}
