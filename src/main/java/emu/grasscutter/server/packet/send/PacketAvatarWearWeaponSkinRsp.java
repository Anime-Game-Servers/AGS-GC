package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarWearWeaponSkinRsp;

import java.util.List;

public class PacketAvatarWearWeaponSkinRsp extends BaseTypedPacket<AvatarWearWeaponSkinRsp> {
	public PacketAvatarWearWeaponSkinRsp(List<Long> avatarGuidList, int skinId) {
		super(new AvatarWearWeaponSkinRsp());
		proto.setWeaponSkinId(skinId);
		proto.setAvatarGuidList(avatarGuidList);
	}
}
