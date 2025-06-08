
package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.PoolMonsterTideConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class AutoMonsterTideScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.MonsterTideScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int autoMonsterTide(@NotNull GroupEventLuaContext context, int challengeIndex, int groupId, Integer[] ordersConfigId, int tideSize, int spawnThreshold, int spawnLimit) {
        logger.debug("[LUA] Call AutoMonsterTide with {},{},{},{},{},{}",
            challengeIndex,groupId,ordersConfigId,tideSize,spawnThreshold,spawnLimit);

        val group = getGroupOrCurrent(context, groupId);

        if (group == null || group.getMonsters() == null) {
            return 1;
        }

        context.getSceneScriptManager().startMonsterTideInGroup(challengeIndex, group, ordersConfigId, tideSize, spawnThreshold, spawnLimit);

        return 0;
    }

    @Override
    public int killMonsterTide(@NotNull GroupEventLuaContext groupEventLuaContext, int groupId, int tideId) {
        return handleUnimplemented(groupId, tideId);
    }

    @Override
    public int autoPoolMonsterTide(@NotNull GroupEventLuaContext context, int index, int groupId, int[] monsterPool, int routeId,
                                   int[] routePoints, int[] monsterAffix, @NotNull PoolMonsterTideConfig monsterPoolParam) {
        return handleUnimplemented(index, groupId, monsterPool, routeId, routePoints, monsterAffix, monsterPoolParam);
        //TODO implement monsterPoolParam contains int totalCount, int minCount, int tag, int fillTime, int fillCount, bool isOrdered
    }

    @Override
    public int clearPoolMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int tideNum) {
        return handleUnimplemented(groupId, tideNum);
    }

    @Override
    public int continueAutoMonster(@NotNull GroupEventLuaContext context, int groupId, int tideNum) {
        return handleUnimplemented(groupId, tideNum);
    }

    @Override
    public int endMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int tideIndex, int endType) {
        return handleUnimplemented(groupId, tideIndex, endType);
    }

    @Override
    public int endPoolMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int index) {
        return handleUnimplemented(groupId, index);
    }

    @Override
    public int pauseAutoMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int monsterTideIndex) {
        return handleUnimplemented(groupId, monsterTideIndex);
    }

    @Override
    public int pauseAutoPoolMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int tideStage) {
        return handleUnimplemented(groupId, tideStage);
    }

    @Override
    public int resumeAutoPoolMonsterTide(@NotNull GroupEventLuaContext context, int groupId, int tideStage) {
        return handleUnimplemented(groupId, tideStage);
    }
}
