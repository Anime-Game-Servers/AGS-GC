package emu.grasscutter.game.dungeons.dungeon_entry;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anime_game_servers.game_data_models.gi.data.dungeon.DungeonRosterData;

import java.util.Optional;

@Entity
@Builder(builderMethodName = "of")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RosterCycleRecord {
    private int rosterId;
    private int selectedPool;
    private String lastCycledTime;

    public static RosterCycleRecord create(int rosterId) {
        return RosterCycleRecord.of()
            .rosterId(rosterId)
            .lastCycledTime(DungeonEntryItem.getLastRefreshTimeStr())
            .selectedPool(0)
            .build();
    }


    public int getNextPool(DungeonRosterData data, int previousPool) {
        var pool = data.getRosterPool();
        if(pool == null){
            return 0;
        }
        return (previousPool + 1) % pool.size();
    }

    public void reset() {
        Optional.ofNullable(GameData.getDungeonRosterDataMap().get(this.rosterId))
            .ifPresent(data -> this.selectedPool = getNextPool(data, this.selectedPool));
        this.lastCycledTime = DungeonEntryItem.getLastRefreshTimeStr();
    }
}
