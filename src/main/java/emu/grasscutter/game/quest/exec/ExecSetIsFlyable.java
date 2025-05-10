package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;

@QuestValueExec(QuestExec.QUEST_EXEC_SET_IS_FLYABLE)
public class ExecSetIsFlyable extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        var canFly = Integer.parseInt(paramStr[0]);
        quest.getOwner().setProperty(PlayerProperty.PROP_IS_FLYABLE, canFly);
        return true;
    }
}
