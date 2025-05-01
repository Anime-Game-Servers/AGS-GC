package emu.grasscutter.data.custom.activity;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class ActivityExtraInfo {
    private int activityId;
    private int duration;
    private int rewardPreview;
    private int specialReward;
    @Nullable private IntList defaultGroups = null;
    @Nullable private IntList defaultWatchers = null;
    @Nullable private List<ActivityStateCondition> startConditions = null;
    @Nullable private List<ActivityStateCondition> activeConditions = null;


    public boolean hasDuration() {
        return duration > 0;
    }
    public boolean hasRewardPreview() {
        return rewardPreview > 0;
    }
    public boolean hasSpecialReward() {
        return specialReward > 0;
    }
    public boolean hasDefaultGroups() {
        return defaultGroups != null && !defaultGroups.isEmpty();
    }
    public boolean hasDefaultWatchers() {
        return defaultWatchers != null && !defaultWatchers.isEmpty();
    }
    public boolean hasStartConditions() {
        return startConditions != null && !startConditions.isEmpty();
    }
    public boolean hasActiveConditions() {
        return activeConditions != null && !activeConditions.isEmpty();
    }


    @Data
    class ActivityStateCondition{
        private ActivityStateCondType type;
        private String param;
    }

    public enum ActivityStateCondType {
        NONE,

        /**
         * Players requires a specific adventure rank
         * param [0]: level the player needs to reach
         */
        PLAYER_LEVEL,

        /**
         * Players requires specific quests to be finished
         * param: list of quest ids required, separated by commas
         */
        FINISH_QUESTS,

        /**
         * Players requires specific main/parent quests to be finished
         * param: list of parent quest ids required, separated by commas
         */
        FINISH_MAIN_QUESTS,
        UNKNOWN;
    }
}
