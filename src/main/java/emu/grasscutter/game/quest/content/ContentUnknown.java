package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData.QuestContentCondition;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;


@QuestValueContent(QuestContent.QUEST_CONTENT_UNKNOWN)
public class ContentUnknown extends BaseContent {
    @Override
    public int updateProgress(GameQuest quest, int currentProgress, QuestContentCondition condition,
                              String paramStr, int... params) {
        return 0;
    }

    @Override
    public boolean checkProgress(GameQuest quest, QuestContentCondition condition, int currentProgress) {
        if (condition.getType() != null)
            QuestSystem.getLogger().error("Unknown condition {} at {}", condition.getType().name(), quest.getSubQuestId());
        return false;
    }
}
