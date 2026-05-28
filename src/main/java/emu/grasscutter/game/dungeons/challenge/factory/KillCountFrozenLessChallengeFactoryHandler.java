package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.ElementReactionTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.KillMonsterTrigger;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_KILL_COUNT_FROZEN_LESS;
import static emu.grasscutter.game.props.ElementReactionType.Freeze;

@ChallengeTypeValue(type = CHALLENGE_KILL_COUNT_FROZEN_LESS)
public class KillCountFrozenLessChallengeFactoryHandler extends ChallengeFactoryHandler{
    /**
     * Build a new challenge
     * @param params: [groupId, monsterCountToKill, maximumGotFrozenCount, unused1]
     * ActiveChallenge with 2, 6, 240014003, 7, 5, 0
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        val realGroup = scene.getScriptManager().getGroupById(params.get(0));
        return new WorldChallenge(
            scene, realGroup,
            header,
            List.of(params.get(1), params.get(2)), // parameters
            buildChallengeTrigger(List.of(new KillMonsterTrigger(1, params.get(1)), new ElementReactionTrigger(2, params.get(2), Freeze, false))),
            scoreInfo
        );
    }
}
