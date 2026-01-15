package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.SetLimitOptimizationNotify;

public class PacketSetLimitOptimizationNotify extends BaseTypedPacket<SetLimitOptimizationNotify> {

	public PacketSetLimitOptimizationNotify(boolean isActive) {
		super(new SetLimitOptimizationNotify(isActive));
	}
}
