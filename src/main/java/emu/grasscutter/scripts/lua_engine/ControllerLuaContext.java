package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityBaseGadget;
import emu.grasscutter.game.entity.EntityGadget;
import lombok.Getter;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibControllerHandlerProvider;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.lua.engine.LuaEngine;
import org.jetbrains.annotations.NotNull;

/**
 * TODO handle uid
 */
public class ControllerLuaContext implements org.anime_game_servers.gi_lua.script_lib.ControllerLuaContext<EntityGadget> {
    @Getter
    final private EntityGadget gadget;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public ControllerLuaContext(LuaEngine engine, EntityGadget gadget) {
        this.gadget = gadget;
        this.engine = engine;
    }

    public EntityGadget getEntity() {
        return gadget;
    }

    @NotNull
    @Override
    public ScriptLibControllerHandlerProvider<EntityGadget, ControllerLuaContext> getScriptLibHandlerProvider() {
        return Grasscutter.getGameServer().getScriptSystem().getScriptLibControllerHandlerProvider();
    }

    @Override
    public int uid() {
        return 0;
    }

    @Override
    public int sourceEntityId() {
        return gadget.getId();
    }

    @Override
    public int targetEntityId() {
        return 0;
    }

    @Override
    public int ownerUid() {
        return 0;
    }
}
