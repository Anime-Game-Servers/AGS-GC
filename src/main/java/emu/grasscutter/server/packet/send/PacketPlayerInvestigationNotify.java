package emu.grasscutter.server.packet.send;

import java.util.ArrayList;

import org.anime_game_servers.multi_proto.gi.messages.world.investigation.Investigation;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationState;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.PlayerInvestigationNotify;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;

public class PacketPlayerInvestigationNotify extends BaseTypedPacket<PlayerInvestigationNotify> {

    public PacketPlayerInvestigationNotify(Player player) {
        super(new PlayerInvestigationNotify());

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

        proto.setInvestigationList(investigationList);
    }

}
