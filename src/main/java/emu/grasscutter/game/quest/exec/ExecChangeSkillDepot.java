package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.common.quest.SubQuestData.QuestExecParam;
import emu.grasscutter.game.quest.GameQuest;
import emu.grasscutter.game.quest.QuestSystem;
import emu.grasscutter.game.quest.QuestValueExec;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

/**
 * Changes the main avatar's element. First parameter is the skill depot index
 */
@QuestValueExec(QuestExec.QUEST_EXEC_CHANGE_SKILL_DEPOT)
public class ExecChangeSkillDepot extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestExecParam condition, String... paramStr) {
        val targetSkillDepotIndex = Integer.parseInt(paramStr[0]);
        val owner = quest.getOwner();
        val mainAvatar = owner.getAvatars().getAvatarById(owner.getMainCharacterId());

        if (mainAvatar == null) {
            QuestSystem.getLogger().error("Failed to get main avatar for use {}", quest.getOwner().getUid());
            return false;
        }

        QuestSystem.getLogger().info("Changing avatar skill depot index to {} for quest {}", targetSkillDepotIndex, quest.getSubQuestId());
        return mainAvatar.changeSkillDepot(targetSkillDepotIndex);
    }
}
