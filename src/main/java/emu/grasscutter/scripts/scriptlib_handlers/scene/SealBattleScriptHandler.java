
package emu.grasscutter.scripts.scriptlib_handlers.scene;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import emu.grasscutter.scripts.scriptlib_handlers.BaseHandler;
import lombok.Getter;
import org.anime_game_servers.gi_lua.script_lib.handler.scene.SealBattleParams;
import org.slf4j.Logger;

public class SealBattleScriptHandler extends BaseHandler implements org.anime_game_servers.gi_lua.script_lib.handler.scene.SealBattleScriptHandler<GroupEventLuaContext> {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();


    @Override
    public int startSealBattle(GroupEventLuaContext context, int gadgetId, SealBattleParams battleParams) {
        return handleUnimplemented(gadgetId, battleParams);
        //TODO implement var2 containt int radius, int battle_time, int monster_group_id, int default_kill_charge, int auto_charge, int auto_decline, int max_energy, SealBattleType battleType
        // for type KILL_MONSTER watch group monster_group_id and afterwards trigger EVENT_SEAL_BATTLE_END with the result in param2
    }
}
