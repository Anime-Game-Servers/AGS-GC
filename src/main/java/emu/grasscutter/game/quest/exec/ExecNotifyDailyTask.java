package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_NOTIFY_DAILY_TASK)
public class ExecNotifyDailyTask extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val taskId = Integer.parseInt(paramStr[0]);
        quest.getOwner().getDailyTaskManager().finishTask(taskId);
        return true;
    }
}
