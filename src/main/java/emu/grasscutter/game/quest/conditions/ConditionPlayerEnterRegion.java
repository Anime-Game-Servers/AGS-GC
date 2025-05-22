package emu.grasscutter.game.quest.conditions;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestAcceptCondition;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.QuestValueCond;
import lombok.val;

import static emu.grasscutter.game.quest.enums.QuestCond.QUEST_COND_PLAYER_ENTER_REGION;

@QuestValueCond(QUEST_COND_PLAYER_ENTER_REGION)
public class ConditionPlayerEnterRegion extends BaseCondition {

    @Override
    public boolean execute(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        if (condition.getParam().length < 2 || params.length != 2) {
            Grasscutter.getLogger().error("Unexpected number of params on QUEST_COND_PLAYER_ENTER_REGION for quest {}", questData.getSubId());
            return false;
        }
        val groupId = condition.getParam()[0];
        val configId = condition.getParam()[1];
        return params[0] == groupId && params[1] == configId;
    }

}
