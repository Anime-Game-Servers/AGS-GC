package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.dungeons.challenge.ChallengeInfo;
import emu.grasscutter.game.dungeons.challenge.ChallengeScoreInfo;
import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.factory.ChallengeFactory;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.constants.ChallengeEventMarkType;
import org.anime_game_servers.gi_lua.models.constants.FatherChallengeProperty;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.AttachChildChallengePointConfig;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.CreateFatherChallengeParameters;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class ChallengeScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.ChallengeScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public int activeChallenge(@NotNull GroupEventLuaContext context, int challengeIndex, int challengeId, int timeLimitOrGroupId, int groupId, int objectiveKills, int param5) {
        logger.debug("[LUA] Call ActiveChallenge with {},{},{},{},{},{}",
            challengeIndex, challengeId, timeLimitOrGroupId, groupId, objectiveKills, param5);

        val actualGroupId = getGroupIdOrCurrentId(context, groupId);
        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, 0),
            List.of(timeLimitOrGroupId, actualGroupId, objectiveKills, param5),
            new ChallengeScoreInfo(0, 0),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if (challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        challenge.start();
        return 0;
    }

    @Override
    public int startChallenge(@NotNull GroupEventLuaContext context, int challengeIndex, int challengeId, @NotNull List<Integer> challengeParams) {
        logger.info("[LUA] Call StartChallenge with {},{},{}", challengeIndex, challengeId, challengeParams);
        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, 0),
            challengeParams,
            new ChallengeScoreInfo(0, 0),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if (challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        challenge.start();
        return 0;
    }

    @Override
    public int stopChallenge(@NotNull GroupEventLuaContext context, int challengeIndex, int result) {
        logger.debug("[LUA] Call StopChallenge with ");
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if (challenge == null) {
            return 1;
        }
        if (challenge.getInfo().getChallengeIndex() != challengeIndex) {
            return 2;
        }

        switch (result) {
            case 0 -> challenge.fail();
            case 1 -> challenge.done();
            default -> {
                logger.warn("[LUA] Call StopChallenge with unsupported result {}", result);
                return 3;
            }
        }
        return 0;
    }

    @Override
    public int pauseChallenge(@NotNull GroupEventLuaContext context, int i) {
        return 0;
    }

    @Override
    public int createFatherChallenge(@NotNull GroupEventLuaContext context, int challengeIndex, int challengeId,
                                     int timeLimit, @NotNull CreateFatherChallengeParameters parameters) {
        logger.debug("[LUA] Call CreateFatherChallenge with {} {} {} {}",
            challengeIndex, challengeId, timeLimit, parameters);

        // TODO handle failOnWipe too
        WorldChallenge challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(challengeIndex, challengeId, challengeIndex),
            List.of(parameters.getSuccess(), parameters.getFail(), timeLimit),
            new ChallengeScoreInfo(parameters.getSuccess(), parameters.getFail()),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        if (challenge == null) return 1;

        context.getSceneScriptManager().getScene().setChallenge(challenge);
        return 0;
    }

    @Override
    public int startFatherChallenge(@NotNull GroupEventLuaContext context, int challengeIndex) {
        logger.debug("[LUA] Call StartFatherChallenge with {}", challengeIndex);
        WorldChallenge challenge = context.getSceneScriptManager().getScene().getChallenge();
        if (challenge == null || challenge.getInfo().getChallengeIndex() != challengeIndex) return 1;

        challenge.start();
        return 0;
    }

    @Override
    public int endFatherChallenge(@NotNull GroupEventLuaContext context, int i) {
        return 0;
    }

    @Override
    public int modifyFatherChallengeProperty(@NotNull GroupEventLuaContext context, int challengeId, FatherChallengeProperty propertyType, int value) {
        return handleUnimplemented(challengeId, propertyType.name(), value);
    }

    @Override
    public int attachChildChallenge(@NotNull GroupEventLuaContext context, int fatherChallengeIndex, int childChallengeIndex, int childChallengeId,
                                    @NotNull List<Integer> parameterList, @NotNull List<Integer> uidList, @NotNull AttachChildChallengePointConfig pointConfig) {
        logger.debug("[LUA] Call AttachChildChallenge with {} {} {} {} {} {}",
            fatherChallengeIndex, childChallengeIndex, childChallengeId,
            parameterList, uidList, pointConfig);

        val challenge = ChallengeFactory.getChallenge(
            new ChallengeInfo(childChallengeIndex, childChallengeId, fatherChallengeIndex),
            parameterList,
            new ChallengeScoreInfo(pointConfig.getSuccess(), pointConfig.getFail()),
            context.getSceneScriptManager().getScene(),
            context.getCurrentGroup()
        );

        val sceneChallenge = context.getSceneScriptManager().getScene().getChallenge();
        if (sceneChallenge == null || challenge == null
            || sceneChallenge.getInfo().getChallengeIndex() != fatherChallengeIndex) return 1;

        sceneChallenge.attachChild(challenge);
        return 0;
    }

    @Override
    public int getChallengeTransaction(@NotNull GroupEventLuaContext context, int challengeId) {
        return 0;
    }

    @Override
    public boolean isChallengeStartedByChallengeId(@NotNull GroupEventLuaContext context, int challengeId) {
        return false;
    }

    @Override
    public boolean isChallengeStartedByChallengeIndex(@NotNull GroupEventLuaContext context, int groupId, int challengeIndex) {
        return false;
    }

    @Override
    public int addChallengeDuration(@NotNull GroupEventLuaContext context, int challengeId, int duration) {
        logger.warn("[LUA] Call unimplemented AddChallengeDuration with {},{}", challengeId, duration);
        var challenge = context.getSceneScriptManager().getScene().getChallenge();
        if (challenge == null) {
            return 1;
        }
        if (challenge.getInfo().getChallengeId() != challengeId) {
            return 2;
        }
        /*if(!challenge.addDuration(duration)){
            return 3;
        }*/
        return 0;
    }

    @Override
    public int setChallengeDuration(@NotNull GroupEventLuaContext context, int challengeId, int time) {
        return handleUnimplemented(challengeId, time);
    }

    @Override
    public int setChallengeEventMark(@NotNull GroupEventLuaContext context, int challengeId, @NotNull ChallengeEventMarkType eventMarkType) {
        return handleUnimplemented(challengeId, eventMarkType.name());
    }
}
