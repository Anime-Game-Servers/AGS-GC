package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.coop.CoopChapter;
import org.anime_game_servers.multi_proto.gi.messages.coop.CoopChapterUpdateNotify;
import java.util.List;

public class PacketCoopChapterUpdateNotify extends BaseTypedPacket<CoopChapterUpdateNotify> {

    public PacketCoopChapterUpdateNotify(List<CoopChapter> coopChapterList) {
        super(new CoopChapterUpdateNotify(coopChapterList));
    }
}
