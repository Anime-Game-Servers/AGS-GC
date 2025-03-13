package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.UnlockCoopChapterRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketUnlockCoopChapterRsp extends BaseTypedPacket<UnlockCoopChapterRsp> {

	public PacketUnlockCoopChapterRsp(int chapterId) {
		super(new UnlockCoopChapterRsp(Retcode.RET_SUCC, chapterId));
	}
}
