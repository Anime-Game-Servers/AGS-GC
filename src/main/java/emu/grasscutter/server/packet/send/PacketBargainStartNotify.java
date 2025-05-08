package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainStartNotify;

public class PacketBargainStartNotify extends BaseTypedPacket<BargainStartNotify> {
    public PacketBargainStartNotify(BargainRecord bargainRecord) {
        super(new BargainStartNotify());
        proto.setBargainId(bargainRecord.getBargainId());
        proto.setSnapshot(bargainRecord.toBargainSnapshotProto());
    }
}
