package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetAllActivatedBargainDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.GetAllActivatedBargainDataReq;

public class HandlerGetAllActivatedBargainDataReq extends TypedPacketHandler<GetAllActivatedBargainDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetAllActivatedBargainDataReq req) {
        session.send(new PacketGetAllActivatedBargainDataRsp(session.getPlayer().getBargainManager().getBargains().values()));
    }
}
