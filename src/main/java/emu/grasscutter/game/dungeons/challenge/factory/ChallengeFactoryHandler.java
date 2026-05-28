package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.trigger.ChallengeTrigger;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ChallengeFactoryHandler {
    protected static Map<Class<? extends ChallengeTrigger>, ChallengeTrigger> buildChallengeTrigger(List<ChallengeTrigger> triggers) {
        return triggers.stream().collect(Collectors.toMap(ChallengeTrigger::getClass, t -> t));
    }
    /**
     * Build a new challenge
     * @param header: currentChallengeIndex, currentChallengeId, fatherChallengeIndex
     * @param params: [Different parameters depending on challenge type, size varies from 3 to 7]
     * @param scene: Current scene that player is in
     * @param group: Group spawned/attached to the challenge
     */
    public abstract WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group);
}
