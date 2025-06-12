package emu.grasscutter.game.managers.dailyquest;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DailyTaskData;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.server.packet.send.PacketDailyTaskDataNotify;
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
 */
@Entity
public class DailyTaskManager extends BasePlayerDataManager {
    @Getter private List<Integer> currentTasks;
    @Getter private List<Integer> unlockedCities;
    @Getter @Setter private int cityFilter;
    @Getter private int taskLevel;
    private Map<Integer, List<Integer>> taskVars;

    // For Morphia Only
    public DailyTaskManager() {
        super();
        currentTasks = new ArrayList<>();
        unlockedCities = new ArrayList<>();
        taskLevel = 1;
        cityFilter = 0;
        taskVars = new HashMap<>();
    }

    public DailyTaskManager(Player player) {
        super(player);
        currentTasks = new ArrayList<>();
        unlockedCities = new ArrayList<>();
        taskLevel = 1;
        cityFilter = 0;
        taskVars = new HashMap<>();
    }

    public void updateTaskLevel() {
        this.taskLevel = 1 + ((this.player.getLevel() - 1) / 5); //see DailyTaskLevelExcelConfigData.json
    }

    public void randomizeTasks() {
        //todo: proper city unlocking code via CityTaskOpenExcelConfigData.json
        if (unlockedCities.isEmpty())
            unlockedCities.add(1);

        //filter tasks
        var taskList = new ArrayList<>(GameData.getDailyTaskDataMap().values().stream()
            .filter(task -> cityFilter == 0 || task.getCityId() == cityFilter)
            .filter(task -> unlockedCities.contains(task.getCityId())).toList());

        //shuffle tasks
        Collections.shuffle(taskList);

        //limit tasks to at most 5 tasks
        if (taskList.size() >= 5) {
            this.currentTasks = taskList.subList(0, 5).stream().map(DailyTaskData::getId).toList();
        } else {
            this.currentTasks = taskList.stream().map(DailyTaskData::getId).toList();
        }

        //Start Tasks
        this.currentTasks.forEach(task ->
            this.player.getQuestManager().queueEvent(QuestCond.QUEST_COND_DAILY_TASK_START, task));
    }

    public void onPlayerLogin() {
        updateTaskLevel();
        this.player.sendPacket(new PacketDailyTaskDataNotify(this.player));
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

    public int getScoreRewardId() {
        return GameData.getDailyTaskLevelDataMap().get(this.taskLevel).getScorePreviewRewardId();
    }

    //outputs taskVars as a proto as seen in DailyTaskDataNotify.
    public List<TaskVar> getTaskVarsProto() {
        return this.taskVars.entrySet().stream().map(e -> {
            val taskVar = new TaskVar();
            taskVar.setKey(e.getKey());
            taskVar.setValueList(e.getValue().stream().toList());
            return taskVar;
        }).toList();
    }

    //outputs currentTasks as a proto as seen in WorldOwnerDailyTaskNotify.
    public List<DailyTaskInfo> getTaskListProto() {
        return this.currentTasks.stream().map(task -> {
            val dailyTaskInfo = new DailyTaskInfo();
            dailyTaskInfo.setDailyTaskId(task);
            val taskData = GameData.getDailyTaskDataMap().get((int) task);
            dailyTaskInfo.setFinishProgress(taskData.getFinishProgress());
            val rewardId = GameData.getDailyTaskRewardDataMap()
                .get(taskData.getTaskRewardId())
                .getDropVec()
                .get(taskLevel - 1) //adjust for zero index
                .getPreviewRewardId();
            dailyTaskInfo.setRewardId(rewardId);
            return dailyTaskInfo;
        }).toList();
    }

    public void setTaskVar(int taskId, int index, int value) {
        getVarList(taskId, index).set(index, value);
        triggerTaskVarAction(taskId, index, value);
    }

    public void incTaskVar(int taskId, int index, int value) {
        val oldValue = getTaskVar(taskId, taskId);
        getVarList(taskId, index).set(index, oldValue + value);
        triggerTaskVarAction(taskId, index, oldValue + value);
    }

    public void decTaskVar(int taskId, int index, int value) {
        incTaskVar(taskId, index, -value);
    }

    public int getTaskVar(int taskId, int index) {
        return getVarList(taskId, index).get(index);
    }

    private List<Integer> getVarList(int taskId, int index) {
        val list = this.taskVars.computeIfAbsent(taskId, k -> new ArrayList<>());

        //pad the list of variables with 0 entries in order to reach the index
        while (list.size() <= index) {
            list.add(0);

        }

        return list;
    }

    private void triggerTaskVarAction(int taskId, int index, int value) {
        this.player.save();
        var questManager = this.player.getQuestManager();
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_EQ, taskId, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_GT, taskId, index, value);
        questManager.queueEvent(QuestCond.QUEST_COND_DAILY_TASK_VAR_LT, taskId, index, value);
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

}
