package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.GetAllActivatedBargainDataRsp;

import java.util.Collection;

public class PacketGetAllActivatedBargainDataRsp extends BaseTypedPacket<GetAllActivatedBargainDataRsp> {
    public PacketGetAllActivatedBargainDataRsp(Collection<BargainRecord> records) {
        super(new GetAllActivatedBargainDataRsp());
        proto.setRetCode(Retcode.RET_SUCC);
        proto.setSnapshotList(records.stream().map(BargainRecord::toBargainSnapshotProto).toList());
    }
}
