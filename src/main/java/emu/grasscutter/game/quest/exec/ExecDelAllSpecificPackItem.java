package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_DEL_ALL_SPECIFIC_PACK_ITEM)
public class ExecDelAllSpecificPackItem extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val items = paramStr[0].split(",");
        boolean success = true;
        for (var itemIdString : items) {
            val itemId = Integer.parseInt(itemIdString);
            if (!quest.getOwner().getInventory().removeItemById(itemId)) {
                success = false;
            }
        }
        return success;
    }
}
