package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainStartNotify;

public class PacketBargainStartNotify extends BaseTypedPacket<BargainStartNotify> {
    public PacketBargainStartNotify(BargainRecord record) {
        super(new BargainStartNotify());
        proto.setBargainId(record.getBargainId());
        proto.setSnapshot(record.toBargainSnapshotProto());
    }
}
