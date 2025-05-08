package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.GetBargainDataRsp;


public class PacketGetBargainDataRsp extends BaseTypedPacket<GetBargainDataRsp> {
    public PacketGetBargainDataRsp(Retcode retcode) {
        super(new GetBargainDataRsp());
        proto.setRetCode(retcode);
    }

    public PacketGetBargainDataRsp(BargainRecord bargainRecord) {
        super(new GetBargainDataRsp());
        proto.setRetCode(Retcode.RET_SUCC);
        proto.setBargainId(bargainRecord.getBargainId());
        proto.setSnapshot(bargainRecord.toBargainSnapshotProto());
    }
}
