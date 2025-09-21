package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.SealBattleParams;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class SealBattleScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.SealBattleScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public int startSealBattle(@NotNull GroupEventLuaContext context, int gadgetId, @NotNull SealBattleParams battleParams) {
        val groupId = context.getCurrentGroup().getGroupInfo().getId();
        val sealBattleManager = context.getSceneScriptManager().getScene().getSealBattleManager();
        return sealBattleManager.startSealBattle(groupId, gadgetId, battleParams) ? 0 : 1;
    }
}
