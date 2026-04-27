package emu.grasscutter.server.packet.send;

import java.util.ArrayList;

import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTarget;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTargetState;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.PlayerInvestigationTargetNotify;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;

public class PacketPlayerInvestigationTargetNotify extends BaseTypedPacket<PlayerInvestigationTargetNotify> {

    public PacketPlayerInvestigationTargetNotify(Player player) {
        super(new PlayerInvestigationTargetNotify());

        val investigationTargetList = new ArrayList<InvestigationTarget>();
        GameData.getInvestigationTargetDataMap().forEach((id, invTarget) -> {
            var progress = player.getPlayerProgress().getCurrentInvestigationTargetProgress(id);

            var target = new InvestigationTarget(id, invTarget.getQuestId());
            //TODO: Check if reward is taken
            if(progress == -1) {
                target.setState(InvestigationTargetState.INVALID); //TODO
            } else {
                target.setState(progress >= invTarget.getProgress() ? InvestigationTargetState.COMPLETE : InvestigationTargetState.IN_PROGRESS);
            }
            target.setState(progress >= invTarget.getProgress() ? InvestigationTargetState.COMPLETE : InvestigationTargetState.IN_PROGRESS);
            target.setProgress(progress < 0 ? 0 : progress); //TODO
            target.setTotalProgress(invTarget.getProgress());
            investigationTargetList.add(target);
        });

        proto.setInvestigationTargetList(investigationTargetList);
    }

}
