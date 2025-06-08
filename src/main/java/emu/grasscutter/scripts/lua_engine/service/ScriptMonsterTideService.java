package emu.grasscutter.scripts.lua_engine.service;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.props.EntityType;
import emu.grasscutter.scripts.SceneScriptManager;
import emu.grasscutter.scripts.listener.ScriptMonsterListener;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.models.scene.group.SceneMonster;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ScriptMonsterTideService {
    private final SceneScriptManager sceneScriptManager;
    private final int challengeIndex;
    private final SceneGroup currentGroup;
    private final AtomicInteger monstersSpawned;
    private final AtomicInteger monsterKillCount;
    private final int spawnLimit;
    private final int spawnThreshold;
    private final int tideSize;
    private final ConcurrentLinkedQueue<Integer> monsterConfigOrders;
    private final List<Integer> monsterConfigIds;
    private final OnMonsterCreated onMonsterCreated= new OnMonsterCreated();
    private final OnMonsterDead onMonsterDead= new OnMonsterDead();

    /**
     *
     * @param sceneScriptManager
     * @param challengeIndex
     * @param group the group that this tide belongs to
     * @param ordersConfigId configIds of the monsters that will be spawned in this tide, in order
     * @param tideSize total size of killable monsters in this tide
     * @param spawnThreshold threshold to spawn more monsters when reached
     * @param spawnLimit upper limit of monsters tide monsters to have active at the same time
     */
    public ScriptMonsterTideService(SceneScriptManager sceneScriptManager, int challengeIndex,
                     SceneGroup group, Integer[] ordersConfigId, int tideSize, int spawnThreshold, int spawnLimit){
        this.sceneScriptManager = sceneScriptManager;
        this.challengeIndex = challengeIndex;
        this.currentGroup = group;
        this.spawnThreshold = spawnThreshold;
        this.tideSize = tideSize;
        this.monsterKillCount = new AtomicInteger(0);
        this.spawnLimit = spawnLimit;
        this.monstersSpawned = new AtomicInteger(0);
        this.monsterConfigOrders = new ConcurrentLinkedQueue<>(List.of(ordersConfigId));
        this.monsterConfigIds = List.of(ordersConfigId);

        this.sceneScriptManager.getScriptMonsterSpawnService().addMonsterCreatedListener(onMonsterCreated);
        this.sceneScriptManager.getScriptMonsterSpawnService().addMonsterDeadListener(onMonsterDead);
        // spawn the first turn with full limit
        addMonsters();
    }

    public class OnMonsterCreated implements ScriptMonsterListener{
        @Override
        public void onNotify(EntityMonster sceneMonster) {
            if(monsterConfigIds.contains(sceneMonster.getConfigId()) && spawnThreshold > 0){
                Grasscutter.getLogger().debug("[MonsterTide] Spawned monster {} with configId {} in group {}",
                        sceneMonster.getId(), sceneMonster.getConfigId(), currentGroup.getGroupInfo().getId());
            }
        }
    }

    public void addMonsters(){
        while(monstersSpawned.get() < spawnLimit && !monsterConfigOrders.isEmpty()){
            addNextMonster();
        }
    }

    public void addNextMonster(){
        val nextMonster = getNextMonster();
        if(nextMonster == null){
            return;
        }
        val sceneMonster = this.sceneScriptManager.createMonster(nextMonster);
        if (sceneMonster == null) {
            return;
        }
        sceneScriptManager.addEntity(sceneMonster);
        monstersSpawned.incrementAndGet();
    }

    public SceneMonster getNextMonster(){
        val nextId = this.monsterConfigOrders.poll();
        val monsters = currentGroup.getMonsters();
        if (monsters == null) {
            return null;
        }
        if(monsters.containsKey(nextId)){
            return monsters.get(nextId);
        }
        // TODO find if this is actually true and a group example: some monster config_id do not exist in groups, so temporarily set it to the first
        //return monsters.values().stream().findFirst().orElse(null);
        return null;
    }

    public class OnMonsterDead implements ScriptMonsterListener {
        @Override
        public void onNotify(EntityMonster sceneMonster) {
            if(sceneMonster.getGroupId() != currentGroup.getGroupInfo().getId()){
                return;
            }
            if (spawnThreshold <= 0) {
                return;
            }

            // we killed a monster from the tide, so check if we need to spawn more
            // TODO verify, maybe we need to work with the total monster count in a group instead
            if(monsterConfigIds.contains(sceneMonster.getConfigId())){
                if(monstersSpawned.decrementAndGet() <= spawnThreshold) {
                    addMonsters();
                }
            }
            // we count all kills from the group, not just the once spawned by the tide, its expected by group `302001019` this way
            val kills = monsterKillCount.incrementAndGet();

            // we reached the tide size limit in kills, to its finished
            if(kills >= tideSize) {
                unload();
                // check if we need to do more cleaning up
            }

            // informs the scripts about the current tide progress
            // scripts might react to different states and e.g. spawn extra monsters, so we broadcast every progress
            sceneScriptManager.callEvent(new ScriptArgs(currentGroup.getGroupInfo().getId(), EventType.EVENT_MONSTER_TIDE_DIE, kills)
                .setEventSource(String.valueOf(challengeIndex)));

        }

    }

    public void unload(){
        this.sceneScriptManager.getScriptMonsterSpawnService().removeMonsterCreatedListener(onMonsterCreated);
        this.sceneScriptManager.getScriptMonsterSpawnService().removeMonsterDeadListener(onMonsterDead);
    }
}
