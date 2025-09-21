package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.dragonspine.DragonSpineCoinChangeNotify;

public class PacketDragonspineCoinChangeNotify extends BaseTypedPacket<DragonSpineCoinChangeNotify> {

	public PacketDragonspineCoinChangeNotify(int schedule, int glimmeringEssence, int warmEssence, int miraculousEssence) {
		super(new DragonSpineCoinChangeNotify(schedule, glimmeringEssence, warmEssence, miraculousEssence));
	}
}
