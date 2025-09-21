package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.SceneScriptManager;
import lombok.Getter;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider;
import org.anime_game_servers.lua.engine.LuaEngine;
import org.jetbrains.annotations.NotNull;

public class GroupEventLuaContext implements org.anime_game_servers.gi_lua.script_lib.GroupEventLuaContext {
    @Getter
    final private SceneGroup groupInstance;
    @Getter
    final private ScriptArgs args;
    final private int ownerUid;

    final private SceneScriptManager scriptManager;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public GroupEventLuaContext(LuaEngine engine, SceneGroup groupInstance, ScriptArgs args, SceneScriptManager scriptManager) {
        this.groupInstance = groupInstance;
        this.args = args;
        this.scriptManager = scriptManager;
        this.engine = engine;
        this.ownerUid = 0;
    }

    public SceneGroup getCurrentGroup() {
        return groupInstance;
    }

    public SceneScriptManager getSceneScriptManager() {
        return scriptManager;
    }

    @NotNull
    @Override
    public ScriptLibGroupHandlerProvider getScriptLibHandlerProvider() {
        return Grasscutter.getGameServer().getScriptSystem().getScriptLibGroupHandlerProvider();
    }

    @Override
    public int uid() {
        return args.getUid();
    }

    @Override
    public int sourceEntityId() {
        return args.getSourceEntityId();
    }

    @Override
    public int targetEntityId() {
        return args.getTargetEntityId();
    }

    @Override
    public int ownerUid() {
        return ownerUid;
    }
}
