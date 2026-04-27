package emu.grasscutter.server.packet.send;

import java.util.ArrayList;

import org.anime_game_servers.multi_proto.gi.messages.world.investigation.Investigation;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationState;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTarget;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTargetState;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.PlayerInvestigationAllInfoNotify;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;

public class PacketPlayerInvestigationAllInfoNotify extends BaseTypedPacket<PlayerInvestigationAllInfoNotify> {

    public PacketPlayerInvestigationAllInfoNotify(Player player) {
        super(new PlayerInvestigationAllInfoNotify());

        val investigationList = new ArrayList<Investigation>();
        GameData.getInvestigationDataMap().forEach((id, investigation) -> {
            var progress = player.getPlayerProgress().getCurrentInvestigationProgress(id);
            int totalProgress = (int)GameData.getInvestigationTargetDataMap().int2ObjectEntrySet().stream().filter(i -> i.getValue().getInvestigationId() == id).count();

            InvestigationState state;
            if(id == 10001) {
                state = InvestigationState.IN_PROGRESS; //TODO: Remove(Testing)
                progress = 0;
            }
            else if(progress < 0) return;
            else if(progress < totalProgress) state = InvestigationState.IN_PROGRESS;
            else state = InvestigationState.COMPLETE; //TODO: Rewards taken

            var target = new Investigation(id);
            target.setState(state); //TODO
            target.setProgress(progress); //TODO
            target.setTotalProgress(totalProgress);
            investigationList.add(target);
        });

        val investigationTargetList = new ArrayList<InvestigationTarget>();
        GameData.getInvestigationTargetDataMap().forEach((id, invTarget) -> {
            var progress = player.getPlayerProgress().getCurrentInvestigationTargetProgress(id);

            var investigationProgress = player.getPlayerProgress().getCurrentInvestigationProgress(invTarget.getInvestigationId());
            if(invTarget.getInvestigationId() == 10001) investigationProgress = 0; //TODO: Remove(Testing)
            if(investigationProgress < 0) return;

            var target = new InvestigationTarget(invTarget.getInvestigationId(), invTarget.getId());
            //TODO: Check if reward is taken
            if(progress < 0) {
                target.setState(InvestigationTargetState.IN_PROGRESS); //TODO
            } else {
                target.setState(progress >= invTarget.getProgress() ? InvestigationTargetState.COMPLETE : InvestigationTargetState.IN_PROGRESS);
            }
            target.setProgress(progress < 0 ? 0 : progress); //TODO
            target.setTotalProgress(invTarget.getProgress());
            investigationTargetList.add(target);
        });

        proto.setInvestigationTargetList(investigationTargetList);
        proto.setInvestigationList(investigationList);
    }

}
