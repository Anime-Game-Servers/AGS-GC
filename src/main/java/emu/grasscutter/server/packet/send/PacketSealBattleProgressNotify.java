package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.managers.seal_battle.SealBattleManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.seal_battle.SealBattleProgressNotify;

public class PacketSealBattleProgressNotify extends BaseTypedPacket<SealBattleProgressNotify> {

    public PacketSealBattleProgressNotify(SealBattleManager sealBattleManager) {
        super(new SealBattleProgressNotify(
            (int) ((System.currentTimeMillis() + sealBattleManager.getTimeLeft()) / 1000), // workaround to allow pausing
            sealBattleManager.getMaxProgress(),
            sealBattleManager.getCurrentProgress(),
            sealBattleManager.getSealEntityId(),
            sealBattleManager.getSealRadius()
        ));
    }
}
