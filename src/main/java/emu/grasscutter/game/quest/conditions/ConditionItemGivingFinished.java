package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_ITEM_GIVING_FINISHED;

@QuestValueCond(QUEST_COND_ITEM_GIVING_FINISHED)
public class ConditionItemGivingFinished extends BaseCondition {
    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        val givingId = condition.getParam()[0];
        val givingGroupId = condition.getParam()[0];

        if (owner.getGivingManager().isCompleted(givingId)) {
            if (givingGroupId == 0) return true;
            val lastGroupId = owner.getGivingManager().getLastGroupId(givingId);
            if (lastGroupId == null) return false;
            return givingGroupId == lastGroupId;
        }
        return false;
    }
}
