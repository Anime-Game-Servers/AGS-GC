package emu.grasscutter.server.packet.scene.entity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.UnmarkEntityInMinMapNotify;

public class PacketUnmarkEntityInMinMapNotify extends BaseTypedPacket<UnmarkEntityInMinMapNotify> {

	public PacketUnmarkEntityInMinMapNotify(int entityId) {
		super(new UnmarkEntityInMinMapNotify(entityId));
	}
}
