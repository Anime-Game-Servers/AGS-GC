package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskInfo;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskProgressNotify;

public class PacketDailyTaskProgressNotify extends BaseTypedPacket<DailyTaskProgressNotify> {
    public PacketDailyTaskProgressNotify(DailyTaskInfo taskInfo) {
        super(new DailyTaskProgressNotify());
        proto.setInfo(taskInfo);
    }
}
