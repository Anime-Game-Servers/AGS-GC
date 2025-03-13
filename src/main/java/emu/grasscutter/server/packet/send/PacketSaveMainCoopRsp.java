package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.SaveMainCoopRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

import java.util.List;

public class PacketSaveMainCoopRsp extends BaseTypedPacket<SaveMainCoopRsp> {

    public PacketSaveMainCoopRsp(int id, List<Integer> savePointId) {
        super(new SaveMainCoopRsp(Retcode.RET_SUCC, id, savePointId));
    }
}
