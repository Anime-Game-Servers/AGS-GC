package emu.grasscutter.scripts;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.entity.gadget.platform.ConfigRoute;
import emu.grasscutter.game.entity.gadget.platform.PointArrayRoute;
import emu.grasscutter.game.managers.blossom.BlossomSchedule;
import emu.grasscutter.game.managers.blossom.enums.BlossomRefreshType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import emu.grasscutter.server.packet.send.*;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.*;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.script_lib.ActivityOpenAndCloseTime;
import org.anime_game_servers.gi_lua.script_lib.LuaContext;
import org.anime_game_servers.lua.engine.LuaTable;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.*;

import static emu.grasscutter.game.props.EnterReason.Lua;
import static org.anime_game_servers.gi_lua.utils.ScriptUtils.luaToPos;

public class ScriptLibHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = BaseHandler.getLogger();

    @Override
    public void PrintGroupWarning(LuaContext luaContext, String msg) {
        if(luaContext instanceof emu.grasscutter.scripts.lua_engine.GroupEventLuaContext){
            var group = ((emu.grasscutter.scripts.lua_engine.GroupEventLuaContext) luaContext).getCurrentGroup();
            logger.debug("[LUA] PrintContextLog {} {}", group.getGroupInfo().getId(), msg);
            return;
        } else {
            logger.debug("[LUA] PrintContextLog {}", msg);
        }
    }

    @Override
    public int AutoMonsterTide(GroupEventLuaContext context, int challengeIndex, int groupId, Integer[] ordersConfigId, int tideCount, int sceneLimit, int param6) {
        logger.debug("[LUA] Call AutoMonsterTide with {},{},{},{},{},{}",
            challengeIndex,groupId,ordersConfigId,tideCount,sceneLimit,param6);

        val group = getGroupOrCurrent(context, groupId);

        if (group == null || group.getMonsters() == null) {
            return 1;
        }

        context.getSceneScriptManager().startMonsterTideInGroup(challengeIndex, group, ordersConfigId, tideCount, sceneLimit);

        return 0;
    }

    @Override
    public int KillMonsterTide(GroupEventLuaContext groupEventLuaContext, int groupId, int tideId) {
        return 0;
    }



    @Override
    public int GetGroupMonsterCountByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call GetGroupMonsterCountByGroupId with {}",
            groupId);
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityMonster && e.getGroupId() == actualGroupId)
            .count();
    }

    @Override
    public int GetRegionEntityCount(GroupEventLuaContext context, int regionEid, EntityType entityType) {
        logger.debug("[LUA] Call GetRegionEntityCount with {} {}", regionEid, entityType.name());

        var region = context.getSceneScriptManager().getRegionById(regionEid);

        if (region == null) {
            return 0;
        }

        return (int) region.getEntities().stream().filter(e -> e.getEntityType().name().toUpperCase().equals(entityType.name())).count();
    }

    @Override
    public int GetRegionConfigId(GroupEventLuaContext context, int regionEid) {
        logger.debug("[LUA] Call GetRegionConfigId with {}", regionEid);
        val region = context.getSceneScriptManager().getRegionById(regionEid);
        if (region == null){
            return -1;
        }
        return region.getConfigId();
    }

    @Override
    public int TowerCountTimeStatus(GroupEventLuaContext context, int isDone, int var2) {
        // TODO record timer
        return handleUnimplemented(isDone, var2);
    }

    @Override
    public int GetGroupMonsterCount(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetGroupMonsterCount ");

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        return (int) context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e instanceof EntityMonster &&
                e.getGroupId() == groupId)
            .count();
    }

    @Override
    public int SetMonsterBattleByGroup(GroupEventLuaContext context, int configId, int groupId) {
        logger.debug("[LUA] Call SetMonsterBattleByGroup with {} {}",
            configId,groupId);
        // TODO implement scene50008_group250008057.lua uses incomplete group numbers

        val scene = context.getSceneScriptManager().getScene();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        // -> MonsterForceAlertNotify
        var entity = scene.getEntityByConfigId(configId, actualGroupId);
        if(entity != null && entity instanceof EntityMonster monster) {
            scene.broadcastPacket(new PacketMonsterForceAlertNotify(monster.getId()));
        }

        return 0;
    }

    @Override
    public int SetIsAllowUseSkill(GroupEventLuaContext context, int canUse) {
        logger.debug("[LUA] Call SetIsAllowUseSkill with {}",
            canUse);

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCanUseSkillNotify(canUse == 1));
        return 0;
    }



    @Override
    public int TowerMirrorTeamSetUp(GroupEventLuaContext context, int team, int var1) {
        logger.debug("[LUA] Call TowerMirrorTeamSetUp with {},{}",
            team,var1);

        context.getSceneScriptManager().unloadCurrentMonsterTide();
        context.getSceneScriptManager().getScene().getPlayers().get(0).getTowerManager().mirrorTeamSetUp(team-1);

        return 0;
    }

    @Override
    public int CreateVehicle(GroupEventLuaContext groupEventLuaContext, int i, int i1, Vector position, Vector position1) {
        return 0;
    }

    @Override
    public int CheckRemainGadgetCountByGroupId(GroupEventLuaContext context, LuaTable table) {
        logger.debug("[LUA] Call CheckRemainGadgetCountByGroupId with {}",
            printTable(table));
        val actualGroupId = getGroupIdOrCurrentId(context, table.getInt("group_id"));

        var count = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(g -> g instanceof EntityGadget entityGadget && entityGadget.getGroupId() == actualGroupId)
            .count();
        return (int)count;
    }

    @Override
    public int MarkPlayerAction(GroupEventLuaContext context, int var1, int var2, int var3) {
        logger.debug("[LUA] Call MarkPlayerAction with {},{},{}",
            var1, var2,var3);

        return 0;
    }

    @Override
    public int GetSceneOwnerUid(GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getHost().getUid();
    }

    @Override
    public int ShowReminder(GroupEventLuaContext context, int reminderId) {
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketDungeonShowReminderNotify(reminderId));
        return 0;
    }

    @Override
    public int CreateGroupTimerEvent(GroupEventLuaContext context, int groupId, String source, double time) {
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return context.getSceneScriptManager().createGroupTimerEvent(actualGroupId, source, time);
    }

    @Override
    public int CancelGroupTimerEvent(GroupEventLuaContext context, int groupId, String source) {
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        return context.getSceneScriptManager().cancelGroupTimerEvent(actualGroupId, source);
    }

    @Override
    public int[] GetSceneUidList(GroupEventLuaContext context) {
        logger.warn("[LUA] Call unchecked GetSceneUidList");
        //TODO check
        val scriptManager = context.getSceneScriptManager();
        if(scriptManager == null){
            return new int[0];
        }
        return scriptManager.getScene().getPlayers().stream().mapToInt(Player::getUid).toArray();
    }

    @Override
    public int GetSeaLampActivityPhase(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int GadgetPlayUidOp(GroupEventLuaContext context, int groupId, int gadgetCrucibleCfgId, List<Integer> uidList, int var4, String var5, LuaTable var6) {
        return handleUnimplemented(groupId, gadgetCrucibleCfgId, uidList, var4, var5, var6);
    }

    @Override
    public long GetServerTime(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTime");
        //TODO check
        return new Date().getTime();
    }

    @Override
    public long GetServerTimeByWeek(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetServerTimeByWeek");
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public int GetCurTriggerCount(GroupEventLuaContext context) {
        logger.debug("[LUA] Call GetCurTriggerCount");
        //TODO check
        return context.getSceneScriptManager().getTriggerCount();
    }

    @Override
    public int GetChannellerSlabLoopDungeonLimitTime(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public boolean IsPlayerAllAvatarDie(GroupEventLuaContext context, int sceneUid) {
        logger.warn("[LUA] Call unimplemented IsPlayerAllAvatarDie {}", sceneUid);
        var playerEntities = context.getSceneScriptManager().getScene().getEntities().values().stream()
            .filter(e -> e.getEntityType().name().toUpperCase().equals(EntityType.AVATAR.name()))
            .toList();

        for (GameEntity p : playerEntities){
            var player = (EntityAvatar)p;
            if(player.isAlive()){
                return false;
            }
        }
        //TODO check
        return true;
    }

    @Override
    public int sendShowCommonTipsToClient(GroupEventLuaContext context, String title, String content, int closeTime) {
        logger.debug("[LUA] Call sendShowCommonTipsToClient with {}, {}, {}", title, content, closeTime);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowCommonTipsNotify(title, content, closeTime));
        return 0;
    }

    @Override
    public int sendCloseCommonTipsToClient(GroupEventLuaContext context) {
        logger.debug("[LUA] Call unimplemented sendCloseCommonTipsToClient");
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCloseCommonTipsNotify());
        return 0;
    }

    @Override
    public int CreateBlossomChestByGroupId(GroupEventLuaContext context, int groupId, int chestConfigId) {
        logger.debug("[LUA] Call check CreateBlossomChestByGroupId with {} {}", groupId, chestConfigId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val currentGroup = getGroupOrCurrent(context, groupId);
        if (currentGroup == null) return 1;

        val gadget = currentGroup.getGadgets().get(chestConfigId);
        val chestGadget = context.getSceneScriptManager().createGadget(gadget);
        if (chestGadget == null) return 1;

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val blossomSchedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (blossomSchedule == null) return 1;

        blossomManager.getSpawnedChest().put(chestGadget.getConfigId(), actualGroupId);
        context.getSceneScriptManager().addEntity(chestGadget);
        context.getSceneScriptManager().getScene().broadcastPacket(
            new PacketBlossomChestCreateNotify(blossomSchedule.getRefreshId(), blossomSchedule.getCircleCampId()));
        return 0;
    }

    @Override
    public int GetBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomScheduleStateByGroupId with {}", groupId);
        if (context.getCurrentGroup() == null) return -1;

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getState).orElse(-1);
    }

    @Override
    public int SetBlossomScheduleStateByGroupId(GroupEventLuaContext context, int groupId, int state) {
        logger.debug("[LUA] Call check SetBlossomScheduleStateByGroupId with {} {}", groupId, state);

        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val result = blossomManager.setBlossomState(actualGroupId, state);
        if (result && state == 1) { // there should only be one gadget of this blossom at this point, which is the operator
            context.getSceneScriptManager().getScene().getEntities().values().stream()
                .filter(entity -> entity.getGroupId() == actualGroupId).filter(EntityGadget.class::isInstance)
                .map(EntityGadget.class::cast).findFirst().ifPresent(gadget -> gadget.updateState(ScriptGadgetState.GearAction2));
        }
        return result ? 0 : 1;
    }

    @Override
    public int RefreshBlossomGroup(GroupEventLuaContext context, LuaTable configTable) {
        logger.debug("[LUA] Call check RefreshBlossomGroup with {}", printTable(configTable));

        val actualGroupId = getGroupIdOrCurrentId(context, configTable.optInt("group_id", 0));
        val group = getGroupOrCurrent(context, configTable.optInt("group_id", 0));
        if (group == null) return 1;

        val groupInstance = context.getSceneScriptManager().getGroupInstanceById(actualGroupId);
        int suiteIndex = configTable.getInt("suite");
        val suite = group.getSuiteByIndex(suiteIndex);
        if (suite == null || groupInstance == null) return 1;

        context.getSceneScriptManager().refreshGroup(groupInstance, suiteIndex, configTable.getBoolean("exclude_prev"));
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        val schedule = blossomManager.getBlossomSchedule().get(actualGroupId);
        if (schedule == null) return 0;

        val spawnedChest = blossomManager.getSpawnedChest().values().stream()
            .filter(gid -> gid == schedule.getGroupId()).findFirst().orElse(null);
        context.getSceneScriptManager().callEvent(new ScriptArgs(
            actualGroupId, spawnedChest == null ? EventType.EVENT_GROUP_REFRESH : EventType.EVENT_BLOSSOM_PROGRESS_FINISH));
        return 0;
    }

    @Override
    public int RefreshBlossomDropRewardByGroupId(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int AddBlossomScheduleProgressByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check AddBlossomScheduleProgressByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return blossomManager.addBlossomProgress(actualGroupId) ? 0 : 1;
    }

    @Override
    public int GetBlossomRefreshTypeByGroupId(GroupEventLuaContext context, int groupId) {
        logger.debug("[LUA] Call check GetBlossomRefreshTypeByGroupId with {}", groupId);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val blossomManager = context.getSceneScriptManager().getScene().getWorld().getHost().getBlossomManager();
        return Optional.ofNullable(blossomManager.getBlossomSchedule().get(actualGroupId))
            .map(BlossomSchedule::getRefreshType).map(BlossomRefreshType::getValue).orElse(2);
    }

    @Override
    public int RefreshHuntingClueGroup(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int GetHuntingMonsterExtraSuiteIndexVec(GroupEventLuaContext context) {
        return handleUnimplemented();
    }



    @Override
    public int FinishExpeditionChallenge(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int ExpeditionChallengeEnterRegion(GroupEventLuaContext context, boolean var1) {
        return handleUnimplemented();
    }

    @Override
    public int InitTimeAxis(GroupEventLuaContext context, String var1, LuaTable var2, boolean var3) {
        return handleUnimplemented(var1, printTable(var2), var3);
        //TODO implement var1 == name? var2 == delay? var3 == should loop?
    }

    @Override
    public int EndTimeAxis(GroupEventLuaContext context, String var1) {
        return handleUnimplemented(var1);
        //TODO implement var1 == name?
    }

    @Override
    public int StartHomeGallery(GroupEventLuaContext context, int galleryId, int uid) {
        return handleUnimplemented(galleryId, uid);
    }

    @Override
    public int SetHandballGalleryBallPosAndRot(GroupEventLuaContext context, int galleryId, LuaTable positionTable, LuaTable rotationTable) {
        return handleUnimplemented(galleryId, printTable(positionTable), printTable(rotationTable));
    }

    @Override
    public int SendServerMessageByLuaKey(GroupEventLuaContext context, String messageKey, int[] targets) {
        return handleUnimplemented(messageKey, targets);
    }

    @Override
    public int TryReallocateEntityAuthority(GroupEventLuaContext context, int uid, int endConfig, int var3) {
        return handleUnimplemented(uid, endConfig, var3);
    }

    @Override
    public int ForceRefreshAuthorityByConfigId(GroupEventLuaContext context, int var1, int uid) {
        return handleUnimplemented(var1, uid);
    }

    @Override
    public int AddPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int DelPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int SetPlayerGroupVisionType(GroupEventLuaContext context, int[] uids, int[] visionTypeList) {
        return handleUnimplemented(uids, visionTypeList);
    }

    @Override
    public int MoveAvatarByPointArray(GroupEventLuaContext context, int uid, int targetId, LuaTable var3, String var4) {
        return handleUnimplemented(uid, targetId, printTable(var3), var4);
        //TODO implement var3 contains int speed, var4 is a json string
    }

    @Override
    public int MovePlayerToPos(GroupEventLuaContext context, int[] targetUIds, Vector pos, Vector rot, int radius, boolean isSkipUi) {
        logger.warn("[LUA] Call unchecked MovePlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);
        //TODO implement var1 contains int[] uid_list, Position pos, int radius, Position rot
        return TransPlayerToPos(context, targetUIds, pos, rot, radius, isSkipUi, -1); // todo this is probably not a full scene reload
    }

    @Override
    public int TransPlayerToPos(GroupEventLuaContext context, int[] targetUIds, Vector pos, Vector rot, int radius, boolean isSkipUi, int sceneId) {
        logger.warn("[LUA] Call unchecked TransPlayerToPos with {}, {}, {}, {}, {}", targetUIds, pos, rot, radius, isSkipUi);

        var scriptManager = context.getSceneScriptManager();
        if(scriptManager==null){
            return 2;
        }

        var scene = scriptManager.getScene();
        scene.getPlayers().stream().filter(p -> ArrayUtils.contains(targetUIds,p.getUid())).forEach(p -> {
            scene.removePlayer(p);
            scene.addPlayer(p);
            val playerPos = p.getPosition().set(pos);

            // Teleport packet
            p.sendPacket(new PacketPlayerEnterSceneNotify(p, EnterType.ENTER_GOTO, Lua, scene.getId(), playerPos));
        });
        return 0;
    }

    @Override
    public int PlayCutScene(GroupEventLuaContext context, int cutsceneId, int var2) {
        logger.warn("[LUA] Call untested PlayCutScene with {} {}", cutsceneId, var2);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketCutsceneBeginNotify(cutsceneId));
        //TODO implement
        return 0;
    }

    @Override
    public int PlayCutSceneWithParam(GroupEventLuaContext context, int cutsceneId, int var2, LuaTable var3) {
        return handleUnimplemented(cutsceneId, var2, printTable(var3));
    }

    @Override
    public int ScenePlaySound(GroupEventLuaContext context, LuaTable soundInfo) {
        logger.debug("[LUA] Call unimplemented ScenePlaySound with {}", printTable(soundInfo));
        val soundName = soundInfo.optString("sound_name", null);
        val playType = soundInfo.optInt("play_type", 0);
        val isBroadcast = soundInfo.optBoolean("is_broadcast", true);
        val luaPlayPosition = soundInfo.getTable("play_pos");

        val playPosition = luaToPos(luaPlayPosition);
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketScenePlayerSoundNotify(new emu.grasscutter.utils.Position(playPosition), soundName, playType));
        return 0;
    }

    @Override
    public int BeginCameraSceneLook(GroupEventLuaContext context, LuaTable sceneLookParams) {
        logger.debug("[LUA] Call BeginCameraSceneLook with {}", printTable(sceneLookParams));
        val luaLookPos = sceneLookParams.getTable("look_pos");

        val cameraParams = new PacketBeginCameraSceneLookNotify.CameraSceneLookNotify();
        cameraParams.setLookPos(new emu.grasscutter.utils.Position(luaToPos(luaLookPos)));
        if(sceneLookParams.has("duration")) {
            cameraParams.setDuration(sceneLookParams.getFloat("duration"));
        }
        if(sceneLookParams.has("is_force")) {
            cameraParams.setForce(sceneLookParams.getBoolean("is_force"));
        }
        if(sceneLookParams.has("is_allow_input")) {
            cameraParams.setAllowInput(sceneLookParams.getBoolean("is_allow_input"));
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            cameraParams.setSetFollowPos(sceneLookParams.getBoolean("is_set_follow_pos"));
        }
        if (sceneLookParams.has("is_force_walk")) {
            cameraParams.setForceWalk(sceneLookParams.getBoolean("is_force_walk"));
        }
        if (sceneLookParams.has("is_change_play_mode")) {
            cameraParams.setChangePlayMode(sceneLookParams.getBoolean("is_change_play_mode"));
        }
        if(sceneLookParams.has("is_recover_keep_current")) {
            cameraParams.setRecoverKeepCurrent(sceneLookParams.getBoolean("is_recover_keep_current"));
        }
        if (sceneLookParams.has("is_set_screen_xy")) {
            val isSetScreenXY = sceneLookParams.getBoolean("is_set_screen_xy");
            cameraParams.setScreenXY(isSetScreenXY);
            if(isSetScreenXY && sceneLookParams.has("screen_x")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_x"));
            }
            if(isSetScreenXY && sceneLookParams.has("screen_y")) {
                cameraParams.setScreenX(sceneLookParams.getFloat("screen_y"));
            }
        }
        if (sceneLookParams.has("is_set_follow_pos")) {
            val isSetFollowPos = sceneLookParams.getBoolean("is_set_follow_pos");
            cameraParams.setSetFollowPos(isSetFollowPos);
            if(isSetFollowPos && sceneLookParams.has("follow_pos")) {
                val luaFollowPos = sceneLookParams.getTable("follow_pos");
                cameraParams.setFollowPos(ScriptUtils.luaToPos(luaFollowPos));
            }
        }
        if(sceneLookParams.has("is_broadcast")) {
            // TODO cameraParams.setBroadcast(sceneLookParams.getBoolean("is_broadcast"));
        }

        context.getSceneScriptManager().getScene().broadcastPacket(new PacketBeginCameraSceneLookNotify(cameraParams));
        return 0;
    }

    @Override
    public int SetPlayerEyePointStream(GroupEventLuaContext context, int var1, int var2, boolean var3) {
        return handleUnimplemented(var1, var2, var3);
    }

    @Override
    public int ClearPlayerEyePoint(GroupEventLuaContext context, int var1) {
        return handleUnimplemented();
    }

    @Override
    public int ShowReminderRadius(GroupEventLuaContext context, int var1, LuaTable var2, int var3) {
        return handleUnimplemented(var1, printTable(var2), var3);
    }

    @Override
    public int ShowClientGuide(GroupEventLuaContext context, String guideName) {
        logger.debug("[LUA] Call unimplemented ShowClientGuide with {}", guideName);
        if (GameData.getGuideTriggerDataStringMap().get(guideName) != null) {
            // if should handle by open state, dont send packet here
            // not entirely sure what return value is about
            // probably not needing this check statement here since the value comes from
            // the lua script
            return 1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketShowClientGuideNotify(guideName));
        return 0;
    }

    @Override
    public int SetWeatherAreaState(GroupEventLuaContext context, int var1, int var2) {
        logger.warn("[LUA] Call unimplemented SetWeatherAreaState with {} {}", var1, var2);
        if(var2 != 0) {
            return context.getSceneScriptManager().getScene().addWeatherArea(var1) ? 0 : 1;
        } else {
            return context.getSceneScriptManager().getScene().removeWeatherArea(var1) ? 0 : 1;
        }
    }

    @Override
    public int EnterWeatherArea(GroupEventLuaContext context, int weatherAreaId) {
        context.getSceneScriptManager().getScene().getPlayers().forEach(p -> {
            if(p.getWeatherAreaId() != weatherAreaId) p.updateWeather(p.getScene());
        });

        return 0;
    }

    @Override
    public boolean CheckIsInMpMode(GroupEventLuaContext context) {
        logger.debug("[LUA] Call CheckIsInMpMode");
        return context.getSceneScriptManager().getScene().getWorld().isMultiplayer();
    }


    /**
     * TODO properly implement
     * var3 might contain the next point, sometimes is a single int, sometimes multiple ints as array
     * var4 has RouteType route_type, bool turn_mode
     */
    @Override
    public int SetPlatformPointArray(GroupEventLuaContext context, int entityConfigId, int pointArrayId, LuaTable var3, LuaTable var4) {
        logger.warn("[LUA] Call half implemented SetPlatformPointArray with {} {} {} {}", entityConfigId, pointArrayId, printTable(var3), printTable(var4));

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(routeConfig instanceof PointArrayRoute pointArrayRoute) {
            if(pointArrayRoute.getPointArrayId() == pointArrayId){
                return -1;
            }
            pointArrayRoute.setPointArrayId(pointArrayId);
        } else {
            routeConfig = new PointArrayRoute(entityGadget.getRotation(), pointArrayId);
            entityGadget.setRouteConfig(routeConfig);
        }

        val pointIndexList = Arrays.stream(var3.getAsIntArray()).boxed().toList();
        if (!entityGadget.scheduleArrayPoints(pointArrayId, pointIndexList)) {
            return -1;
        }
        context.getSceneScriptManager().getScene().broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int SetPlatformRouteId(GroupEventLuaContext context, int entityConfigId, int routeId) {
        logger.info("[LUA] Call SetPlatformRouteId {} {}", entityConfigId, routeId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(entityConfigId, groupId);
        if(entity == null){
            return 1;
        }
        if(!(entity instanceof EntityGadget entityGadget)){
            return 2; //todo maybe also check the gadget type?
        }

        var routeConfig = entityGadget.getRouteConfig();
        if(routeConfig instanceof ConfigRoute configRoute){
            if(configRoute.getRouteId() == routeId){
                return 0;
            }
            configRoute.setRouteId(routeId);
        } else {
            routeConfig = new ConfigRoute(entityGadget.getRotation().clone(), routeId);
            entityGadget.setRouteConfig(routeConfig);
        }

        routeConfig.setStartSceneTime(scene.getSceneTime());
        routeConfig.setStartIndex(0);
        entityGadget.schedulePlatform();
        scene.broadcastPacket(new PacketPlatformChangeRouteNotify(entityGadget));
        return 0;
    }

    @Override
    public int StartPlatform(GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StartPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.startPlatform() ? 0 : 2;
    }

    @Override
    public int StopPlatform(GroupEventLuaContext context, int configId) {
        logger.info("[LUA] Call StopPlatform {} ", configId);

        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val scene = context.getSceneScriptManager().getScene();
        val entity = scene.getEntityByConfigId(configId, groupId);
        if(!(entity instanceof EntityGadget entityGadget)) {
            return 1;
        }

        return entityGadget.stopPlatform() ? 0 : 2;
    }

    @Override
    public int CreateChannellerSlabCampRewardGadget(GroupEventLuaContext context, int configId) {
        logger.warn("[LUA] Call unimplemented CreateChannellerSlabCampRewardGadget {}", configId);
        var group = context.getCurrentGroup();
        if(group == null){
            return 1;
        }
        createGadget(context.getSceneScriptManager(), configId, group);
        //TODO implement fully
        return 0;
    }

    @Override
    public int AssignPlayerShowTemplateReminder(GroupEventLuaContext context, int var1, LuaTable var2) {
        return handleUnimplemented(var1, printTable(var2));
        //TODO implement var2 contains LuaTable param_uid_vec, LuaTable param_vec int[] uid_vec
    }

    @Override
    public int RevokePlayerShowTemplateReminder(GroupEventLuaContext context, int var1, LuaTable var2) {
        return handleUnimplemented(var1, printTable(var2));
    }

    private static int killGroupEntityWithTable(SceneScriptManager sceneScriptManager, SceneGroup group, LuaTable lists){
        // get targets
        val monsterList = lists.getTable("monsters");
        val gadgetList = lists.getTable("gadgets");
        val monsterSize = monsterList != null ? monsterList.getSize() : 0;
        val gadgetSize = gadgetList != null ? gadgetList.getSize() : 0;

        int[] targets = new int[monsterSize + gadgetSize];
        int targetsIndex = 0;
        if(monsterList != null) {
            for (int i = 1; i <= monsterSize; i++, targetsIndex++) {
                targets[targetsIndex] = monsterList.optInt(i, -1);
            }
        }
        if(gadgetList != null) {
            for (int i = 1; i <= gadgetSize; i++, targetsIndex++) {
                targets[targetsIndex] = gadgetList.optInt(i, -1);
            }
        }

        // kill targets if exists
        for(int cfgId : targets){
            var entity = sceneScriptManager.getScene().getEntityByConfigId(cfgId, group.getGroupInfo().getId());
            if (entity == null || cfgId == 0) {
                continue;
            }
            sceneScriptManager.getScene().killEntity(entity, 0);
        }
        return 0;
    }


    @Override
    public ActivityOpenAndCloseTime GetActivityOpenAndCloseTimeByScheduleId(GroupEventLuaContext context, int scheduleId) {logger.debug("[LUA] Call GetActivityOpenAndCloseTimeByScheduleId with {}", scheduleId);

        val activityConfig = ActivityManager.getScheduleActivityConfigMap().get(scheduleId);

        if(activityConfig == null)
            return null;

        val result = new ActivityOpenAndCloseTime();
        result.setOpenTime(activityConfig.getBeginTime());
        result.setCloseTime(activityConfig.getEndTime());
        return result;
    }

    @Override
    public int GetGameHour(GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getGameTimeHours();
    }


    /**
     * TODO implement
     * @param context
     * @param givingId The id if the giving element found in [GivingData]
     * @param groupId The groupdId of the group containing the gadget
     * @param gadgetCfgId The gadgets target configId
     * @return 0 if success, something else if failed
     */
    @Override
    public int ActiveGadgetItemGiving(GroupEventLuaContext context, int givingId, int groupId, int gadgetCfgId) {
        return handleUnimplemented(givingId, groupId, gadgetCfgId);
    }

    @Override
    public int AddChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int pointsToAdd) {
        return handleUnimplemented(groupId, param2, uid, pointsToAdd);
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    @Override
    public int AddExhibitionAccumulableData(GroupEventLuaContext context, int uid, String param2, int param3) {
        return handleUnimplemented(uid, param2, param3);
    }


    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param exhibitionPlayType
     * @param galleryId
     * @return
     */
    @Override
    public int AddExhibitionAccumulableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, ExhibitionPlayType exhibitionPlayType, int galleryId) {
        return handleUnimplemented(uid, param2, param3, exhibitionPlayType.name(), galleryId);
    }


    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2  probably the name of the data field
     * @param param3
     * @return
     */
    @Override
    public int AddExhibitionReplaceableData(GroupEventLuaContext context, int uid, String param2, int param3) {
        return handleUnimplemented(uid, param2, param3);
    }

    /**
     * TODO implement
     * @param context
     * @param uid
     * @param param2 probably the name of the data field
     * @param param3
     * @param param4 contains the fields "play_type" is part of the enum [ExhibitionPlayType] and "gallery_id"
     * @return
     */
    @Override
    public int AddExhibitionReplaceableDataAfterSuccess(GroupEventLuaContext context, int uid, String param2, int param3, LuaTable param4) {
        return handleUnimplemented(uid, param2, param3, printTable(param4));
    }

    @Override
    public int AddGadgetPlayProgress(GroupEventLuaContext context, int param1, int param2, int progressChange) {
        return handleUnimplemented(param1, param2, progressChange);
    }

    @Override
    public int AddIrodoriChessBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int points) {
        return handleUnimplemented(groupId, param2, points);
    }

    @Override
    public int AddIrodoriChessTowerServerGlobalValue(GroupEventLuaContext context, int groupId, int param2, int param3, int delta) {
        return handleUnimplemented(groupId, param2, param3, delta);
    }

    @Override
    public int AddMechanicusBuildingPoints(GroupEventLuaContext context, int groupId, int param2, int uid, int delta) {
        return handleUnimplemented(groupId, param2, uid, delta);
    }

    @Override
    public int AddRegionRecycleProgress(GroupEventLuaContext context, int regionId, int delta) {
        return handleUnimplemented(regionId, delta);
    }

    @Override
    public int AddRegionSearchProgress(GroupEventLuaContext context, int regionId, int delta) {
        return handleUnimplemented(regionId, delta);
    }

    @Override
    public int AddRegionalPlayVarValue(GroupEventLuaContext context, int uid, int regionId, int delta) {
        return handleUnimplemented(uid, regionId, delta);
    }

    @Override
    public int AddSceneMultiStagePlayUidValue(GroupEventLuaContext context, int groupId, int param2, String param3, int uid, int param5) {
        return handleUnimplemented(groupId, param2, param3, uid, param5);
    }

    @Override
    public int AddScenePlayBattleProgress(GroupEventLuaContext context, int groupId, int progress) {
        return handleUnimplemented(groupId, progress);
    }

    /**
     * TODO implement
     * @param context
     * @param param1Table contains the following fields: param_index:int, param_list:Table, param_uid_list:Table,
     *                    duration:int, target_uid_list:Table
     * @return
     */
    @Override
    public int AssignPlayerUidOpNotify(GroupEventLuaContext context, LuaTable param1Table) {
        return handleUnimplemented(printTable(param1Table));
    }

    @Override
    public int CreateTreasureMapSpotRewardGadget(GroupEventLuaContext context, int gadgetCfgId) {
        return handleUnimplemented(gadgetCfgId);
    }

    /**
     * TODO implement
     */
    @Override
    public int updateBundleMarkShowStateByGroupId(GroupEventLuaContext groupEventLuaContext, int groupId, boolean val2) {
        return handleUnimplemented(groupId, val2);
    }

    @Override
    public int autoPoolMonsterTide(GroupEventLuaContext context, int index, int groupId, int[] monsterPool, int routeId, int[] routePoints, int[] monsterAffix, LuaTable monsterPoolParam) {
        return handleUnimplemented(index, groupId, monsterPool, routeId, routePoints, monsterAffix, printTable(monsterPoolParam));
        //TODO implement monsterPoolParam contains int totalCount, int minCount, int tag, int fillTime, int fillCount, bool isOrdered
    }

    @Override
    public int beginCameraSceneLookWithTemplate(GroupEventLuaContext context, int var1, LuaTable camParam) {
        return handleUnimplemented(var1, printTable(camParam));
        //TODO implement camParam contains int lookConfigId, Vector lookPos, int followType, Vector followPos, boolean isBrodcast, int delay
    }

    @Override
    public boolean checkIsInGroup(GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId) == 0;
    }

    @Override
    public int clearExhibitionReplaceableData(GroupEventLuaContext context, int uid, String key) {
        return handleUnimplemented(uid, key);
    }

    @Override
    public int clearPoolMonsterTide(GroupEventLuaContext context, int groupId, int tideNum) {
        return handleUnimplemented(groupId, tideNum);
    }

    @Override
    public int continueAutoMonster(GroupEventLuaContext context, int groupId, int tideNum) {
        return handleUnimplemented(groupId, tideNum);
    }

    @Override
    public int continueTimeAxis(GroupEventLuaContext context, String key) {
        return handleUnimplemented(key);
    }

    @Override
    public int createFoundation(GroupEventLuaContext context, int[] uidList, int configId, int groupId, int index) {
        return handleUnimplemented(uidList, configId, groupId, index);
    }

    @Override
    public int createFoundations(GroupEventLuaContext context, LuaTable fundationTable, int groupId, int index) {
        return handleUnimplemented(printTable(fundationTable), groupId, index);
    }

    @Override
    public int createGadgetWave(GroupEventLuaContext context, int areaId, int suitId, int offset, Vector boxSize, Vector gadgetSize) {
        return handleUnimplemented(areaId, suitId, offset, boxSize, gadgetSize);
    }

    @Override
    public int createGadgetWithGlobalValue(GroupEventLuaContext context, int configId, LuaTable sgv) {
        return handleUnimplemented(configId, printTable(sgv));
        //TODO implement Sgv contains ["SGV_BDShootType"] or sometimes nothing.
    }

    @Override
    public int createScenePlayGeneralRewardGadget(GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId);
    }

    @Override
    public int endAllTimeAxis(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int endMonsterTide(GroupEventLuaContext context, int groupId, int tideIndex, int endType) {
        return handleUnimplemented(groupId, tideIndex, endType);
    }

    @Override
    public int endPoolMonsterTide(GroupEventLuaContext context, int groupId, int index) {
        return handleUnimplemented(groupId, index);
    }

    @Override
    public int endSceneMultiStagePlay(GroupEventLuaContext context, int playIndex, boolean isSucc) {
        return handleUnimplemented(playIndex, isSucc);
    }

    @Override
    public int endSceneMultiStagePlayStage(GroupEventLuaContext context, int playIndex, String stageName, boolean isSucc) {
        return handleUnimplemented(playIndex, stageName, isSucc);
    }

    @Override
    public int enterCurve(GroupEventLuaContext context, int uid, int curveId, int pointId, LuaTable oceanCurrent) {
        return handleUnimplemented(uid, curveId, pointId, printTable(oceanCurrent));
        //TODO implement oceanCurrent unknown what it is.
    }

    @Override
    public int failScenePlayBattle(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int finishRandTask(GroupEventLuaContext context, int optionId, boolean isSucc) {
        return handleUnimplemented(optionId, isSucc);
    }

    @Override
    public int forbidPlayerRegionVision(GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int getAranaraCollectableCountByTypeAndState(GroupEventLuaContext context, int type, int state) {
        return handleUnimplemented(type, state);
    }

    @Override
    public int getChainLevel(GroupEventLuaContext context, int chainId) {
        return handleUnimplemented(chainId);
    }

    @Override
    public int getExhibitionAccumulableData(GroupEventLuaContext context, int uid, int exhibitionId) {
        return handleUnimplemented(uid, exhibitionId);
    }

    @Override
    public int getExhibitionReplaceableData(GroupEventLuaContext context, int uid, int exhibitionId) {
        return handleUnimplemented(uid, exhibitionId);
    }

    @Override
    public int getGadgetPlayProgress(GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId);
    }

    @Override
    public int getGadgetPlayStageBeginProgress(GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId);
    }

    @Override
    public int getGadgetPlayUidValue(GroupEventLuaContext context, int groupId, int configId, int uid, String name) {
        return handleUnimplemented(groupId, configId, uid, name);
    }

    @Override
    public int getGameTimePassed(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int getGivingItemList(GroupEventLuaContext context, int givingId) {
        return handleUnimplemented(givingId);
    }

    @Override
    public int getGroupAliveMonsterList(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int getGroupLogicStateValue(GroupEventLuaContext context, String sgvName) {
        return handleUnimplemented(sgvName);
    }

    @Override
    public int getOfferingLevel(GroupEventLuaContext context, int offeringId) {
        return handleUnimplemented(offeringId);
    }

    @Override
    public int getPlatformArrayInfoByPointId(GroupEventLuaContext context, int arrayId, int pointId) {
        return handleUnimplemented(arrayId, pointId);
    }

    @Override
    public int getPlatformPointArray(GroupEventLuaContext context, int configId) {
        return handleUnimplemented(configId);
    }

    @Override
    public int getPlayerVehicleType(GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int getRegionalPlayVarValue(GroupEventLuaContext context, int uid, int type) {
        return handleUnimplemented(uid, type);
    }

    @Override
    public int getSceneMultiStagePlayUidValue(GroupEventLuaContext context, int groupId, int index, String name, int uid) {
        return handleUnimplemented(groupId, index, name, uid);
    }

    @Override
    public int getScenePlayBattleHostUid(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int getScenePlayBattleType(GroupEventLuaContext context, int groupId) {
        return handleUnimplemented(groupId);
    }

    @Override
    public int getScenePlayBattleUidValue(GroupEventLuaContext context, int groupId, int uid, String key) {
        return handleUnimplemented(groupId, uid, key);
    }

    @Override
    public int getSceneTimeSeconds(GroupEventLuaContext context) {
        return handleUnimplemented();
    }

    @Override
    public int getSurroundUidList(GroupEventLuaContext context, int configId, int radius) {
        return handleUnimplemented(configId, radius);
    }

    @Override
    public int initSceneMultistagePlay(GroupEventLuaContext context, int index, MultistagePlayType playType, LuaTable paramTable, int[] uidList) {
        return handleUnimplemented(index, playType, paramTable, uidList);
        //TODO implement paramTable contains int[] banList, int rounds, int initBuildingPoints
    }

    @Override
    public int invalidGravenPhotoBundleMark(GroupEventLuaContext context, int groupBundleId) {
        return handleUnimplemented(groupBundleId);
    }

    @Override
    public boolean isPlayerTransmittable(GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid) == 0;
    }

    @Override
    public boolean isWidgetEquipped(GroupEventLuaContext context, int hostUid, int widgetId) {
        return handleUnimplemented(hostUid, widgetId) == 0;
    }

    @Override
    public int markGroupLuaAction(GroupEventLuaContext context, String action, String transaction, LuaTable log) {
        return handleUnimplemented(action, transaction, printTable(log));
        //TODO implement log contains many sting/int key/value pairs
    }

    @Override
    public int modifyClimatePolygonParamTable(GroupEventLuaContext context, int one, LuaTable climateTable) {
        return handleUnimplemented(one, printTable(climateTable));
        //TODO implement climateTable contains int climateType, int meterInheritRatio
    }

    @Override
    public int moveAvatarByPointArrayWithTemplate(GroupEventLuaContext context, int uid, int pointArrayId, int[] routeList, int gadgetState, LuaTable speed) {
        return handleUnimplemented(uid, pointArrayId, routeList, gadgetState, speed);
        //TODO implement speed is sometimes int speed=10 and sometimes {4,60}
    }

    @Override
    public int notifyAllPlayerPerformOperation(GroupEventLuaContext context, int teamEntityId, int type, int effectIndex, Vector hunterPos, Vector hunterRot) {
        return handleUnimplemented(teamEntityId, type, effectIndex, hunterPos, hunterRot);
    }

    @Override
    public int pauseAutoMonsterTide(GroupEventLuaContext context, int groupId, int monsterTideIndex) {
        return handleUnimplemented(groupId, monsterTideIndex);
    }

    @Override
    public int pauseAutoPoolMonsterTide(GroupEventLuaContext context, int groupId, int tideStage) {
        return handleUnimplemented(groupId, tideStage);
    }

    @Override
    public int pauseTimeAxis(GroupEventLuaContext context, String key) {
        return handleUnimplemented(key);
    }

    @Override
    public int prestartScenePlayBattle(GroupEventLuaContext context, LuaTable sceneParam) {
        return handleUnimplemented(printTable(sceneParam));
        //TODO implement sceneParam contains int duration, int startCd, int[] progressStage, int groupId, int mode
    }

    @Override
    public int recieveAllAranaraCollectionByType(GroupEventLuaContext context, int groupId, int type) {
        return handleUnimplemented(groupId, type);
    }

    @Override
    public int resumeAutoPoolMonsterTide(GroupEventLuaContext context, int groupId, int tideStage) {
        return handleUnimplemented(groupId, tideStage);
    }

    @Override
    public int revertPlayerRegionVision(GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int scenePlayBattleUidOp(GroupEventLuaContext context, int groupId, int configId, int[] uidList, int buffType, String paramString, int[] paramList, int[] paramTargetList, int index, int duration) {
        return handleUnimplemented(groupId, configId, uidList, buffType, paramString, paramList, paramTargetList, index, duration);
    }

    @Override
    public int setChainLevel(GroupEventLuaContext context, int chainId, int level, boolean isNotify) {
        return handleUnimplemented(chainId, level, isNotify);
    }

    @Override
    public int setDarkPressureLevel(GroupEventLuaContext context, int darkLevel) {
        return handleUnimplemented(darkLevel);
    }

    @Override
    public int setEnvironmentEffectState(GroupEventLuaContext context, int index, String key, float[] floatParam, int[] intParam) {
        return handleUnimplemented(index, key, floatParam, intParam);
    }

    @Override
    public int setGadgetPlayUidValue(GroupEventLuaContext context, int groupId, int configId, int uid, String key, int value) {
        return handleUnimplemented(groupId, configId, uid, key, value);
    }

    @Override
    public int setLimitOptimization(GroupEventLuaContext context, int uid, boolean isLimitOptimization) {
        return handleUnimplemented(uid, isLimitOptimization);
    }

    @Override
    public int setPlatformRouteIndexToNext(GroupEventLuaContext context, int configId) {
        return handleUnimplemented(configId);
    }

    @Override
    public int setPlayerEyePoint(GroupEventLuaContext context, int configId, int configId2) {
        return handleUnimplemented(configId, configId2);
    }

    @Override
    public int setPlayerEyePointLOD(GroupEventLuaContext context, int configId, int configId2, int lodLevel) {
        return handleUnimplemented(configId, configId2, lodLevel);
    }

    @Override
    public int setPlayerInteractOption(GroupEventLuaContext context, String key) {
        return handleUnimplemented(key);
    }

    @Override
    public int setSceneMultiStagePlayUidValue(GroupEventLuaContext context, int groupId, int index, String tag, int value) {
        return handleUnimplemented(groupId, index, tag, value);
    }

    @Override
    public int setSceneMultiStagePlayValue(GroupEventLuaContext context, int index, String tag, int value, boolean isNotify) {
        return handleUnimplemented(index, tag, value, isNotify);
    }

    @Override
    public int setSceneMultiStagePlayValues(GroupEventLuaContext context, int index, LuaTable paramTable, boolean isNotify) {
        return handleUnimplemented(index, printTable(paramTable), isNotify);
        //TODO implement paramTable contains a lot
    }

    @Override
    public int setScenePlayBattlePlayTeamEntityGadgetId(GroupEventLuaContext context, int groupId, int gadgetId) {
        return handleUnimplemented(groupId, gadgetId);
    }

    @Override
    public int setScenePlayBattleUidValue(GroupEventLuaContext context, int groupId, int uid, String key, int value) {
        return handleUnimplemented(groupId, uid, key, value);
    }

    @Override
    public int setWidgetClientDetectorCoolDown(GroupEventLuaContext context, int configId, boolean isSucc) {
        return handleUnimplemented(configId, isSucc);
    }

    @Override
    public int showClientTutorial(GroupEventLuaContext context, int tutorialId, int[] uidList) {
        return handleUnimplemented(tutorialId, uidList);
    }

    @Override
    public int showCommonPlayerTips(GroupEventLuaContext context, int type, String[] keys) {
        return handleUnimplemented(type, keys);
    }

    @Override
    public int showReminderByUid(GroupEventLuaContext context, int[] uidList, int reminderId) {
        return handleUnimplemented(uidList, reminderId);
    }

    @Override
    public int showTemplateReminder(GroupEventLuaContext context, int reminderId, int[] timerInfo) {
        return handleUnimplemented(reminderId, timerInfo);
    }

    @Override
    public int skipTeyvatTime(GroupEventLuaContext context, int time, int rate) {
        return handleUnimplemented(time, rate);
    }

    @Override
    public int startGadgetPlay(GroupEventLuaContext context, int groupId, int configId) {
        return handleUnimplemented(groupId, configId);
    }

    @Override
    public int startSceneMultiStagePlayStage(GroupEventLuaContext context, int index, int time, String key, LuaTable paramTable) {
        return handleUnimplemented(index, time, key, printTable(paramTable));
        //TODO implement paramTable contains int previewStageIndex, int previewDisplayDuration
    }

    @Override
    public int stopFishing(GroupEventLuaContext context, int uid) {
        return handleUnimplemented(uid);
    }

    @Override
    public int stopReminder(GroupEventLuaContext context, int reminderId) {
        return handleUnimplemented(reminderId);
    }

    @Override
    public int switchSceneEnvAnimal(GroupEventLuaContext context, int animalId) {
        return handleUnimplemented(animalId);
    }

    @Override
    public int tryRecordActivityPushTips(GroupEventLuaContext context, int pushTipId) {
        return handleUnimplemented(pushTipId);
    }

    @Override
    public int updateStakeHomePlayRecord(GroupEventLuaContext context, int[] uidList) {
        return handleUnimplemented(uidList);
    }

}
