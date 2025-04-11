package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.ItemGivingRsp;


public class PacketItemGivingRsp extends BaseTypedPacket<ItemGivingRsp> {
    public PacketItemGivingRsp() {
        this(0, Mode.FAILURE);
    }

    public PacketItemGivingRsp(int value, Mode mode) {
        super(new ItemGivingRsp());
        proto.setRetcode(mode == Mode.FAILURE ? Retcode.RET_FAIL : Retcode.RET_SUCC);
        if (mode == Mode.EXACT_SUCCESS) {
            proto.setGivingId(value);
        } else if (mode == Mode.GROUP_SUCCESS) {
            proto.setGivingGroupId(value);
        }
    }

    public enum Mode {
        GROUP_SUCCESS,
        EXACT_SUCCESS,
        FAILURE
    }
}
