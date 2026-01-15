package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.CommonPlayerTipsNotify;

import java.util.List;

public class PacketCommonPlayerTipsNotify extends BaseTypedPacket<CommonPlayerTipsNotify> {

	public PacketCommonPlayerTipsNotify(int type, List<String> textMapList) {
		super(new CommonPlayerTipsNotify(type, textMapList));
	}
}
