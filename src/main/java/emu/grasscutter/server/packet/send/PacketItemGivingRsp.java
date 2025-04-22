package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.managers.giving.GivingManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.ItemGivingRsp;


public class PacketItemGivingRsp extends BaseTypedPacket<ItemGivingRsp> {
    // error case with specific error
    public PacketItemGivingRsp(Retcode retcode) {
        super(new ItemGivingRsp());
        proto.setRetcode(retcode);
    }

    public PacketItemGivingRsp(GivingManager.HandleGivingResult result) {
        super(new ItemGivingRsp(result.getRetcode()));
        val mode = result.getMode();
        switch (mode){
            case EXACT_SUCCESS -> proto.setGivingId(result.getGivingId());
            case GROUP_SUCCESS ->  proto.setGivingGroupId(result.getGivingGroupId());
        }
    }


}
