package emu.grasscutter.game.quest.content;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;
import lombok.val;

@QuestValueContent(QuestContent.QUEST_CONTENT_GADGET_STATE_CHANGE)
public class ContentGadgetStateChange extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type, String paramStr, int... params) {
        if (condition.getParam().length < 2 || params.length != 2) {
            Grasscutter.getLogger().error("Unexpected number of params on QUEST_CONTENT_GADGET_STATE_CHANGE for quest {}", questData.getSubId());
            return false;
        }
        val groupId = condition.getParam()[0];
        val configId = condition.getParam()[1];
        val gadgetState = condition.getParamString();

        return groupId == params[0] && configId == params[1] && paramStr.equals(gadgetState);
    }
}
