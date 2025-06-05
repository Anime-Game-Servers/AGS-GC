package emu.grasscutter.game.managers.dailyQuest;

import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketDailyTaskDataNotify;
import emu.grasscutter.server.packet.send.PacketTaskVarNotify;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.TaskVar;

import java.util.*;

/**
 * Handles logic related to the daily quest system.
 * This system handles daily quest variables, packets, and randomization.
 */
public class DailyTaskManager extends BasePlayerDataManager {
    @Getter private List<Integer> unlockedCities;
    @Getter private int scoreRewardId;
    private Map<Integer, List<Integer>> taskVars;


    public DailyTaskManager(Player player) {
        super(player);
        unlockedCities = new ArrayList<>();
        scoreRewardId = 2006;
        taskVars = new HashMap<>();
    }


    //takes a seed and
    public void randomizeQuests(int seed) {
        //todo: make the seed the current day
        val dailyTaskSeededRandom = new Random(seed);

        //temp. Please remove.
        if (unlockedCities.isEmpty())//todo: proper city unlocking code
            unlockedCities.add(1);

        //todo: pull one quest from each pool associated with the cityIds found in unlockedCities.
    }

    public void onPlayerLogin() {
        this.player.sendPacket(new PacketDailyTaskDataNotify(this.player));
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

    //outputs taskVars as a proto as seen in DailyTaskDataNotify.
    public List<TaskVar> getTaskVars() {
        return this.taskVars.entrySet().stream().map(e -> {
            val taskVar = new TaskVar();
            taskVar.setKey(e.getKey());
            taskVar.setValueList(e.getValue().stream().toList());
            return taskVar;
        }).toList();
    }

    public void setTaskVar(int taskId, int index, int value) {
        val list = this.taskVars.computeIfAbsent(taskId, k -> new ArrayList<>());

        //pad the list of variables with 0 entries in order to reach the index
        while (list.size() <= index) {
            list.add(0);
        }

        list.set(index, value);

        this.player.save();
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

    public void incTaskVar(int taskId, int index, int value) {
        val list = this.taskVars.computeIfAbsent(taskId, k -> new ArrayList<>());

        //pad the list of variables with 0 entries in order to reach the index
        while (list.size() <= index) {
            list.add(0);
        }

        list.set(index, list.get(index) + value);

        this.player.save();
        this.player.sendPacket(new PacketTaskVarNotify(this.player));
    }

    public void decTaskVar(int taskId, int index, int value) {
        incTaskVar(taskId, index, -value);
    }

}
