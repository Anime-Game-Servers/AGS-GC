package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.scripts.lua_engine.ControllerLuaContext;
import org.anime_game_servers.gi_lua.script_lib.handler.gadget_controller.GadgetPlayControllerHandler;
import org.jetbrains.annotations.Nullable;

public class ScriptLibControllerHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibControllerHandlerProvider<EntityGadget, ControllerLuaContext> {
    final GadgetControllerHandler gadgetControllerHandler = new GadgetControllerHandler();

    @Nullable
    @Override
    public GadgetControllerHandler getGadgetControllerHandler() {
        return gadgetControllerHandler;
    }

    @Override
    public @Nullable GadgetPlayControllerHandler<EntityGadget, ControllerLuaContext> getGadgetPlayControllerHandler() {
        return null;
    }
}
