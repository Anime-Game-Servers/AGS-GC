package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

import static emu.grasscutter.utils.Utils.random;

@QuestValueExec(QuestExec.QUEST_EXEC_REFRESH_GROUP_SUITE_RANDOM)
public class ExecRefreshGroupSuiteRandom extends QuestExecHandler {

    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val sceneId = Integer.parseInt(paramStr[0]);
        val entries = paramStr[1].split(";");
        val scriptManager = quest.getOwner().getWorld().getSceneById(sceneId).getScriptManager();

        boolean result = true;
        for (var entry : entries) {
            val entryArray = entry.split(",");
            val groupId = Integer.parseInt(entryArray[0]);
            val randomSuiteIndex = random.nextInt(1, entryArray.length);
            val suiteId = Integer.parseInt(entryArray[randomSuiteIndex]);

            if (!scriptManager.refreshGroupSuite(groupId, suiteId, quest)) {
                result = false;
            }
        }

        return result;
    }

}
