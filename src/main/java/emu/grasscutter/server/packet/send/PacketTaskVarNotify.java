package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.TaskVarNotify;

public class PacketTaskVarNotify extends BaseTypedPacket<TaskVarNotify> {

    public PacketTaskVarNotify(Player player) {
        super(new TaskVarNotify());
        proto.setTaskVarList(player.getDailyTaskManager().getTaskVarsProto());
    }
}
