package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.seal_battle.SealBattleEndNotify;

public class PacketSealBattleEndNotify extends BaseTypedPacket<SealBattleEndNotify> {

    public PacketSealBattleEndNotify(int entityId, boolean isSuccess) {
        super(new SealBattleEndNotify(isSuccess, entityId));
    }
}
