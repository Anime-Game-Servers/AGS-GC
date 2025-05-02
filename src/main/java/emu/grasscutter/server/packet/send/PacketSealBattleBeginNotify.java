package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.managers.seal_battle.SealBattleManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.seal_battle.SealBattleBeginNotify;

public class PacketSealBattleBeginNotify extends BaseTypedPacket<SealBattleBeginNotify> {

    public PacketSealBattleBeginNotify(SealBattleManager sealBattleManager) {
        super(new SealBattleBeginNotify(
            sealBattleManager.getProtoSealType(),
            sealBattleManager.getSealEntityId(),
            sealBattleManager.getMaxProgress(),
            sealBattleManager.getSealRadius()
        ));
    }
}
