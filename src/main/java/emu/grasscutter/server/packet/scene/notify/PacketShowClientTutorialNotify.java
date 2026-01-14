package emu.grasscutter.server.packet.scene.notify;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.notify.ShowClientTutorialNotify;

public class PacketShowClientTutorialNotify extends BaseTypedPacket<ShowClientTutorialNotify> {

	public PacketShowClientTutorialNotify(int tutorialId) {
		super(new ShowClientTutorialNotify(tutorialId));
	}
}
