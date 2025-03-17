package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.SetCoopChapterViewedRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketSetCoopChapterViewedRsp extends BaseTypedPacket<SetCoopChapterViewedRsp> {

	public PacketSetCoopChapterViewedRsp(int chapterId) {
		super(new SetCoopChapterViewedRsp(Retcode.RET_SUCC, chapterId));
	}
}
