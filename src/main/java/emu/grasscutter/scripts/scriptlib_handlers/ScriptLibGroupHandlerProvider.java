package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupEntityHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupGadgetHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupMonsterHandler;
import emu.grasscutter.scripts.scriptlib_handlers.player.QuestScriptHandler;
import emu.grasscutter.scripts.scriptlib_handlers.scene.*;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.MonsterTideScriptHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptLibGroupHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider<GroupEventLuaContext> {
    // will be replaced by smaller scriptlib_handlers
    final ScriptLibHandler oldHandler = new emu.grasscutter.scripts.ScriptLibHandler();

    // scene
    final AutoMonsterTideScriptHandler autoMonsterTideHandler = new AutoMonsterTideScriptHandler();
    final ChallengeScriptHandler challengeHandler = new ChallengeScriptHandler();
    // TODO death zone
    final DungeonScriptHandler dungeonHandler = new DungeonScriptHandler();
    // TODO Gallery
    final GroupManagementScriptHandler groupManagementHandler = new GroupManagementScriptHandler();
    final SceneStateScriptHandler sceneStateHandler = new SceneStateScriptHandler();
    final SealBattleScriptHandler sealBattleScriptHandler = new SealBattleScriptHandler();

    // player
    final QuestScriptHandler questScriptHandler = new QuestScriptHandler();

    // entity
    // TODO entity ability handler
    final GroupEntityHandler groupEntityHandler = new GroupEntityHandler();
    final GroupGadgetHandler groupGadgetHandler = new GroupGadgetHandler();
    final GroupMonsterHandler groupMonsterHandler = new GroupMonsterHandler();

    // activity
    // TODO aster
    // TODO ChannelerSlab
    // TODO char amusement
    // TODO chess
    // TODO coinCollect
    // TODO crystalLink
    // TODO dig
    // TODO effigy
    // TODO FleurFair
    // TODO FungusFighter
    // TODO HideAndSeek
    // TODO InstableSpray
    // TODO IrodoriChess
    // TODO LanternRite
    // TODO LuminanceStoneChallenge
    // TODO LunaRite
    // TODO Mechanicus
    // TODO MistTrial
    // TODO Potion
    // TODO RogueDiary
    // TODO Roguelike
    final SummerTimeHandler summerTimeHandler = new SummerTimeHandler();
    // TODO TreasureMap
    // TODO TreasureSeelie
    // TODO UgcDungeon
    // TODO Vintage
    // TODO WinterCamp


    @NotNull
    @Override
    public ScriptLibHandler<GroupEventLuaContext> getScriptLibHandler() {
        return oldHandler;
    }

    @Override
    public SummerTimeScriptHandler<GroupEventLuaContext> getSummerTimeHandler() {
        return summerTimeHandler;
    }


    @Override
    public @Nullable MonsterTideScriptHandler<GroupEventLuaContext> getMonsterTideHandler() {
        return autoMonsterTideHandler;
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

    // Player
    @Override
    public QuestScriptHandler getQuestHandler() {
        return questScriptHandler;
    }

    // entity
    @Override
    public GroupEntityHandler getGroupEntityHandler() {
        return groupEntityHandler;
    }

    @Override
    public GroupGadgetHandler getGroupGadgetHandler() {
        return groupGadgetHandler;
    }

    @Override
    public @Nullable GroupMonsterHandler getGroupMonsterHandler() {
        return groupMonsterHandler;
    }
}
