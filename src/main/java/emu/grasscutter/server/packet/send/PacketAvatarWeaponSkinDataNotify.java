package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.DateHelper;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarWeaponSkinDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.WeaponSkinInfo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class PacketAvatarWeaponSkinDataNotify extends BaseTypedPacket<AvatarWeaponSkinDataNotify> {

    public PacketAvatarWeaponSkinDataNotify(Player player) {
        super(new AvatarWeaponSkinDataNotify());
        var skinIds = List.copyOf(GameData.getAvatarWeaponSkinDataMap().keySet());
        var expireAt = DateHelper.getUnixTime(Date.from(Instant.now().plus(27, ChronoUnit.HOURS)));
        proto.setSkinIdList(skinIds);
        proto.setSkinInfoList(skinIds.stream().map(id -> new WeaponSkinInfo(id, expireAt)).toList());
    }
}
