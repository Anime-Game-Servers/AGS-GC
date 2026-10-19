package emu.grasscutter.server.packet.activity.aster;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.AsterLargeInfoNotify;

public class PacketAsterLargeInfoNotify extends BaseTypedPacket<AsterLargeInfoNotify> {

	public PacketAsterLargeInfoNotify(AsterLargeDetailInfo activityInfo) {
		super(new AsterLargeInfoNotify(activityInfo));
	}
}
