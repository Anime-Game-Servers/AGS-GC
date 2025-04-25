package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainOfferPriceRsp;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainResultType;

public class PacketBargainOfferPriceRsp extends BaseTypedPacket<BargainOfferPriceRsp> {
    public PacketBargainOfferPriceRsp(BargainResultType result, BargainRecord record) {
        super(new BargainOfferPriceRsp());
        proto.setRetCode(
            record.isFinished()
                ? Retcode.RET_BARGAIN_FINISHED
                : Retcode.RET_BARGAIN_NOT_ACTIVATED);
        proto.setCurMood(record.getCurrentMood());
        proto.setBargainResult(result);
        proto.setResultParam(0);
    }

    public PacketBargainOfferPriceRsp(Retcode retcode){
        super(new BargainOfferPriceRsp());
        proto.setRetCode(retcode);
    }
}
