package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketBargainOfferPriceRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainOfferPriceReq;

public class HandlerBargainOfferPriceReq extends TypedPacketHandler<BargainOfferPriceReq> {
    @Override
    public void handle(GameSession session, byte[] header, BargainOfferPriceReq req) throws Exception {
        // Fetch the active bargain.
        var bargainId = req.getBargainId();
        var bargain = session.getPlayer().getBargainManager().getBargains().get(bargainId);
        if (bargain == null) {
            Grasscutter.getLogger().warn("Bargain {} not found.", bargainId);
            session.send(new PacketBargainOfferPriceRsp(Retcode.RET_FAIL));
            return;
        }

        // Apply the offer.
        var result = bargain.applyOffer(req.getPrice());

        // Queue the quest content event.
        var questManager = session.getPlayer().getQuestManager();
        switch (result) {
            case BARGAIN_COMPLETE_SUCC ->
                questManager.queueEvent(QuestContent.QUEST_CONTENT_BARGAIN_SUCC, bargainId, 0);
            case BARGAIN_SINGLE_FAIL ->
                questManager.queueEvent(QuestContent.QUEST_CONTENT_ITEM_LESS_THAN_BARGAIN, bargainId, 0);
            case BARGAIN_COMPLETE_FAIL ->
                questManager.queueEvent(QuestContent.QUEST_CONTENT_BARGAIN_FAIL, bargainId, 0);
            default ->
                Grasscutter.getLogger().warn("Encountered unknown BargainResultType.");
        }

        // Return the resulting packet.
        session.send(new PacketBargainOfferPriceRsp(result, bargain));
    }
}
