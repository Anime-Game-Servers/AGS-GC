package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_SURVIVE;

@ChallengeTypeValue(type = CHALLENGE_SURVIVE)
public class SurviveChallengeFactoryHandler extends ChallengeFactoryHandler {
    /**
     * Build a new challenge
     * @param params: [timeToSurvive, unused1, unused2, unused3]
     * grp 201055005
     * ActiveChallenge with 100, 56, 60, 0, 0, 0
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(0)), // parameters
            buildChallengeTrigger(List.of(new TimeTrigger(1, params.get(0), false))),
            scoreInfo
        );
    }
}
