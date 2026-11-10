package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.KillMonsterTrigger;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.*;

@ChallengeTypeValue(type = {CHALLENGE_KILL_COUNT_IN_TIME, CHALLENGE_KILL_COUNT_FAST})
public class KillCountTimeOrFastChallengeFactoryHandler extends ChallengeFactoryHandler{
    private static boolean shouldResetTimer(ChallengeType type){
        return type == CHALLENGE_KILL_COUNT_FAST;
    }

    /**
     * Build a new challenge
     * @param params: [timeLimit, groupId, targetCount, unused1]
     * ActiveChallenge with 180,180,45,133108061,1,0
     * ActiveChallenge Fast with 1001, 5, 15, 240004005, 10, 0
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        val realGroup = scene.getScriptManager().getGroupById(params.get(1));

        return new WorldChallenge(
            scene, realGroup,
            header,
            List.of(params.get(2), params.get(0)), // parameters
            buildChallengeTrigger(List.of(new KillMonsterTrigger(1, params.get(2), shouldResetTimer(type)), new TimeTrigger(2, params.get(0)))),
            scoreInfo
        );
    }
}
