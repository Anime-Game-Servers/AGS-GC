package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anime_game_servers.multi_proto.gi.messages.world.investigation.Investigation;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationState;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTarget;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationTargetState;

/**
 * Tracks progress the player made in the world, like obtained items, seen characters and more
 */
@Entity
public class PlayerProgress {

    @Getter private Map<Integer, ItemEntry> itemHistory;

    // keep track of EXEC_ADD_QUEST_PROGRESS count, will be used in CONTENT_ADD_QUEST_PROGRESS
    // not sure where to put this, this should be saved to DB but not to individual quest, since
    // it will be hard to loop and compare
    private Map<Integer, Integer> questProgressCountMap;

    // keep track of the investigation progress
    private Map<Integer, Integer> investigationProgressCountMap;
    private Map<Integer, Integer> investigationTargetProgressCountMap;

    public PlayerProgress(){
        this.questProgressCountMap = new Int2IntOpenHashMap();
        this.investigationTargetProgressCountMap = new Int2IntOpenHashMap();
        this.investigationProgressCountMap = new Int2IntOpenHashMap();
        this.itemHistory = new Int2ObjectOpenHashMap<>();
    }

    public boolean hasPlayerObtainedItemHistorically(int itemId){
        return itemHistory.containsKey(itemId);
    }

    public int addToItemHistory(int itemId, int count){
        val itemEntry = itemHistory.computeIfAbsent(itemId, (key) -> new ItemEntry(itemId));
        return itemEntry.addToObtainedCount(count);
    }

    public int getCurrentProgress(int progressId){
        return questProgressCountMap.getOrDefault(progressId, -1);
    }

    public int addToCurrentProgress(int progressId, int count){
        return questProgressCountMap.merge(progressId, count, Integer::sum);
    }

    public int getCurrentInvestigationProgress(int investigationId){
        return investigationProgressCountMap.getOrDefault(investigationId, -1);
    }

    public int setCurrentInvestigationProgress(int investigationId, int count){
        return investigationProgressCountMap.merge(investigationId, count, Integer::sum);
    }

    public int getCurrentInvestigationTargetProgress(int investigationTargetId){
        return investigationTargetProgressCountMap.getOrDefault(investigationTargetId, -1);
    }

    public int addToCurrentInvestigationTargetProgress(int investigationTargetId, int count){
        return investigationTargetProgressCountMap.merge(investigationTargetId, count, Integer::sum);
    }

    @Entity
    @NoArgsConstructor
    public static class ItemEntry{
        @Getter private int itemId;
        @Getter @Setter private int obtainedCount;

        ItemEntry(int itemId){
            this.itemId = itemId;
        }

        int addToObtainedCount(int amount){
            this.obtainedCount+=amount;
            return this.obtainedCount;
        }
    }
}
