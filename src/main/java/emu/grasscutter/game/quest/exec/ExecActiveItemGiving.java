package emu.grasscutter.game.quest.exec;

import emu.grasscutter.game.quest.GameQuest;

import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.Grasscutter;

@QuestValueExec(QuestExec.QUEST_EXEC_ACTIVE_ITEM_GIVING)
public class ExecActiveItemGiving extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        var givingId = Integer.parseInt(paramStr[0]);
        if (quest.getOwner().getGivingManager().addGiveItemAction(givingId)) {
            Grasscutter.getLogger().debug("Quest {} added give action {}.", quest.getSubQuestId(), givingId);
            return true;
        }
        Grasscutter.getLogger().warn("Quest {} attempted to add give action {} twice.", quest.getSubQuestId(), givingId);
        return false;
    }
}
