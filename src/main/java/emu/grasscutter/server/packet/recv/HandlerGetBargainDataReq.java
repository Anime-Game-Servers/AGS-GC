package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetBargainDataRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.GetBargainDataReq;

public class HandlerGetBargainDataReq extends TypedPacketHandler<GetBargainDataReq> {
    @Override
    public void handle(GameSession session, byte[] header, GetBargainDataReq req) throws Exception {
        var bargainId = req.getBargainId();
        var bargain = session.getPlayer().getBargainManager().getBargains().get(bargainId);
        if (bargain == null) {
            session.send(new PacketGetBargainDataRsp(Retcode.RET_BARGAIN_NOT_ACTIVATED));
            return;
        }

        session.send(new PacketGetBargainDataRsp(bargain));
    }
}
