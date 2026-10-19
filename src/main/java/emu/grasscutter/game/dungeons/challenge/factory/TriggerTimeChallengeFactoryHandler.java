package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.TriggerGroupTriggerTrigger;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_TRIGGER_IN_TIME;

@ChallengeTypeValue(type = CHALLENGE_TRIGGER_IN_TIME)
public class TriggerTimeChallengeFactoryHandler extends ChallengeFactoryHandler {
    /**
     * Build a new challenge
     * @param params: [timeLimit, unused1, triggerTag, triggerCount]
     * kill gadgets(explosive barrel) in time: ActiveChallenge with 56,201,20,2,201,4
     * open chest in time: ActiveChallenge with 666,202,30,7,202,1
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(0), params.get(3)), // parameters
            buildChallengeTrigger(List.of(new TimeTrigger(1, params.get(0)), new TriggerGroupTriggerTrigger(2, params.get(3), params.get(2)))),
            scoreInfo
        );
    }
}
