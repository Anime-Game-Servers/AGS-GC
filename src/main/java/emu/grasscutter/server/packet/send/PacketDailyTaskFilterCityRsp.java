package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskFilterCityRsp;

public class PacketDailyTaskFilterCityRsp extends BaseTypedPacket<DailyTaskFilterCityRsp> {

    public PacketDailyTaskFilterCityRsp(int cityId) {
        super(new DailyTaskFilterCityRsp());
        proto.setCityId(cityId);
        proto.setRetcode(Retcode.RET_SUCC);
    }
}
