package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.RedeemLegendaryKeyRsp;

public class PacketRedeemLegendaryKeyRsp extends BaseTypedPacket<RedeemLegendaryKeyRsp> {
    public PacketRedeemLegendaryKeyRsp(int keyCount) {
        super(new RedeemLegendaryKeyRsp());
        proto.setLegendaryKeyCount(keyCount);
    }
}
