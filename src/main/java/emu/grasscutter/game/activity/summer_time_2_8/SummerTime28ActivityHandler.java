package emu.grasscutter.game.activity.summer_time_2_8;

import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.ActivityConditionExecutor;
import emu.grasscutter.game.inventory.Inventory;
import emu.grasscutter.server.packet.activity.PacketActivityCoinInfoNotify;
import lombok.val;
import org.anime_game_servers.game_data_models.gi.data.activity.ActivityType;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.summer_time_v2.SummerTimeV2BoatStageInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.summer_time_v2.SummerTimeV2DetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.summer_time_v2.SummerTimeV2DungeonStageInfo;

import java.util.List;
import java.util.Map;

@GameActivity(ActivityType.NEW_ACTIVITY_SUMMER_TIME_2_8)
public class SummerTime28ActivityHandler extends ActivityHandler<SummerTime28PlayerData> implements Inventory.VirtualCurrencyHandler<PlayerActivityData> {

    @Override
    public SummerTime28PlayerData onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        var activityDetailData = SummerTime28PlayerData.create();
        playerActivityData.setDetail(activityDetailData);
        return activityDetailData;
    }

    @Override
    public void initCurrencyHandlers(PlayerActivityData playerActivityData) {
        super.initCurrencyHandlers(playerActivityData);
        val inventory = playerActivityData.getPlayer().getInventory();
        inventory.registerVirtualCurrencyHandler(141, this, playerActivityData); // Glimmering Essence
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {
        var activityDetail = playerActivityData.getDetail(SummerTime28PlayerData.class);
        if(activityDetail == null) {
            activityDetail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        val activityDetailInfo = new SummerTimeV2DetailInfo();


        // todo get from player progress and SummerTimeV2BoatStageExcelConfigData
        val boatStageInfoList = List.of(
            new SummerTimeV2BoatStageInfo(1, true, 1, 1),
            new SummerTimeV2BoatStageInfo(2, true, 1, 2),
            new SummerTimeV2BoatStageInfo(3, true, 1, 3),
            new SummerTimeV2BoatStageInfo(4, true, 1, 4),
            new SummerTimeV2BoatStageInfo(5, true, 1, 5),
            new SummerTimeV2BoatStageInfo(6, true, 1, 6)
        );
        activityDetailInfo.setBoatStageInfoList(boatStageInfoList);

        // todo get from player progress and SummerTimeV2DungeonStageExcelConfigData
        val dungeonStageInfoList = List.of(
            new SummerTimeV2DungeonStageInfo(true, true, 1, 1),
            new SummerTimeV2DungeonStageInfo(true, true, 1, 2),
            new SummerTimeV2DungeonStageInfo(true, true, 1, 3),
            new SummerTimeV2DungeonStageInfo(true, true, 1, 4)
        );
        activityDetailInfo.setDungeonStageInfoList(dungeonStageInfoList);

        activityDetailInfo.setContentClosed(false);
        activityDetailInfo.setTakenRewardCount(0);
        activityDetailInfo.setCurDungeonRewardLimit(1000);

        activityInfo.setDetail(new ActivityInfo.Detail.SummerTimeV2Info(activityDetailInfo));
        activityInfo.setActivityCoinMap(Map.of(141, activityDetail.getStratagemShard()));
    }

    @Override
    public int getCurrency(PlayerActivityData playerActivityData, int itemId) {
        val playerData = playerActivityData.getDetail(SummerTime28PlayerData.class);
        if (playerData == null) {
            return 0;
        }
        if(itemId == 141) {
            return playerData.getStratagemShard();
        }
        return 0;
    }

    @Override
    public void setCurrency(PlayerActivityData playerActivityData, int itemId, int count) {
        var detail = playerActivityData.getDetail(SummerTime28PlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        if (itemId == 141) {
            detail.setStratagemShard(count);
        } else {
            return;
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        notifyCoinMap(playerActivityData, detail);
    }

    @Override
    public void modifyCurrency(PlayerActivityData playerActivityData, int itemId, int modifyValue) {
        var detail = playerActivityData.getDetail(SummerTime28PlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }


        val shardCountCount = detail.getStratagemShard();
        if (itemId == 141) {
            detail.setStratagemShard(shardCountCount+modifyValue);
        } else {
            return;
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        notifyCoinMap(playerActivityData, detail);
    }

    private void notifyCoinMap(PlayerActivityData playerActivityData, SummerTime28PlayerData detailData){
        val activityId = playerActivityData.getActivityId();
        val scheduleId = getActivityConfigItem().getScheduleId();
        val coinMap = Map.of(141, detailData.getStratagemShard());
        playerActivityData.getPlayer().sendPacket(new PacketActivityCoinInfoNotify(activityId, scheduleId, coinMap));
    }
}
