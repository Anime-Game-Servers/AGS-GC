package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.CancelCoopTaskRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketCancelCoopTaskRsp extends BaseTypedPacket<CancelCoopTaskRsp> {

	public PacketCancelCoopTaskRsp(int chapterId) {
		super(new CancelCoopTaskRsp(Retcode.RET_SUCC, chapterId));
	}
}
