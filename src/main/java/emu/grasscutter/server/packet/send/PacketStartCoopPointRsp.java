package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.MainCoop;
import org.anime_game_servers.multi_proto.gi.messages.coop.StartCoopPointRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketStartCoopPointRsp extends BaseTypedPacket<StartCoopPointRsp> {

	public PacketStartCoopPointRsp(int coopPoint, MainCoop startCoop) {
		super(new StartCoopPointRsp(Retcode.RET_SUCC, coopPoint));
		proto.setStart(true);
		proto.setStartMainCoop(startCoop);
	}
}
