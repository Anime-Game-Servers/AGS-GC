package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityBannerNotify;

public class PacketActivityBannerNotify extends BaseTypedPacket<ActivityBannerNotify> {

	public PacketActivityBannerNotify(int activityId, int scheduleId) {
		super(new ActivityBannerNotify(activityId, scheduleId));
	}
}
