package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.activity.ActivityHandler;
import emu.grasscutter.scripts.scriptlib_handlers.activity.SummerTimeHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupEntityHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupGadgetHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupMonsterHandler;
import emu.grasscutter.scripts.scriptlib_handlers.entities.GroupRegionHandler;
import emu.grasscutter.scripts.scriptlib_handlers.gadget.PlatformHandler;
import emu.grasscutter.scripts.scriptlib_handlers.other.*;
import emu.grasscutter.scripts.scriptlib_handlers.player.QuestScriptHandler;
import emu.grasscutter.scripts.scriptlib_handlers.scene.*;
import org.anime_game_servers.gi_lua.script_lib.handler.activity.SummerTimeScriptHandler;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.MonsterTideScriptHandler;
import org.jetbrains.annotations.Nullable;

public class ScriptLibGroupHandlerProvider implements org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider<GroupEventLuaContext> {

    // scene
    final AutoMonsterTideScriptHandler autoMonsterTideHandler = new AutoMonsterTideScriptHandler();
    final BlossomScriptHandler blossomHandler = new BlossomScriptHandler();
    final ChallengeScriptHandler challengeHandler = new ChallengeScriptHandler();
    // TODO death zone
    final DungeonScriptHandler dungeonHandler = new DungeonScriptHandler();
    // TODO Gallery
    final GroupManagementScriptHandler groupManagementHandler = new GroupManagementScriptHandler();
    final ScenePlayerHandler scenePlayerHandler = new ScenePlayerHandler();
    // TODO Hunting
    // TODO GadgetChain
    // TODO ScenePlay
    final SceneStateScriptHandler sceneStateHandler = new SceneStateScriptHandler();
    final SealBattleScriptHandler sealBattleScriptHandler = new SealBattleScriptHandler();
    final TimersScriptHandler timersScriptHandler = new TimersScriptHandler();
    final WeatherHandler weatherHandler = new WeatherHandler();

    // player
    final QuestScriptHandler questScriptHandler = new QuestScriptHandler();

    // entity
    // TODO entity ability handler
    final GroupEntityHandler groupEntityHandler = new GroupEntityHandler();
    final GroupGadgetHandler groupGadgetHandler = new GroupGadgetHandler();
    final GroupMonsterHandler groupMonsterHandler = new GroupMonsterHandler();
    final GroupRegionHandler groupRegionHandler = new GroupRegionHandler();

    //Gadget
    // TODO GadgetGiving
    // TODO GadgetPlay
    final PlatformHandler platformHandler = new PlatformHandler();
    // TODO Vehicle

    // other
    // TODO Aranara
    final MiscNotifyHandler miscNotifyHandler = new MiscNotifyHandler();
    final LoggingHandler loggingHandler = new LoggingHandler();
    // TODO offering
    // TODO RegionalPlay
    final TowerHandler towerHandler = new TowerHandler();
    final TimeHandler timeHandler = new TimeHandler();
    final VisionHandler visionHandler = new VisionHandler();

    // activity
    final ActivityHandler activityHandler = new ActivityHandler();
    // TODO aster
    // TODO ChannelerSlab
    // TODO char amusement
    // TODO chess
    // TODO coinCollect
    // TODO crystalLink
    // TODO dig
    // TODO effigy
    // TODO expedition
    // TODO FleurFair
    // TODO FungusFighter
    // TODO GravenInnocence
    // TODO HideAndSeek
    // TODO InstableSpray
    // TODO IrodoriChess
    // TODO LanternRite
    // TODO LuminanceStoneChallenge
    // TODO LunaRite
    // TODO Mechanicus
    // TODO MichiaeMatsuri
    // TODO MistTrial
    // TODO Moonfin
    // TODO Potion
    // TODO RogueDiary
    // TODO Roguelike
    // TODO SeaLamp
    final SummerTimeHandler summerTimeHandler = new SummerTimeHandler();
    // TODO TreasureMap
    // TODO TreasureSeelie
    // TODO UgcDungeon
    // TODO Vintage
    // TODO WaterSpiritChallenge
    // TODO WinterCamp

    @Override
    public ActivityHandler getActivityHandler() {
        return activityHandler;
    }

    @Override
    public SummerTimeScriptHandler<GroupEventLuaContext> getSummerTimeHandler() {
        return summerTimeHandler;
    }


    @Override
    public MonsterTideScriptHandler<GroupEventLuaContext> getMonsterTideHandler() {
        return autoMonsterTideHandler;
    }

    @Override
    public BlossomScriptHandler getBlossomHandler() {
        return blossomHandler;
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
    public ScenePlayerHandler getScenePlayerHandler() {
        return scenePlayerHandler;
    }

    @Override
    public SceneStateScriptHandler getSceneStateHandler() {
        return sceneStateHandler;
    }

    @Override
    public SealBattleScriptHandler getSealBattleHandler() {
        return sealBattleScriptHandler;
    }

    @Override
    public TimersScriptHandler getTimersHandler() {
        return timersScriptHandler;
    }

    @Override
    public @Nullable WeatherHandler getWeatherHandler() {
        return weatherHandler;
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
    public GroupMonsterHandler getGroupMonsterHandler() {
        return groupMonsterHandler;
    }
    @Override
    public GroupRegionHandler getGroupRegionHandler() {
        return groupRegionHandler;
    }

    //Gadget
    @Override
    public PlatformHandler getPlatformHandler() {
        return platformHandler;
    }

    //other


    @Override
    public MiscNotifyHandler getMiscNotifyHandler() {
        return miscNotifyHandler;
    }

    @Override
    public LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }

    @Override
    public TowerHandler getTowerHandler() {
        return towerHandler;
    }

    @Override
    public TimeHandler getTimeHandler() {
        return timeHandler;
    }

    @Override
    public VisionHandler getVisionHandler() {
        return visionHandler;
    }
}
