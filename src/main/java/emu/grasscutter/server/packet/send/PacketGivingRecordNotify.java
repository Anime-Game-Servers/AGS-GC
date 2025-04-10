package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecord;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecordNotify;

import java.util.List;

public class PacketGivingRecordNotify extends BaseTypedPacket<GivingRecordNotify> {
    public PacketGivingRecordNotify(List<GivingRecord> records) {
        super(new GivingRecordNotify());
        proto.setGivingRecordList(records);
    }
}
