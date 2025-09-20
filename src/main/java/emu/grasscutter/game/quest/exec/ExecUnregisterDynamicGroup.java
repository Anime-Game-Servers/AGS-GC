package emu.grasscutter.game.quest.exec;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_UNREGISTER_DYNAMIC_GROUP)
public class ExecUnregisterDynamicGroup extends QuestExecHandler {

    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        if (paramStr.length < 2) {
            Grasscutter.getLogger().error("Not enough parameters for UnregisterDynamicGroup in quest {}", quest.getSubQuestId());
            return false;
        }
        val groupId = Integer.parseInt(paramStr[0]);
        val unknownParam = paramStr[1].isBlank()? 0 : Integer.parseInt(paramStr[1]); //TODO: Goes from 0 to 1, maybe is a boolean. Investigate
        val scene = quest.getOwner().getScene();

        QuestSystem.getLogger().info("Unregistering group {}", groupId);

        if(!scene.unregisterDynamicGroup(groupId)){
            return false;
        }

        //Remove suites if they are registered
        quest.getMainQuest().getQuestGroupSuites().removeIf(gs -> gs.getGroup() == groupId && gs.getScene() == scene.getId());

        QuestSystem.getLogger().debug("Unregistered group {} in scene {}", groupId, scene.getId());

        return true;
    }

}
