package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDailyTaskFilterCityRsp;
import org.anime_game_servers.multi_proto.gi.messages.quest.daily.DailyTaskFilterCityReq;

public class HandlerDailyTaskFilterCityReq extends TypedPacketHandler<DailyTaskFilterCityReq> {
    @Override
    public void handle(GameSession session, byte[] header, DailyTaskFilterCityReq req) throws Exception {
        session.getPlayer().getDailyTaskManager().setCityFilter(req.getCityId());
        session.send(new PacketDailyTaskFilterCityRsp(req.getCityId()));
    }
}
