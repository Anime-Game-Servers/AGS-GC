package emu.grasscutter.game.activity.dragonspine;

import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.inventory.Inventory;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.server.packet.send.*;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.dragonspine.DragonSpineActivityDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.dragonspine.DragonSpineChapterInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;

import java.util.List;
import java.util.Map;

@GameActivity(ActivityType.NEW_ACTIVITY_DRAGONSPINE)
public class DragonspineActivityHandler extends ActivityHandler<DragonspinePlayerData> implements Inventory.VirtualCurrencyHandler<PlayerActivityData> {

    @Override
    public DragonspinePlayerData onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        var activityDetailData = DragonspinePlayerData.create();
        playerActivityData.setDetail(activityDetailData);
        return activityDetailData;
    }

    @Override
    public void initCurrencyHandlers(PlayerActivityData playerActivityData) {
        super.initCurrencyHandlers(playerActivityData);
        val inventory = playerActivityData.getPlayer().getInventory();
        inventory.registerVirtualCurrencyHandler(111, this, playerActivityData); // Glimmering Essence
        inventory.registerVirtualCurrencyHandler(112, this, playerActivityData); // Warm Essence
        inventory.registerVirtualCurrencyHandler(113, this, playerActivityData); // Miraculous Essence
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {
        var dragonspineDetail = playerActivityData.getDetail(DragonspinePlayerData.class);
        if(dragonspineDetail == null) {
            dragonspineDetail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        // todo get from player progress and DragonspineStageExcelConfigData
        val chapterInfoList = List.of(
            new DragonSpineChapterInfo(1, 0, true, 1, 0),
            new DragonSpineChapterInfo(2, 0, true, 4, 0),
            new DragonSpineChapterInfo(3, 0, true, 7, 0),
            new DragonSpineChapterInfo(4, 0, true, 10, 0)
        );

        val dragonspineInfo = new DragonSpineActivityDetailInfo(chapterInfoList);
        dragonspineInfo.setContentFinishTime((int)getActivityConfigItem().getCloseTime().getTime());

        // probably related to DragonSpineEnhanceExcelConfigData
        dragonspineInfo.setWeaponEnhanceLevel(4);


        // Dragonspine specific currencies
        val glimmeringCount = dragonspineDetail.getGlimmeringEssence();
        val warmCount = dragonspineDetail.getWarmEssence();
        val miraculousCount = dragonspineDetail.getMiraculousEssence();
        dragonspineInfo.setShimmeringEssence(glimmeringCount);
        dragonspineInfo.setWarmEssence(warmCount);
        dragonspineInfo.setWondrousEssence(miraculousCount);

        activityInfo.setDetail(new ActivityInfo.Detail.DragonSpineInfo(dragonspineInfo));
        activityInfo.setActivityCoinMap(Map.of(111, 1, 112, 1, 113, 1));
    }

    @Override
    public int getCurrency(PlayerActivityData playerActivityData, int itemId) {
        val playerData = playerActivityData.getDetail(DragonspinePlayerData.class);
        if (playerData == null) {
            return 0;
        }
        if(itemId == 111) {
            return playerData.getGlimmeringEssence();
        } else if(itemId == 112) {
            return playerData.getWarmEssence();
        } else if(itemId == 113) {
            return playerData.getMiraculousEssence();
        }
        return 0;
    }

    @Override
    public void setCurrency(PlayerActivityData playerActivityData, int itemId, int count) {
        var detail = playerActivityData.getDetail(DragonspinePlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        if (itemId == 111) {
            detail.setGlimmeringEssence(count);
        } else if(itemId == 112) {
            detail.setWarmEssence(count);
        } else if(itemId == 113) {
            detail.setMiraculousEssence(count);
        } else {
            return;
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        playerActivityData.getPlayer().sendPacket(
            new PacketDragonspineCoinChangeNotify(getActivityConfigItem().getScheduleId(), detail.getGlimmeringEssence(), detail.getWarmEssence(), detail.getMiraculousEssence())
        );
    }

    @Override
    public void modifyCurrency(PlayerActivityData playerActivityData, int itemId, int modifyValue) {
        var detail = playerActivityData.getDetail(DragonspinePlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }


        val glimmeringCount = detail.getGlimmeringEssence();
        val warmCount = detail.getWarmEssence();
        val miraculousCount = detail.getMiraculousEssence();
        if (itemId == 111) {
            detail.setGlimmeringEssence(glimmeringCount+modifyValue);
        } else if(itemId == 112) {
            detail.setWarmEssence(warmCount+modifyValue);
        } else if(itemId == 113) {
            detail.setMiraculousEssence(miraculousCount+modifyValue);
        } else {
            return;
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        playerActivityData.getPlayer().sendPacket(
            new PacketDragonspineCoinChangeNotify(getActivityConfigItem().getScheduleId(),
                detail.getGlimmeringEssence(), detail.getWarmEssence(), detail.getMiraculousEssence()
            )
        );
    }
}
