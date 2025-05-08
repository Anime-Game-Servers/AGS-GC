package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainTerminateNotify;

public class PacketBargainTerminateNotify extends BaseTypedPacket<BargainTerminateNotify> {
    public PacketBargainTerminateNotify(int bargainId) {
        super(new BargainTerminateNotify());
        proto.setBargainId(bargainId);
    }
}
