package emu.grasscutter.game.quest.content;

import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.quest.QuestValueContent;
import emu.grasscutter.game.quest.enums.QuestContent;

import static emu.grasscutter.game.quest.enums.QuestContent.QUEST_CONTENT_TRIGGER_FIRE;

@QuestValueContent(QUEST_CONTENT_TRIGGER_FIRE)
public class ContentTriggerFire extends BaseContent {
    @Override
    public boolean isEvent(SubQuestData questData, SubQuestData.QuestContentCondition condition, QuestContent type, String paramStr, int... params) {
        // this is trigger by entering a scene region
        // How it works: a Lua function (e.g. `condition_EVENT_ENTER_REGION_84`) is invoked
        // if it returns true, this event should be triggered for some on-going quest
        // NOTE: The returned value is already checked elsewhere
        return condition.getType() == type;
    }
}
