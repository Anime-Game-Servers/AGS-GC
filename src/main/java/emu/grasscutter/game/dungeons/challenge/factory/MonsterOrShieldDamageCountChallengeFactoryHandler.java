package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.DamageCountTrigger;
import emu.grasscutter.game.world.Scene;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_MONSTER_DAMAGE_COUNT;
import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.CHALLENGE_SHEILD_ABSORB_DAMAGE_COUNT;

@ChallengeTypeValue(type = {CHALLENGE_MONSTER_DAMAGE_COUNT, CHALLENGE_SHEILD_ABSORB_DAMAGE_COUNT})
public class MonsterOrShieldDamageCountChallengeFactoryHandler extends ChallengeFactoryHandler{
    /**
     * Build a new challenge
     * @param params: [damageCount or absorbDamage, unused1, unused2, unused3]
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            header,
            List.of(params.get(0)), // parameters
            buildChallengeTrigger(List.of(new DamageCountTrigger(1, params.get(0)))),
            scoreInfo
        );
    }
}
