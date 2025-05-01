package emu.grasscutter.game.activity.aster;

import emu.grasscutter.game.activity.ActivityHandler;
import emu.grasscutter.game.activity.GameActivity;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.inventory.Inventory;
import emu.grasscutter.game.props.ActivityType;
import emu.grasscutter.server.packet.send.*;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.aster.*;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;

import java.util.List;
import java.util.Map;

@GameActivity(ActivityType.NEW_ACTIVITY_ASTER)
public class AsterActivityHandler extends ActivityHandler<AsterGamePlayerData> implements Inventory.VirtualCurrencyHandler<PlayerActivityData> {

    @Override
    public AsterGamePlayerData onInitPlayerActivityData(PlayerActivityData playerActivityData) {
        var activityDetailData = AsterGamePlayerData.create();
        playerActivityData.setDetail(activityDetailData);
        return activityDetailData;
    }

    @Override
    public void initCurrencyHandlers(PlayerActivityData playerActivityData) {
        super.initCurrencyHandlers(playerActivityData);
        val inventory = playerActivityData.getPlayer().getInventory();
        inventory.registerVirtualCurrencyHandler(109, this, playerActivityData); // Aster Credit
        inventory.registerVirtualCurrencyHandler(110, this, playerActivityData); // Aster Token
    }

    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {
        var asterDetail = playerActivityData.getDetail(AsterGamePlayerData.class);
        if(asterDetail == null) {
            asterDetail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        val asterLittle = new AsterLittleDetailInfo();
        asterLittle.setOpen(true);
        asterLittle.setBeginTime(activityInfo.getFirstDayStartTime());
        asterLittle.setStageBeginTime(activityInfo.getFirstDayStartTime());
        // todo get stage based on AsterStageExcelConfigData and the day
        asterLittle.setStageId(3);
        asterLittle.setStageState(AsterLittleStageState.ASTER_LITTLE_STAGE_STARTED);

        playerActivityData.getPlayer().sendPacket(new PacketAsterLittleInfoNotify(asterLittle));

        val asterMiddle = new AsterMidDetailInfo();
        asterMiddle.setOpen(true);
        asterMiddle.setBeginTime(activityInfo.getFirstDayStartTime());
        val asterMiddleCamp = new AsterMidCampInfo(1, new Vector(1538.519f,335.521f,-2113.576f));

        playerActivityData.getPlayer().sendPacket(new PacketAsterMidCampInfoNotify(asterMiddleCamp));
        asterMiddle.setCampList(List.of(asterMiddleCamp));

        playerActivityData.getPlayer().sendPacket(new PacketAsterMidInfoNotify(asterMiddle));

        val asterLarge = new AsterLargeDetailInfo();
        asterLarge.setOpen(true);
        asterLarge.setBeginTime(activityInfo.getFirstDayStartTime());

        playerActivityData.getPlayer().sendPacket(new PacketAsterLargeInfoNotify(asterLarge));

        val asterProgressDetailInfo = new AsterProgressDetailInfo();
        asterProgressDetailInfo.setCount(1000);
        asterProgressDetailInfo.setLastAutoAddTime(activityInfo.getFirstDayStartTime());
        playerActivityData.getPlayer().sendPacket(new PacketAsterProgressInfoNotify(asterProgressDetailInfo));

        val asterInfo = new AsterActivityDetailInfo(asterLittle, asterMiddle, asterLarge, asterProgressDetailInfo);
        asterInfo.setContentClosed(false);
        asterInfo.setContentCloseTime(asterInfo.getContentCloseTime());
        asterInfo.setSpecialRewardTaken(asterInfo.isSpecialRewardTaken());


        // Aster specific currencies
        val tokenCount = asterDetail.getAsterToken();
        val creditCount = asterDetail.getAsterCredit();
        asterInfo.setAsterToken(tokenCount);
        asterInfo.setAsterCredit(creditCount);
        playerActivityData.getPlayer().sendPacket(new PacketAsterMiscInfoNotify(creditCount, tokenCount));

        activityInfo.setDetail(new ActivityInfo.Detail.AsterInfo(asterInfo));
        activityInfo.setActivityCoinMap(Map.of(109, 1, 110, 1));
    }

    @Override
    public int getCurrency(PlayerActivityData playerActivityData, int itemId) {
        val playerData = playerActivityData.getDetail(AsterGamePlayerData.class);
        if (playerData == null) {
            return 0;
        }
        if(itemId == 109) {
            return playerData.getAsterCredit();
        } else if(itemId == 110) {
            return playerData.getAsterToken();
        }
        return 0;
    }

    @Override
    public void setCurrency(PlayerActivityData playerActivityData, int itemId, int count) {
        var detail = playerActivityData.getDetail(AsterGamePlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        if (itemId == 109) {
            detail.setAsterCredit(count);
        } else if(itemId == 110) {
            detail.setAsterToken(count);
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        playerActivityData.getPlayer().sendPacket(
            new PacketAsterMiscInfoNotify(detail.getAsterCredit(), detail.getAsterToken())
        );
    }

    @Override
    public void modifyCurrency(PlayerActivityData playerActivityData, int itemId, int modifyValue) {
        var detail = playerActivityData.getDetail(AsterGamePlayerData.class);
        if(detail == null) {
            detail = onInitPlayerActivityData(playerActivityData);
            playerActivityData.save();
        }

        var asterToken = detail.getAsterToken();
        var asterCredit = detail.getAsterCredit();
        if (itemId == 109) {
            detail.setAsterCredit(asterCredit+modifyValue);
        } else if(itemId == 110) {
            detail.setAsterToken(asterToken+modifyValue);
        }
        playerActivityData.setDetail(detail);
        playerActivityData.save();
        playerActivityData.getPlayer().sendPacket(
            new PacketAsterMiscInfoNotify(detail.getAsterCredit(), detail.getAsterToken())
        );
    }
}
