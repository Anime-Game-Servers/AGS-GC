package emu.grasscutter.server.packet.ability;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.core.gi.models.Vector;
import org.anime_game_servers.multi_proto.gi.messages.ability.OperateType;
import org.anime_game_servers.multi_proto.gi.messages.ability.PerformOperationNotify;

public class PacketPerformOperationNotify extends BaseTypedPacket<PerformOperationNotify> {

	public PacketPerformOperationNotify(int entityId, int index, int typeIndex, Vector pos, Vector rot) {
		super(new PerformOperationNotify(entityId, index, OperateType.getEntries().get(typeIndex), new Position(pos).toProto(), new Position(rot).toProto()));
	}
}
