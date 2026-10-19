package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.GuardTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.KillMonsterTrigger;
import emu.grasscutter.game.dungeons.challenge.trigger.TimeTrigger;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;
import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_GUARD_HP;

@ChallengeTypeValue(type = CHALLENGE_GUARD_HP)
public class KillCountGuardTimeChallengeFactoryHandler extends ChallengeFactoryHandler{
    /**
     * Build a new challenge
     * @param params: [timeLimit, groupId, gadgetCFGId, unused1, incTimerCount(pretty sure)] or
     *                [timeLimit, groupId, gadgetCFGId, incTimerCount(pretty sure)]
     * StartChallenge with 666, 251,{ 120,133210421,421007,0,5 } or
     * ActiveChallenge with 666, 12, 30, 111101015, 15019, 90
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        val realGroup = scene.getScriptManager().getGroupById(params.get(1));
        int goal = realGroup == null || realGroup.getMonsters() == null ? 0 : realGroup.getMonsters().size();

        return new WorldChallenge(
            scene, realGroup,
            header,
            List.of(100, params.get(0), params.get(params.size()-1)), // parameters
            buildChallengeTrigger(List.of(new GuardTrigger(1, params.get(2)), new TimeTrigger(2, params.get(0)), new KillMonsterTrigger(3, goal, params.get(params.size()-1)))),
            scoreInfo
        );
    }
}
