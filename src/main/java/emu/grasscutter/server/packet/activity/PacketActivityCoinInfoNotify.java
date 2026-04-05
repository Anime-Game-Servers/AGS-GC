package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityCoinInfoNotify;

import java.util.Map;

public class PacketActivityCoinInfoNotify extends BaseTypedPacket<ActivityCoinInfoNotify> {

	public PacketActivityCoinInfoNotify(int activityId, int scheduleId, Map<Integer, Integer> coinMap) {
		super(new ActivityCoinInfoNotify(coinMap, activityId, scheduleId));
	}
}
