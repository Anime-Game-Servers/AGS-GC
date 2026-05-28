package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.KillMonsterTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.TriggerGroupTriggerTrigger;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_TRIGGER_IN_TIME_FLY;

@ChallengeTypeValue(type = CHALLENGE_TRIGGER_IN_TIME_FLY)
public class TriggerTimeFlyChallengeFactoryHandler extends ChallengeFactoryHandler{
    /**
     * Build a new challenge
     * @param params: [timeLimit, unused1, triggerTag, incTimerCount]
     * ActiveChallenge with 888,189,25,4,666,5
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(0), params.get(0)), // parameters
            buildChallengeTrigger(List.of(new TimeTrigger(2, params.get(0)), new TriggerGroupTriggerTrigger(0, 1, params.get(2)), new KillMonsterTrigger(0, Integer.MAX_VALUE, params.get(3)))),
            scoreInfo
        );
    }
}
