package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.*;
import java.util.List;

public class PacketMainCoopUpdateNotify extends BaseTypedPacket<MainCoopUpdateNotify> {

    public PacketMainCoopUpdateNotify(List<MainCoop> mainCoops) {
        super(new MainCoopUpdateNotify(mainCoops.stream().filter(x -> x.getId() != 0).toList()));
    }
}
