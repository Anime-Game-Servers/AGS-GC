package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityBannerClearReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityBannerClearRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerActivityClearReq extends TypedPacketPairHandler<ActivityBannerClearReq, ActivityBannerClearRsp> {

    @Override
    public boolean handle(GameSession session, byte[] header, ActivityBannerClearReq request, ActivityBannerClearRsp response) {
        val success = session.getPlayer().getActivityManager().setBannerCleared(request.getActivityId(), request.getScheduleId());
        response.setRetcode(success ? Retcode.RET_SUCC : Retcode.RET_ACTIVITY_BANNER_ALREADY_CLEARED);

        return true;
    }
}
