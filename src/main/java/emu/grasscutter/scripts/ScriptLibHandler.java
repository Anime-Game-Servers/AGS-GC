package emu.grasscutter.scripts;

import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.*;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.constants.*;
import org.anime_game_servers.lua.engine.LuaTable;
import org.slf4j.Logger;

import java.util.*;

public class ScriptLibHandler extends BaseHandler{
    @Getter
    private static final Logger logger = BaseHandler.getLogger();

    // TODO
    /*@Override
    public int createChannellerSlabCampRewardGadget(GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call unimplemented CreateChannellerSlabCampRewardGadget {}", configId);
        var group = context.getCurrentGroup();
        if(group == null){
            return 1;
        }
        createGadget(context.getSceneScriptManager(), configId, group);
        //TODO implement fully
        return 0;
    }*/
}
