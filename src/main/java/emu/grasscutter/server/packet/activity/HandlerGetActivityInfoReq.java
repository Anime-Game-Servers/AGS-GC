package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.GetActivityInfoReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.GetActivityInfoRsp;

public class HandlerGetActivityInfoReq extends TypedPacketPairHandler<GetActivityInfoReq, GetActivityInfoRsp> {
	@Override
	public boolean handle(GameSession session, byte[] header, GetActivityInfoReq payload, GetActivityInfoRsp rsp) {
        rsp.setActivityInfoList(payload.getActivityIdList().stream()
            .map(session.getPlayer().getActivityManager()::getInfoProtoByActivityId)
            .toList()
        );
        return true;
	}
}
