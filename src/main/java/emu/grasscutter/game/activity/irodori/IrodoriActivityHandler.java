package emu.grasscutter.game.activity.irodori;

import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.props.ActivityType;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.irodori_chess.IrodoriActivityDetailInfo;

@GameActivity(ActivityType.NEW_ACTIVITY_IRODORI)
public class IrodoriActivityHandler extends ActivityHandler<Object> {

    @Override
    public Object onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        return null;
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {
        val irodoriInfo = new IrodoriActivityDetailInfo();
        activityInfo.setDetail(new ActivityInfo.Detail.IrodoriInfo(irodoriInfo));
    }
}
