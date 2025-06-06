package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_DAILY_TASK_START;

@QuestValueCond(QUEST_COND_DAILY_TASK_START)
public class ConditionDailyTaskStart extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val taskId = condition.getParam()[0];
        return owner.getDailyTaskManager().getCurrentTasks().contains(taskId);
    }

}
