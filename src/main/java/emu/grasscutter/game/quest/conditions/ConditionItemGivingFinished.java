package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_ITEM_GIVING_FINISHED;

@QuestValueCond(QUEST_COND_ITEM_GIVING_FINISHED)
public class ConditionItemGivingFinished extends BaseCondition {
    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        return condition.getParam()[0] == params[0] && (condition.getParam()[1] == 0 || condition.getParam()[1] == params[1]);
    }
}
