package emu.grasscutter.game.activity;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.condition.ActivityConditionExecutor;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.DateHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.game_data_models.gi.data.activity.ActivityCondGroupData;
import org.anime_game_servers.game_data_models.gi.data.activity.ActivityData;
import org.anime_game_servers.game_data_models.gi.data.watcher.WatcherTriggerType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class ActivityHandler<PLAYER_DETAIL_DATA> {
    /**
     * Must set before initWatchers
     */
    @Getter ActivityConfigItem activityConfigItem;
    @Getter ActivityData activityData;
    Map<WatcherTriggerType, List<ActivityWatcher>> watchersMap = new HashMap<>();

    public abstract void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo);
    public abstract PLAYER_DETAIL_DATA onInitPlayerActivityData(PlayerActivityData playerActivityData);

    public void onLoadScene(Scene scene, Player player, ActivityConfigItem activityInfo) {
        if(scene.getId() != 3){
            return;
        }
        val activityId = activityInfo.getActivityId();

        val activityExtraInfo = GameData.getActivityExtraInfoMap().get(activityId);
        if(activityExtraInfo == null || !activityExtraInfo.hasDefaultGroups()){
            return;
        }

        activityExtraInfo.getDefaultGroups().forEach(scene::loadDynamicGroup);
    }

    public void initWatchers(Map<WatcherTriggerType, ConstructorAccess<?>> activityWatcherTypeMap){
        activityData = GameData.getActivityDataMap().get(activityConfigItem.getActivityId());

        if(activityData == null || activityData.getWatcherIds() == null){
            return;
        }

        // add watcher to map by id
        activityData.getWatcherIds().forEach(watcherId -> {
            val watcherData = GameData.getActivityWatcherDataMap().get(watcherId.intValue());
            if(watcherData == null || watcherData.getTriggerConfig() == null || watcherData.getTriggerConfig().getTriggerType() == null){
                // todo log
                return;
            }
            val triggerType = watcherData.getTriggerConfig().getTriggerType();
            var watcherType = activityWatcherTypeMap.get(triggerType);
            ActivityWatcher watcher;
            if(watcherType != null){
                watcher = (ActivityWatcher) watcherType.newInstance();
            }else{
                watcher = new DefaultWatcher();
            }

            watcher.setWatcherId(watcherData.getId());
            watcher.setActivityHandler(this);
            watcher.setActivityWatcherData(watcherData);
            watchersMap.computeIfAbsent(triggerType, k -> new ArrayList<>());
            watchersMap.get(triggerType).add(watcher);
        });
    }

    public void initCurrencyHandlers(PlayerActivityData playerActivityData){}

    protected void triggerCondEvents(Player player){
        if(activityData == null || activityData.getCondGroupIds() == null){
            return;
        }
        val questManager = player.getQuestManager();
        activityData.getCondGroupIds().forEach(condGroupId -> {
            val condGroup = GameData.getActivityCondGroupMap().get((int)condGroupId);
            if(condGroup != null)
                condGroup.getCondIds().forEach(condID -> questManager.queueEvent(QuestCond.QUEST_COND_ACTIVITY_COND, condID));
        });
    }

    private List<Integer> getActivityConditions(){
        if(activityData == null || activityData.getCondGroupIds() == null){
            return new ArrayList<>();
        }
        return activityData.getCondGroupIds().stream().map(condGroupId -> GameData.getActivityCondGroupMap().get((int)condGroupId))
            .filter(Objects::nonNull)
            .map(ActivityCondGroupData::getCondIds)
            .flatMap(Collection::stream)
            .toList();
    }

    // TODO handle possible overwrites
    private List<Integer> getMeetConditions(ActivityConditionExecutor conditionExecutor){
        return conditionExecutor.getMeetActivitiesConditions(getActivityConditions());
    }

    private Map<Integer, PlayerActivityData.WatcherInfo> initWatchersDataForPlayer(){
        return watchersMap.values().stream()
            .flatMap(Collection::stream)
            .map(PlayerActivityData.WatcherInfo::init)
            .collect(Collectors.toMap(PlayerActivityData.WatcherInfo::getWatcherId, y -> y));
    }

    public PlayerActivityData initPlayerActivityData(Player player){
        PlayerActivityData playerActivityData = PlayerActivityData.of()
            .activityId(activityConfigItem.getActivityId())
            .uid(player.getUid())
            .watcherInfoMap(initWatchersDataForPlayer())
            .build();

        onInitPlayerActivityData(playerActivityData);
        return playerActivityData;
    }

    public boolean isBannerCondMeet(PlayerActivityData playerActivityData, int scheduleId, ActivityConditionExecutor conditionExecutor){
        val activityExtraInfo = GameData.getActivityExtraInfoMap().get(activityConfigItem.getActivityId());
        if(activityExtraInfo == null || !activityExtraInfo.hasBannerConditionId()){
            return false;
        }
        return getMeetConditions(conditionExecutor).contains(activityExtraInfo.getBannerConditionId());
    }

    public ActivityInfo toProto(PlayerActivityData playerActivityData, ActivityConditionExecutor conditionExecutor){
        val activityId = activityConfigItem.getActivityId();
        val typeId = activityData.getActivityType() != null ? activityData.getActivityType().getId() : 0;

        val proto = new ActivityInfo();
        proto.setActivityId(activityId);
        proto.setActivityType(typeId);
        proto.setScheduleId(activityConfigItem.getScheduleId());
        proto.setBeginTime(DateHelper.getUnixTime(activityConfigItem.getBeginTime()));
        proto.setFirstDayStartTime(DateHelper.getUnixTime(activityConfigItem.getBeginTime()));
        proto.setEndTime(DateHelper.getUnixTime(activityConfigItem.getEndTime()));
        proto.setMeetCondList(getMeetConditions(conditionExecutor));
        proto.setPlayOpenAnim(true);
        proto.setBannerCleared(playerActivityData.isBannerCleared(activityConfigItem.getScheduleId()));

        if (playerActivityData != null){
            proto.setWatcherInfoList(playerActivityData.getAllWatcherInfoList());
        }

        onProtoBuild(playerActivityData, proto);

        return proto;
    }

}
