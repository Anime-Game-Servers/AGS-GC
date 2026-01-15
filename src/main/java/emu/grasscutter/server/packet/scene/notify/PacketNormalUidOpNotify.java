package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.notify.NormalUidOpNotify;

import java.util.List;

public class PacketNormalUidOpNotify extends BaseTypedPacket<NormalUidOpNotify> {

	public PacketNormalUidOpNotify(int duration, int paramIndex, List<Integer> paramList, List<Integer> uidParamList) {
		super(new NormalUidOpNotify(duration, paramIndex, paramList, uidParamList));
	}
}
