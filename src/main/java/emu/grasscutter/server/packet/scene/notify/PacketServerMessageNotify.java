package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.notify.ServerMessageNotify;

public class PacketServerMessageNotify extends BaseTypedPacket<ServerMessageNotify> {

	public PacketServerMessageNotify(int index) {
		super(new ServerMessageNotify(index));
	}
}
