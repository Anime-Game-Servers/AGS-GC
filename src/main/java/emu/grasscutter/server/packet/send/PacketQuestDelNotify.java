package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.child.QuestDelNotify;

public class PacketQuestDelNotify extends BaseTypedPacket<QuestDelNotify> {

    public PacketQuestDelNotify(int questId) {
        super(new QuestDelNotify(questId));
    }
}
