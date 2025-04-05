package emu.grasscutter.game.activity.irodori;

import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.irodori_chess.IrodoriActivityDetailInfo;

@GameActivity(ActivityType.NEW_ACTIVITY_IRODORI)
public class IrodoriActivityHandler extends ActivityHandler {

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {
        val irodoriInfo = new IrodoriActivityDetailInfo();
        activityInfo.setDetail(new ActivityInfo.Detail.IrodoriInfo(irodoriInfo));
    }
}
