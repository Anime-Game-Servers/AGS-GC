package emu.grasscutter.server.packet.activity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.ActivityManager;
import emu.grasscutter.net.packet.TypedPacketPairHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.shop.ActivityShopSheetInfo;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetActivityShopSheetInfoReq;
import org.anime_game_servers.multi_proto.gi.messages.shop.GetActivityShopSheetInfoRsp;

import java.util.ArrayList;
import java.util.List;

public class HandlerGetActivityShopSheetInfoReq extends TypedPacketPairHandler<GetActivityShopSheetInfoReq, GetActivityShopSheetInfoRsp> {

    @Override
    public boolean handle(GameSession session, byte[] header, GetActivityShopSheetInfoReq req, GetActivityShopSheetInfoRsp rsp) {
        val shopType = req.getShopType();
        val sheetInfo = GameData.getActivityShopDataMap().get(shopType);
        ActivityConfigItem activityConfigItem = null;

        if (sheetInfo != null) {
            activityConfigItem = ActivityManager.getScheduleActivityConfigMap().get(sheetInfo.getScheduleId());
        }

        rsp.setShopType(shopType);

        if (sheetInfo == null || activityConfigItem == null) {
            rsp.setRetcode(Retcode.RET_SHOP_NOT_OPEN);
            return true;
        }

        List<ActivityShopSheetInfo> sheetInfos = new ArrayList<>(sheetInfo.getSheetList().size());
        for (int id : sheetInfo.getSheetList()) {
            sheetInfos.add(new ActivityShopSheetInfo(
                id,
                (int)activityConfigItem.getBeginTime().getTime(),
                (int)activityConfigItem.getEndTime().getTime()));
        }
        rsp.setSheetInfoList(sheetInfos);

        return true;
    }

}
