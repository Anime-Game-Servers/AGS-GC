package emu.grasscutter.game.world.managers.seal_battle.handlers;

import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.world.managers.seal_battle.SealBattleManager;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.EnergySealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.MonsterSealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.SealBattleParams;

public class KillMonsterSealBattleHandler implements SealBattleManager.SealBattleHandler {
    @Override
    public void onKill(SealBattleManager manager, EntityMonster monster) {
        val sealBattleParams = (MonsterSealBattleParams) manager.getActiveSealBattleParams();
        val maxScore = sealBattleParams.getMaxProgress();
        val currentScore = manager.getCurrentProgress();

        if(monster.getGroupId() != sealBattleParams.getMonster_group_id()){
            return;
        }

        val newScore = Math.min(maxScore, currentScore + 1);

        if(newScore < maxScore) {
            manager.updateProgress(newScore);
        }
        else {
            manager.finishSealBattle(SealBattleManager.SealBattleResult.SUCCESS);
        }
    }

    @Override
    public void onTick(SealBattleManager manager) {
        if(manager.isTimeUp()){
            manager.finishSealBattle(SealBattleManager.SealBattleResult.FAIL);
        }
        manager.updateProgress(manager.getCurrentProgress());
    }

    @Override
    public int getMaxProgress(SealBattleParams params) {
        val monsterParams = (MonsterSealBattleParams) params;
        return monsterParams.getMaxProgress();
    }

    @Override
    public int getEndTime(SealBattleParams params, int startTime) {
        val monsterParams = (MonsterSealBattleParams) params;
        return startTime + monsterParams.getKill_time()*1000;
    }
}
