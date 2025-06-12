
package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.ChangeLevelTagParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class SceneStateScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.SceneStateScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    @Override
    public int unhideScenePoint(@NotNull GroupEventLuaContext groupEventLuaContext, int scenePointId) {
        return handleUnimplemented(scenePointId);
    }

    @Override
    public int unlockScenePoint(@NotNull GroupEventLuaContext groupEventLuaContext, int scenePointId) {
        return handleUnimplemented(scenePointId);
    }

    @Override
    public int unlockForce(@NotNull GroupEventLuaContext context, int force) {
        logger.debug("[LUA] Call UnlockForce {}", force);
        context.getSceneScriptManager().getScene().unlockForce(force);
        return 0;
    }

    @Override
    public int lockForce(@NotNull GroupEventLuaContext context, int force) {
        logger.debug("[LUA] Call LockForce {}", force);
        context.getSceneScriptManager().getScene().lockForce(force);
        return 0;
    }

    @Override
    public int addSceneTag(@NotNull GroupEventLuaContext context, int sceneId, int sceneTagId) {
        return handleUnimplemented(sceneId, sceneTagId);
    }

    @Override
    public int delSceneTag(@NotNull GroupEventLuaContext context, int sceneId, int sceneTagId) {
        return handleUnimplemented(sceneId, sceneTagId);
    }

    @Override
    public boolean checkSceneTag(@NotNull GroupEventLuaContext context, int sceneId, int sceneTagId) {
        logger.warn("[LUA] Call unimplemented CheckSceneTag with {}, {}", sceneId, sceneTagId);
        //TODO implement
        return false;
    }

    @Override
    public int changeToTargetLevelTag(@NotNull GroupEventLuaContext context, int levelTagId) {
        return handleUnimplemented(levelTagId);
    }

    @Override
    public int changeToTargetLevelTagWithParamTable(@NotNull GroupEventLuaContext context, int levelTagId, @NotNull ChangeLevelTagParams params) {
        return handleUnimplemented(levelTagId, params);
    }

    @Override
    public @NotNull List<Integer> getCurrentLevelTagVec(@NotNull GroupEventLuaContext context, int levelTagGroupId) {
        logger.warn("[LUA] Call unimplemented GetCurrentLevelTagVec with {}", levelTagGroupId);
        return List.of();
    }

    @Override
    public @NotNull String getLevelTagNameById(@NotNull GroupEventLuaContext context, int levelTagId) {
        logger.warn("[LUA] Call unimplemented GetLevelTagNameById with {}", levelTagId);
        return "";
    }

    @Override
    public boolean isLevelTagChangeInCD(@NotNull GroupEventLuaContext context, int levelTagGroupId) {
        logger.warn("[LUA] Call unimplemented IsLevelTagChangeInCD with {}", levelTagGroupId);
        return false;
    }

    @Override
    public int getSceneOwnerUid(GroupEventLuaContext context) {
        return context.getSceneScriptManager().getScene().getWorld().getHost().getUid();
    }

    @Override
    public int[] getSceneUidList(GroupEventLuaContext context) {
        logger.warn("[LUA] Call unchecked GetSceneUidList");
        //TODO check
        val scriptManager = context.getSceneScriptManager();
        if(scriptManager == null){
            return new int[0];
        }
        return scriptManager.getScene().getPlayers().stream().mapToInt(Player::getUid).toArray();
    }

    @Override
    public boolean checkIsInMpMode(GroupEventLuaContext context) {
        logger.debug("[LUA] Call CheckIsInMpMode");
        return context.getSceneScriptManager().getScene().getWorld().isMultiplayer();
    }
}
