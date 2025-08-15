package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_DAILY_TASK_VAR_EQ;

@QuestValueCond(QUEST_COND_DAILY_TASK_VAR_EQ)
public class ConditionDailyTaskVarEq extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        if (condition.getParam().length < 3) {
            QuestSystem.getLogger().warn("Resources are missing implied params in quest {}", questData.getSubId());
            return false;
        }
        val taskId = condition.getParam()[0];
        val index = condition.getParam()[1];
        val targetValue = condition.getParam()[2];

        val dailyTaskVarValue = owner.getDailyTaskManager().getTaskVar(taskId, index);
        QuestSystem.getLogger().debug("dailyTaskVar {} {} : {}", taskId, targetValue, dailyTaskVarValue);
        return dailyTaskVarValue == targetValue;
    }

}
