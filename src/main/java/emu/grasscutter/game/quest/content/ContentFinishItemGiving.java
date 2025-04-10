package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

@QuestValueContent(QuestContent.QUEST_CONTENT_FINISH_ITEM_GIVING)
public class ContentFinishItemGiving extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, QuestContentCondition condition, QuestContent type, String paramStr, int... params) {
        return condition.getParam()[0] == params[0] && (condition.getParam()[1] == 0 || condition.getParam()[1] == params[1]);
    }
}
