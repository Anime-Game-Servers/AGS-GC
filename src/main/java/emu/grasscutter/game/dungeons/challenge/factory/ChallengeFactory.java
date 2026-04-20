package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.world.Scene;
import lombok.val;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeData;
import org.anime_game_servers.game_data_models.gi.data.scene.challenge.ChallengeType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;

import java.util.*;

public class ChallengeFactory {
    private static final Map<ChallengeType, ChallengeFactoryHandler> challengeFactoryHandlers = new HashMap<>();


    static {
        // Instantiate objects of those classes dynamically
        Grasscutter.reflector.getSubTypesOf(ChallengeFactoryHandler.class).forEach(clazz -> {
            try {
                val classInstance = clazz.getDeclaredConstructor().newInstance();
                Arrays.stream(clazz.getAnnotation(ChallengeTypeValue.class).type()).forEach(type ->
                    challengeFactoryHandlers.put(type, classInstance));
            } catch (Exception e) {
                Grasscutter.getLogger().error("Cannot load handler {}", clazz.getSimpleName(), e);
            }
        });
    }

    /**
     * challengeInfo: currentChallengeIndex, currentChallengeId, fatherChallengeIndex
     * */
    public static WorldChallenge getChallenge(ChallengeInfo challengeInfo, List<Integer> params, ChallengeScoreInfo scoreInfo, Scene scene, SceneGroup group){
        val challengeType = Optional.ofNullable(GameData.getDungeonChallengeConfigDataMap().get(challengeInfo.challengeId()))
            .map(ChallengeData::getChallengeType)
            .orElse(ChallengeType.CHALLENGE_NONE);
        return Optional.ofNullable(challengeFactoryHandlers.get(challengeType))
            .map(handler -> handler.build(challengeType, challengeInfo, params, scoreInfo, scene, group))
            .orElse(null);
    }
}
