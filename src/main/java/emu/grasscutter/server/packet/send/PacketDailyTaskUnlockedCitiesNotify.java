package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskUnlockedCitiesNotify;

public class PacketDailyTaskUnlockedCitiesNotify extends BaseTypedPacket<DailyTaskUnlockedCitiesNotify> {

    public PacketDailyTaskUnlockedCitiesNotify(Player player) {
        super(new DailyTaskUnlockedCitiesNotify());
        proto.setUnlockedCityList(player.getDailyTaskManager().getUnlockedCities().stream().toList());
    }
}
