package emu.grasscutter.game.world.managers.seal_battle.handlers;

import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.world.managers.seal_battle.SealBattleManager;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.EnergySealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.SealBattleParams;

public class EnergySealBattleHandler implements SealBattleManager.SealBattleHandler {
    @Override
    public void onKill(SealBattleManager manager, EntityMonster monster) {
        val sealBattleParams = (EnergySealBattleParams) manager.getActiveSealBattleParams();

        if (monster.getGroupId() != sealBattleParams.getMonsterGroupId()) {
            return;
        }

        var score = monster.getSpawnConfig().getKillScore();
        if (score == 0) {
            score = sealBattleParams.getDefaultKillCharge();
        }
        checkAndHandleScore(manager, sealBattleParams, score);
    }

    @Override
    public void onTick(SealBattleManager manager) {
        if (manager.isTimeUp()) {
            manager.finishSealBattle(SealBattleManager.SealBattleResult.FAIL);
            return;
        }

        val sealBattleParams = (EnergySealBattleParams) manager.getActiveSealBattleParams();

        var diff = 0;
        if (manager.arePlayersInZone()) {
            diff = sealBattleParams.getAutoCharge();
        } else {
            diff = -sealBattleParams.getAutoDecline();
        }

        checkAndHandleScore(manager, sealBattleParams, diff);
    }

    private void checkAndHandleScore(SealBattleManager manager, EnergySealBattleParams params, int diff) {

        val currentScore = manager.getCurrentProgress();
        val maxScore = params.getMaxEnergy();
        val newScore = Math.min(maxScore, currentScore + diff);

        if (newScore < maxScore) {
            manager.updateProgress(newScore);
        } else {
            manager.finishSealBattle(SealBattleManager.SealBattleResult.SUCCESS);
        }
    }

    @Override
    public int getMaxProgress(SealBattleParams params) {
        val energyParams = (EnergySealBattleParams) params;
        return energyParams.getMaxEnergy();
    }

    @Override
    public int getEndTime(SealBattleParams params, int startTime) {
        val energyParams = (EnergySealBattleParams) params;
        return startTime + energyParams.getBattleTime() * 1000;
    }
}
