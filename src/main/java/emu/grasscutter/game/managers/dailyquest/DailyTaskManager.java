package emu.grasscutter.game.managers.dailyquest;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DailyTaskData;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.server.packet.send.PacketDailyTaskDataNotify;
import emu.grasscutter.server.packet.send.PacketDailyTaskProgressNotify;
import emu.grasscutter.server.packet.send.PacketDailyTaskUnlockedCitiesNotify;
import emu.grasscutter.server.packet.send.PacketTaskVarNotify;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskInfo;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.TaskVar;

import java.util.*;

/**
 * Handles logic related to the daily quest system.
 * This system handles daily quest variables, packets, and randomization.
 * TODO:
 *  Implement commission rewards for finishing all tasks in a day. (What triggers it?)
 *  Randomization should choose only one task from each pool
 *  the pools should prefer the dailies found in CityTaskOpenExcelConfigData.json first.
 *  Dailies have a whole questTrigger-like system for linking them. See DailyTaskData and DailyTaskExcelConfig
 *  Removing old currentTasks from the quest list is hacky and requires a relog to not look glitchy. Is this the best way?
 */
@Entity
public class DailyTaskManager extends BasePlayerDataManager {
    @Getter private List<Integer> currentTasks;
    @Getter private List<Integer> finishedCurrentTasks;
    @Getter private List<Integer> unlockedCities;
    @Getter @Setter private int cityFilter;
    @Getter private int taskLevel;
    @Getter @Setter private int legendaryKeyDailyTasks;
    private Map<Integer, List<Integer>> taskVars;

    /**
     * Morphia use only.
     * Do not use
     */
    public DailyTaskManager() {
        super();
        currentTasks = new ArrayList<>();
        finishedCurrentTasks = new ArrayList<>();
        unlockedCities = new ArrayList<>();
        taskLevel = 1;
        cityFilter = 0;
        legendaryKeyDailyTasks = 0;
        taskVars = new HashMap<>();
    }

    public DailyTaskManager(Player player) {
        super(player);
        currentTasks = new ArrayList<>();
        finishedCurrentTasks = new ArrayList<>();
        unlockedCities = new ArrayList<>();
        taskLevel = 1;
        cityFilter = 0;
        legendaryKeyDailyTasks = 0;
        taskVars = new HashMap<>();
    }

    /**
     * Updates the task level to match the player's level.
     * See DailyTaskLevelExcelConfigData.json
     */
    public void updateTaskLevel() {
        this.taskLevel = 1 + ((this.player.getLevel() - 1) / 5);
    }

    /**
     * Changes the daily tasks into new tasks.
     * Cleans up the old tasks.
     */
    public void randomizeTasks() {
        //clear finished tasks
        finishedCurrentTasks.clear();

        //hack: remove old currentTasks from the quest list
        this.player.getQuestManager().getMainQuests().values().forEach(mQuest ->
            mQuest.getChildQuests().values().forEach(sQuest ->
                sQuest.getQuestData().getAcceptCond().stream()
                    .filter(cond ->
                        cond.getType() == QuestCond.QUEST_COND_DAILY_TASK_START && currentTasks.contains(cond.getParam()[0]))
                    .forEach(cond -> mQuest.delete())
            )
        );

        //filter tasks
        var taskList = new ArrayList<>(GameData.getDailyTaskDataMap().values().stream()
            .filter(task -> cityFilter == 0 || task.getCityId() == cityFilter)
            .filter(task -> unlockedCities.contains(task.getCityId())).toList());

        //shuffle tasks
        Collections.shuffle(taskList);

        //limit tasks to at most 4 tasks
        if (taskList.size() >= 4) {
            this.currentTasks = taskList.subList(0, 4).stream().map(DailyTaskData::getId).toList();
        } else {
            this.currentTasks = taskList.stream().map(DailyTaskData::getId).toList();
        }

        //Start Tasks
        this.currentTasks.forEach(task ->
            this.player.getQuestManager().queueEvent(QuestCond.QUEST_COND_DAILY_TASK_START, task));
    }

