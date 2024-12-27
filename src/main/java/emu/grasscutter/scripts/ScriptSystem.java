package emu.grasscutter.scripts;

import emu.grasscutter.scripts.scriptlib_handlers.ScriptLibControllerHandlerProvider;
import emu.grasscutter.scripts.scriptlib_handlers.ScriptLibGroupHandlerProvider;
import emu.grasscutter.server.game.BaseGameSystem;
import emu.grasscutter.server.game.GameServer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.SceneMeta;
import org.anime_game_servers.gi_lua.script_lib.ScriptLib;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;

public class ScriptSystem extends BaseGameSystem {
    @Getter private static final ScriptLoaderLib scriptLoader = new ScriptLoaderLib();
    Int2ObjectMap<SceneMeta> sceneMetaCache = new Int2ObjectOpenHashMap<>();

    @Getter ScriptLibHandler scriptLibHandler = new emu.grasscutter.scripts.ScriptLibHandler();

    @Getter
    ScriptLibControllerHandlerProvider scriptLibControllerHandlerProvider = new ScriptLibControllerHandlerProvider();

    @Getter
    ScriptLibGroupHandlerProvider scriptLibGroupHandlerProvider = new ScriptLibGroupHandlerProvider();
    public ScriptSystem(GameServer server) {
        super(server);
        ScriptLib.setStaticHandler(new StaticScriptLibHandler());
    }

    public SceneMeta getSceneMeta(int sceneId) {
        if(sceneMetaCache.containsKey(sceneId)){
            return sceneMetaCache.get(sceneId);
        }
        val meta = SceneMeta.of(sceneId, scriptLoader);
        sceneMetaCache.put(sceneId, meta);
        return meta;
    }
}
