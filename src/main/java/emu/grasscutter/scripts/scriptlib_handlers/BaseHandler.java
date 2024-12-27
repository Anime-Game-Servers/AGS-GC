package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.lua.engine.LuaTable;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Set;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class BaseHandler {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    protected int handleUnimplemented(Object... args){
        val methodName = StackWalker.getInstance(Set.of(RETAIN_CLASS_REFERENCE)).walk(s -> s.limit(2).toList().get(1)).getMethodName();
        logger.warn("[LUA] Call unimplemented {} with {}", methodName, Arrays.toString(args));
        return 0;
    }

    protected SceneGroup getGroupOrCurrent(GroupEventLuaContext context, int groupId){
        return groupId!=0 ? context.getSceneScriptManager().getGroupById(groupId) : context.getCurrentGroup();
    }
    protected int getGroupIdOrCurrentId(GroupEventLuaContext context, int groupId){
        return groupId!=0 ? groupId : context.getCurrentGroup().getGroupInfo().getId();
    }

    protected String printTable(LuaTable table){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(var meta : table.getKeys()){
            sb.append(meta).append(":").append(table.get(meta)).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    protected GameEntity createGadget(SceneScriptManager sceneScriptManager, int configId, org.anime_game_servers.gi_lua.models.scene.group.SceneGroup group){
        val groupId = group.getGroupInfo().getId();
        val groupBlockId = group.getGroupInfo().getBlockId();
        val groupGadgets = group.getGadgets();
        if (groupGadgets == null){
            logger.warn("[LUA] Create gadget called with cid: {} gid: {} bid: {}, but gadgets is null", configId, groupId, groupBlockId);
            return null;
        }

        val gadget = groupGadgets.get(configId);
        if (gadget == null){
            logger.warn("[LUA] Create gadget called with cid: {} gid: {} bid: {}, but gadget is null", configId, groupId, groupBlockId);
            return null;
        }

        val entity = sceneScriptManager.createGadget(gadget);
        if(entity==null){
            logger.warn("[LUA] Create gadget null with cid: {} gid: {} bid: {}", configId, groupId, groupBlockId);
            return null;
        }

        sceneScriptManager.addEntity(entity);
        return entity;
    }
}
