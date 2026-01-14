package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.notify.StopReminderNotify;

public class PacketStopReminderNotify extends BaseTypedPacket<StopReminderNotify> {

	public PacketStopReminderNotify(int reminderId) {
		super(new StopReminderNotify(reminderId));
	}
}
