package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;

@QuestValueExec(QuestExec.QUEST_EXEC_UPDATE_PARENT_QUEST_REWARD_INDEX)
public class ExecUpdateParentQuestRewardIndex extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        int rewardIndex = Integer.parseInt(paramStr[0]);
        quest.getMainQuest().setRewardIndex(rewardIndex);
        return true;
    }
}
