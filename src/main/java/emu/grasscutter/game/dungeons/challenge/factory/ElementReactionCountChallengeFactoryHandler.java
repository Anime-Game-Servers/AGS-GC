package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.ElementReactionTrigger;
import emu.grasscutter.game.props.ElementReactionType;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.List;
import java.util.stream.IntStream;

import static org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType.*;
import static emu.grasscutter.game.props.ElementReactionType.*;

@ChallengeTypeValue(type = {CHALLENGE_ELEMENT_REACTION_COUNT, CHALLENGE_SWIRL_ELEMENT_REACTION_COUNT, CHALLENGE_SWIRL_ELEMENT_REACTION_COUNT})
public class ElementReactionCountChallengeFactoryHandler extends ChallengeFactoryHandler{
    private static final List<ElementReactionType> swirlReaction = List.of(SwirlFire, SwirlWater, SwirlElectric, SwirlIce);
    private static final List<ElementReactionType> crystalliseReaction = List.of(
        CrystalliseFire, CrystalliseWater, CrystalliseElectric, CrystalliseIce);

    /**
     * Get reaction type by integer for this challenge
     * @param oneHotReactionType: one hot encoded list to select targeted reaction type
     */
    private static List<ElementReactionType> getReactionType(ChallengeType type, List<Integer> oneHotReactionType) {
        if (type == CHALLENGE_ELEMENT_REACTION_COUNT) {
            return List.of(ElementReactionType.getTypeByValue(oneHotReactionType.get(0)));
        }

        val reactionTypes = type == CHALLENGE_SWIRL_ELEMENT_REACTION_COUNT ? swirlReaction : crystalliseReaction;

        return IntStream.range(0, reactionTypes.size())
            .filter(i -> oneHotReactionType.get(i) == 1)
            .mapToObj(reactionTypes::get)
            .toList();
    }

    /**
     * Build a new challenge
     * @param params: 1) [reactionType, goal, unused1, unused2] or
     *              2) [Fire, Water, Electric, Ice, goalCount],
     *              first four parameters are one hot encoded
     */
    @Override
    public WorldChallenge build(ChallengeType type, ChallengeInfo header, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group) {
        // normal reaction if size == 4, crystallise or swirl if more than 4
        val goalCount = params.size() > 4 ? params.get(4) : params.get(1);
        return new WorldChallenge(
            scene, group,
            header,
            List.of(goalCount), // parameters
            buildChallengeTrigger(List.of(new ElementReactionTrigger(1, goalCount, getReactionType(type, params.subList(0, 4)), true))),
            scoreInfo
        );
    }
}
