package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskDataNotify;

public class PacketDailyTaskDataNotify extends BaseTypedPacket<DailyTaskDataNotify> {

    public PacketDailyTaskDataNotify(Player player) {
        super(new DailyTaskDataNotify());
        proto.setScoreRewardId(player.getDailyTaskManager().getScoreRewardId());
    }
}
