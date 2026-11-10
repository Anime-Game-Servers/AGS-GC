package emu.grasscutter.server.packet.scene.entity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MarkEntityInMinMapNotify;

public class PacketMarkEntityInMinMapNotify extends BaseTypedPacket<MarkEntityInMinMapNotify> {

	public PacketMarkEntityInMinMapNotify(int entityId, Vector position, int monsterId) {
		super(new MarkEntityInMinMapNotify(entityId));
        proto.setPosition(position);
        proto.setMonsterId(monsterId);
	}
}
