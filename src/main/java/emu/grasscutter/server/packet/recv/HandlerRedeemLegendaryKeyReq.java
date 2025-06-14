package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketRedeemLegendaryKeyRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.quest.RedeemLegendaryKeyReq;

public class HandlerRedeemLegendaryKeyReq extends TypedPacketHandler<RedeemLegendaryKeyReq> {
    @Override
    public void handle(GameSession session, byte[] header, RedeemLegendaryKeyReq req) throws Exception {
        val player = session.getPlayer();
        val newTaskCount = player.getDailyTaskManager().getLegendaryKeyDailyTasks() - 8;
        player.getDailyTaskManager().setLegendaryKeyDailyTasks(newTaskCount);

        // Change player properties
        player.addLegendaryKey(1);
        player.setProperty(PlayerProperty.PROP_PLAYER_LEGENDARY_DAILY_TASK_NUM, newTaskCount);

        // Send packet
        player.getScene().broadcastPacket(new PacketRedeemLegendaryKeyRsp(player.getLegendaryKey()));
    }
}
