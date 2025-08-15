package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.WorldOwnerDailyTaskNotify;

public class PacketWorldOwnerDailyTaskNotify extends BaseTypedPacket<WorldOwnerDailyTaskNotify> {

    public PacketWorldOwnerDailyTaskNotify(Player player) {
        super(new WorldOwnerDailyTaskNotify());
        proto.setFilterCityId(player.getDailyTaskManager().getCityFilter());
        proto.setTaskList(player.getDailyTaskManager().getTaskListProto());
    }
}
