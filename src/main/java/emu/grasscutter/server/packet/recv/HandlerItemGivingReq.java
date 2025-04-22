package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.excels.GivingData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.ItemGivingReq;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.ItemGivingType;

public class HandlerItemGivingReq extends TypedPacketHandler<ItemGivingReq> {
    @Override
    public void handle(GameSession session, byte[] header, ItemGivingReq req) throws Exception {
        // todo is the itemGuidCountMap relevant?
        val givingManager = session.getPlayer().getGivingManager();
        val giveId = req.getGivingId();
        val items = req.getItemParamList();
        val giveType = fromGivingType(req.getItemGivingType());

        givingManager.handleGivingRequest(giveId, items, giveType);
    }

    public GivingData.GiveType fromGivingType(ItemGivingType type) {
        return switch (type) {
            case QUEST -> GivingData.GiveType.QUEST;
            case GADGET -> GivingData.GiveType.GADGET;
            default -> null;
        };
    }
}
