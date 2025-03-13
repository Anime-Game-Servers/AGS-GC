package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.FinishMainCoopRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketFinishMainCoopRsp extends BaseTypedPacket<FinishMainCoopRsp> {

    public PacketFinishMainCoopRsp(int id, int endingSavePointId) {
        super(new FinishMainCoopRsp(Retcode.RET_SUCC, endingSavePointId, id));
    }
}
