package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_SET_DAILY_TASK_VAR)
public class ExecSetDailyTaskVar extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val taskId = Integer.parseInt(paramStr[0]);
        val index = Integer.parseInt(paramStr[1]);
        val value = Integer.parseInt(paramStr[2]);

        quest.getOwner().getDailyTaskManager().setTaskVar(taskId, index, value);
        return true;
    }
}
