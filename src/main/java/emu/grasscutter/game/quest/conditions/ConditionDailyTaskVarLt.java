package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_DAILY_TASK_VAR_LT;

@QuestValueCond(QUEST_COND_DAILY_TASK_VAR_LT)
public class ConditionDailyTaskVarLt extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val taskId = condition.getParam()[0];
        val maxValue = condition.getParam()[1];
        var index = 0;
        if (condition.getParam().length >= 3) {
            index = condition.getParam()[2];
        }

        val dailyTaskVarValue = owner.getDailyTaskManager().getTaskVar(taskId, index);
        QuestSystem.getLogger().debug("dailyTaskVar {} {} : {}", taskId, maxValue, dailyTaskVarValue);
        return dailyTaskVarValue > maxValue;
    }

}
