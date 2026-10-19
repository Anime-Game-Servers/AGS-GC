package emu.grasscutter.server.packet.activity;

import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityTakeWatcherRewardReq;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityTakeWatcherRewardRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

import java.util.Optional;

public class HandlerActivityTakeWatcherRewardReq extends TypedPacketPairHandler<ActivityTakeWatcherRewardReq, ActivityTakeWatcherRewardRsp> {

	@Override
	public boolean handle(GameSession session, byte[] header, ActivityTakeWatcherRewardReq req, ActivityTakeWatcherRewardRsp rsp) {
		Optional.ofNullable(session.getPlayer().getActivityManager().getPlayerActivityDataMap().get(req.getActivityId()))
            .ifPresent(x -> x.takeWatcherReward(req.getWatcherId()));

        rsp.setRetcode(Retcode.RET_SUCC);
        rsp.setActivityId(req.getActivityId());
        rsp.setWatcherId(req.getWatcherId());
        return true;
	}

}
