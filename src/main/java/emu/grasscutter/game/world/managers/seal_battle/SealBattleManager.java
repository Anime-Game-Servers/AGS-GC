package emu.grasscutter.game.world.managers.seal_battle;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.game.world.managers.seal_battle.handlers.EnergySealBattleHandler;
import emu.grasscutter.game.world.managers.seal_battle.handlers.KillMonsterSealBattleHandler;
import emu.grasscutter.server.packet.send.PacketSealBattleBeginNotify;
import emu.grasscutter.server.packet.send.PacketSealBattleEndNotify;
import emu.grasscutter.server.packet.send.PacketSealBattleProgressNotify;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.script_lib.EnergySealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.MonsterSealBattleParams;
import org.anime_game_servers.gi_lua.script_lib.SealBattleParams;
import org.anime_game_servers.multi_proto.gi.messages.scene.seal_battle.SealBattleType;

public class SealBattleManager {
    private final Scene scene;
    @Getter
    private SealBattleParams activeSealBattleParams;
    @Getter
    private int currentProgress = 0;
    private SealBattleHandler handler = null;
    private int startTime = 0;
    @Getter
    private int endTime = 0;
    private int sealGroupId = 0;
    private int sealGadgetCfgId = 0;
    @Getter
    private EntityGadget sealGadget = null;

    public SealBattleManager(Scene scene) {
        this.scene = scene;
    }

    public boolean startSealBattle(int groupId, int gadgetCfgId, SealBattleParams params) {
        if (activeSealBattleParams != null) {
            // Already running
            return false;
        }
        val sealGadgetEntity = scene.getEntityByConfigId(gadgetCfgId, groupId);
        if (!(sealGadgetEntity instanceof EntityGadget)) {
            return false;
        }
        this.sealGadget = (EntityGadget) sealGadgetEntity;
        this.activeSealBattleParams = params;
        this.handler = getSealBattleManager(params);
        if (handler == null) {
            // TODO cleanup and logging
            return false;
        }

        this.startTime = scene.getSceneTime();
        this.endTime = handler.getEndTime(params, startTime);
        this.currentProgress = 0;
        this.sealGroupId = groupId;
        this.sealGadgetCfgId = gadgetCfgId;

        var args = new ScriptArgs(groupId, EventType.EVENT_SEAL_BATTLE_BEGIN)
            .setParam1(sealGadgetCfgId);
        scene.getScriptManager().callEvent(args);
        scene.broadcastPacket(new PacketSealBattleBeginNotify(this));
        scene.broadcastPacket(new PacketSealBattleProgressNotify(this));
        return true;
    }

    private SealBattleHandler getSealBattleManager(SealBattleParams params) {
        return switch (params.getSealBattleType()) {
            case ENERGY_CHARGE -> new EnergySealBattleHandler();
            case KILL_MONSTER -> new KillMonsterSealBattleHandler();
            // TODO stay alive
            default -> null;
        };
    }

    public void onTick() {
        if (activeSealBattleParams == null || handler == null || scene.isPaused()) {
            return;
        }
        handler.onTick(this);
    }

    public void onKill(EntityMonster monster) {
        if (activeSealBattleParams == null || handler == null) {
            return;
        }
        handler.onKill(this, monster);
    }

    public void finishSealBattle(SealBattleResult result) {
        if (activeSealBattleParams == null) {
            return;
        }

        val gadget = sealGadget;
        this.activeSealBattleParams = null;
        this.sealGadget = null;

        var args = new ScriptArgs(sealGroupId, EventType.EVENT_SEAL_BATTLE_END)
            .setParam1(sealGadgetCfgId)
            .setParam2(result.ordinal());
        scene.getScriptManager().callEvent(args);
        scene.broadcastPacket(new PacketSealBattleEndNotify(gadget.getId(), result == SealBattleResult.SUCCESS));
    }

    int getDuration() {
        if (activeSealBattleParams == null) {
            return 0;
        }
        if (activeSealBattleParams instanceof MonsterSealBattleParams monsterParams) {
            return monsterParams.getKill_time();
        }
        if (activeSealBattleParams instanceof EnergySealBattleParams energyParams) {
            return energyParams.getBattleTime();
        }
        return 0;
    }

    public void updateProgress(int newValue) {
        val previousProgress = this.currentProgress;
        this.currentProgress = Math.max(newValue, 0);

        if (currentProgress < previousProgress) {
            var args = new ScriptArgs(sealGroupId, EventType.EVENT_SEAL_BATTLE_PROGRESS_DECREASE)
                .setParam1(sealGadgetCfgId)
                .setParam2(currentProgress);
            scene.getScriptManager().callEvent(args);
        }
        scene.broadcastPacket(new PacketSealBattleProgressNotify(this));
    }

    public int getMaxProgress() {
        if (activeSealBattleParams == null || handler == null) {
            return 0;
        }
        return handler.getMaxProgress(activeSealBattleParams);
    }

    public int getTimeLeft() {
        return Math.max(0, endTime - scene.getSceneTime());
    }

    public boolean isTimeUp() {
        return scene.getSceneTime() >= endTime;
    }

    public int getSealEntityId() {
        if (sealGadget == null) {
            return 0;
        }
        return sealGadget.getId();
    }

    public int getSealRadius() {
        if (activeSealBattleParams == null) {
            return 0;
        }
        return activeSealBattleParams.getRadius();
    }

    public boolean arePlayersInZone() {

        val gadgetPos = sealGadget.getPosition();
        val radius = activeSealBattleParams.getRadius();
        val position = scene.getPlayers().get(0).getPosition();
        var x = Math.pow(gadgetPos.getX() - position.getX(), 2);
        var z = Math.pow(gadgetPos.getZ() - position.getZ(), 2);
        return x + z <= (radius * radius);
    }

    public SealBattleType getProtoSealType() {
        if (activeSealBattleParams == null) {
            return SealBattleType.UNRECOGNISED;
        }
        return switch (activeSealBattleParams.getSealBattleType()) {
            case KILL_MONSTER -> SealBattleType.SEAL_BATTLE_KILL_MONSTER;
            case ENERGY_CHARGE -> SealBattleType.SEAL_BATTLE_ENERGY_CHARGE;
            // TODO stay alive
            default -> SealBattleType.UNRECOGNISED;
        };
    }

    public enum SealBattleResult {
        FAIL,
        SUCCESS
    }

    public interface SealBattleHandler {
        int getMaxProgress(SealBattleParams params);

        int getEndTime(SealBattleParams params, int startTime);

        void onTick(SealBattleManager manager);

        void onKill(SealBattleManager manager, EntityMonster monster);
    }
}