    /**
     * Sends DailyTaskDataNotify and TaskVarNotify.
     * These happen on player login.
     */
    public void onPlayerLogin() {
        this.player.sendPacket(new PacketDailyTaskDataNotify(this.player));
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

    /**
     * Returns the scorePreviewRewardId found in DailyTaskLevelExcelConfigData for the player's current task level
     */
    public int getScoreRewardId() {
        return GameData.getDailyTaskLevelDataMap().get(this.taskLevel).getScorePreviewRewardId();
    }

    /**
     * Checks if a given questId should unlock daily tasks for any cities.
     *
     * @param subQuestId The quest that should be checked against CityTaskOpenExcelConfigData.
     */
    public void checkForCityUnlock(int subQuestId) {
        GameData.getCityTaskOpenDataMap().values().forEach(city -> {
            if (city.getQuestId() == subQuestId && !unlockedCities.contains(city.getCityId())) {
                unlockedCities.add(city.getCityId());
                this.player.sendPacket(new PacketDailyTaskUnlockedCitiesNotify(this.player));
            }
        });
    }

    /**
     * Outputs taskVars as a proto as seen in DailyTaskDataNotify.
     *
     * @return A list of TaskVar protos with the current task vars.
     */
    public List<TaskVar> getTaskVarsProto() {
        return this.taskVars.entrySet().stream().map(e -> {
            val taskVar = new TaskVar();
            taskVar.setKey(e.getKey());
            taskVar.setValueList(e.getValue().stream().toList());
            return taskVar;
        }).toList();
    }

    /**
     * Outputs currentTasks as a proto as seen in WorldOwnerDailyTaskNotify.
     *
     * @return A list of DailyTaskInfo protos with the current task info.
     */
    public List<DailyTaskInfo> getTaskListProto() {
        return this.currentTasks.stream().map(this::getTaskInfoProto).toList();
    }

    /**
     * Converts a taskId into a DailyTaskInfo proto.
     * Checks if it is finished or not.
     *
     * @param taskId The taskId that you want to turn into a proto.
     * @return A list of DailyTaskInfo proto with the task info.
     */
    public DailyTaskInfo getTaskInfoProto(int taskId) {
        val dailyTaskInfo = new DailyTaskInfo();
        dailyTaskInfo.setDailyTaskId(taskId);
        val taskData = GameData.getDailyTaskDataMap().get(taskId);
        dailyTaskInfo.setFinishProgress(taskData.getFinishProgress());
        val rewardId = GameData.getDailyTaskRewardDataMap()
            .get(taskData.getTaskRewardId())
            .getDropVec()
            .get(taskLevel - 1) //adjust for zero index
            .getPreviewRewardId();
        dailyTaskInfo.setRewardId(rewardId);
        if (finishedCurrentTasks.contains(taskId)) {
            dailyTaskInfo.setProgress(dailyTaskInfo.getFinishProgress());
            dailyTaskInfo.setFinished(true);
        }
        return dailyTaskInfo;
    }

    /**
     * Marks a taskId as having been finished today.
     * Sends a DailyTaskProgressNotify and updates the PROP_PLAYER_LEGENDARY_DAILY_TASK_NUM player property.
     *
     * @param taskId The taskId that should be marked finished.
     */
    public void finishTask(int taskId) {
        if (!finishedCurrentTasks.contains(taskId)) {
            finishedCurrentTasks.add(taskId);
            legendaryKeyDailyTasks += 1;
        }

        this.player.sendPacket(new PacketDailyTaskProgressNotify(getTaskInfoProto(taskId)));
        this.player.setProperty(PlayerProperty.PROP_PLAYER_LEGENDARY_DAILY_TASK_NUM, legendaryKeyDailyTasks);
    }

    /**
     * Sets a task var to a value.
     *
     * @param taskId The taskId of the task var.
     * @param index  The index for the value.
     * @param value  The value of the task var.
     */
    public void setTaskVar(int taskId, int index, int value) {
        getVarList(taskId, index).set(index, value);
        triggerTaskVarAction(taskId, index, value);
    }

    /**
     * Adds to the value of a task var and stores the result.
     *
     * @param taskId The taskId of the task var.
     * @param index  The index for the value.
     * @param value  The amount to add to the value of the task var.
     */
    public void incTaskVar(int taskId, int index, int value) {
        val oldValue = getTaskVar(taskId, taskId);
        getVarList(taskId, index).set(index, oldValue + value);
        triggerTaskVarAction(taskId, index, oldValue + value);
    }

    /**
     * Subtracts from the value of a task var and stores the result.
     *
     * @param taskId The taskId of the task var.
     * @param index  The index for the value.
     * @param value  The amount to remove from the value of the task var.
     */
    public void decTaskVar(int taskId, int index, int value) {
        incTaskVar(taskId, index, -value);
    }

    /**
     * Returns the value of the task var with a given taskId/index.
     *
     * @param taskId The taskId of the task var.
     * @param index  The index for the value.
     * @return The value of the task var with a given taskId/index.
     */
    public int getTaskVar(int taskId, int index) {
        return getVarList(taskId, index).get(index);
    }

    /**
     * Returns a list of task var with a given taskId.
     * Pads the list up to the given index.
     *
     * @param taskId The taskId of the task var.
     * @param index  The padding needed to get the variable you want.
     * @return A list of task var with a given taskId padded to the given index.
     */
    private List<Integer> getVarList(int taskId, int index) {
        val list = this.taskVars.computeIfAbsent(taskId, k -> new ArrayList<>());

        //pad the list of variables with 0 entries in order to reach the index
        while (list.size() <= index) {
            list.add(0);
        }

        return list;
    }

    /**
     * Does the quest triggers and packets common to all task var operations.
     *
     * @param taskId The taskId of the task var.
     * @param index  The index for the value.
     * @param value  The value of the task var.
     */
    private void triggerTaskVarAction(int taskId, int index, int value) {
        this.player.save();
        var questManager = this.player.getQuestManager();
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_EQ, taskId, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_GT, taskId, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_LT, taskId, index, value);
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

}
