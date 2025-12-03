package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.*;
import emu.grasscutter.server.packet.send.scene.PacketAvatarChangeTraceEffectNotify;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarWearWeaponSkinReq;

public class HandlerAvatarWearWeaponSkinReq extends TypedPacketHandler<AvatarWearWeaponSkinReq> {
	@Override
    public void handle(GameSession session, byte[] header, AvatarWearWeaponSkinReq req) throws Exception {
        for (var guid: req.getAvatarGuidList()) {
            var avatar = session.getPlayer().getAvatars().getAvatarByGuid(guid);
            avatar.changeWeaponSkin(req.getWeaponSkinId());
        }

        session.send(new PacketAvatarWearWeaponSkinRsp(
            req.getAvatarGuidList(),
            req.getWeaponSkinId()
        ));
	}
}
