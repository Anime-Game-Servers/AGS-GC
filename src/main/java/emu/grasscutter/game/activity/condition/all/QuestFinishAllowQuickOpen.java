package emu.grasscutter.game.activity.condition.all;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityCondition;
import emu.grasscutter.game.activity.condition.ActivityConditionBaseHandler;
import emu.grasscutter.game.quest.GameQuest;
import org.anime_game_servers.core.gi.enums.QuestState;

import static emu.grasscutter.game.activity.condition.ActivityConditions.NEW_ACTIVITY_COND_QUEST_FINISH_ALLOW_QUICK_OPEN;

@ActivityCondition(NEW_ACTIVITY_COND_QUEST_FINISH_ALLOW_QUICK_OPEN)
public class QuestFinishAllowQuickOpen extends ActivityConditionBaseHandler {
    @Override
    public boolean execute(PlayerActivityData activityData, ActivityConfigItem activityConfig, int... params) {
        GameQuest quest = activityData
            .getPlayer()
            .getQuestManager()
            .getQuestById(params[0]);

        //todo: QuestFinishAllowQuickOpen will probably need to be changed later.
        // I doubt it's an exact copy of the regular QuestFinished condition
        return quest != null && quest.getState() == QuestState.QUEST_STATE_FINISHED;
    }
}
