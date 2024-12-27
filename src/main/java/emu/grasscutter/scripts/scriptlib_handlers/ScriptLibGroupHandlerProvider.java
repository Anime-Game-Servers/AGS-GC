package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupGadgetHandler;
import emu.grasscutter.scripts.scriptlib_handlers.scene.*;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptLibGroupHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider<GroupEventLuaContext> {
    // will be replaced by smaller scriptlib_handlers
    final ScriptLibHandler oldHandler = new emu.grasscutter.scripts.ScriptLibHandler();

    final SummerTimeHandler summerTimeHandler = new emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler();
    final GroupGadgetHandler groupGadgetHandler = new GroupGadgetHandler();
    final ChallengeScriptHandler challengeHandler = new ChallengeScriptHandler();
    final DungeonScriptHandler dungeonHandler = new DungeonScriptHandler();
    final GroupManagementScriptHandler groupManagementHandler = new GroupManagementScriptHandler();
    final SceneStateScriptHandler sceneStateHandler = new SceneStateScriptHandler();
    final SealBattleScriptHandler sealBattleScriptHandler = new SealBattleScriptHandler();

    @NotNull
    @Override
    public ScriptLibHandler<GroupEventLuaContext> getScriptLibHandler() {
        return oldHandler;
    }

    @Override
    public SummerTimeScriptHandler<GroupEventLuaContext> getSummerTimeScriptHandler() {
        return summerTimeHandler;
    }

    @Override
    public GroupGadgetHandler getGroupGadgetHandler() {
        return groupGadgetHandler;
    }

    @Override
    public ChallengeScriptHandler getChallengeHandler() {
        return challengeHandler;
    }

    @Override
    public DungeonScriptHandler getDungeonHandler() {
        return dungeonHandler;
    }

    @Override
    public GroupManagementScriptHandler getGroupManagementHandler() {
        return groupManagementHandler;
    }

    @Override
    public SceneStateScriptHandler getSceneStateHandler() {
        return sceneStateHandler;
    }

    @Override
    public SealBattleScriptHandler getSealBattleHandler() {
        return sealBattleScriptHandler;
    }
}
