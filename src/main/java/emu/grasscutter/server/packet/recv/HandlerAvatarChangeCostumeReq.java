package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarChangeCostumeReq;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarChangeCostumeRsp;

public class HandlerAvatarChangeCostumeReq extends TypedPacketPairHandler<AvatarChangeCostumeReq, AvatarChangeCostumeRsp> {

	@Override
	public boolean handle(GameSession session, byte[] header, AvatarChangeCostumeReq req, AvatarChangeCostumeRsp rsp) {

		boolean success = session.getPlayer().getAvatars().changeCostume(req.getAvatarGuid(), req.getCostumeId());

		if (!success) {
            rsp.setRetcode(Retcode.RET_SVR_ERROR);
		} else {
            rsp.setAvatarGuid(req.getAvatarGuid());
            rsp.setCostumeId(req.getCostumeId());
            rsp.setRetcode(Retcode.RET_SUCC);
		}
        return true;
	}

}
