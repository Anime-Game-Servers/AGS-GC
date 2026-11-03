package emu.grasscutter.data.common;

import com.google.gson.annotations.SerializedName;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.DailyDungeonData;
import emu.grasscutter.utils.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.anime_game_servers.game_data_models.gi.data.dungeon.DungeonRosterData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collection;


@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings(value = "SpellCheckingInspection")
public class PointData {
    private int id;
    private int sceneId;
    private String $type;
    @Nullable
    private Position tranPos;
    @Nullable
    private Position tranRot;
    private Position pos;
    private Position rot;
    private Position size;
    @SerializedName(value="dungeonIds", alternate={"JHHFPGJNMIN"})
    private int[] dungeonIds;
    @SerializedName(value="dungeonRandomList", alternate={"OIBKFJNBLHO"})
    private int[] dungeonRandomList;
    private int[] dungeonRosterList;
    @SerializedName(value="groupIDs", alternate={"HFOBOOHKBGF"})
    private int[] groupIds;
    @SerializedName(value="tranSceneId", alternate={"JHBICGBAPIH"})
    private int tranSceneId;
    private int entryPointId;
    @SerializedName(value="dungeonEntryType", alternate={"EJFPAJGPEBH"})
    private String dungeonEntryType;

    // all dungeons that should appear in this entry, regardless of showing conditions
    private List<Integer> allDungeonIds;

    public String getType() {
        return this.$type;
    }

    public Position getTransPosWithFallback(){
        if (this.tranPos == null) {
            return this.pos;
        }
        return this.tranPos;
    }

    public Position getTransRotWithFallback(){
        if (this.tranRot == null) {
            return this.rot;
        }
        return this.tranRot;
    }

    public void onLoad() {
        if (this.dungeonEntryType == null) {
            this.dungeonEntryType = "";
        }

        if (this.dungeonRandomList != null && this.dungeonRandomList.length > 0) {
            this.allDungeonIds = Arrays.stream(this.dungeonRandomList)
                .mapToObj(GameData.getDailyDungeonDataMap()::get).filter(Objects::nonNull)
                .map(DailyDungeonData::getMap).map(Map::values)
                .flatMap(Collection::stream).flatMapToInt(Arrays::stream)
                .distinct().boxed().toList();
        }

        if (this.dungeonRosterList != null && this.dungeonRosterList.length > 0) {
            this.allDungeonIds = Arrays.stream(this.dungeonRosterList)
                .mapToObj(GameData.getDungeonRosterDataMap()::get)
                .filter(Objects::nonNull)
                .map(DungeonRosterData::getRosterPool)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(DungeonRosterData.RosterPool::getDungeonList)
                .flatMap(List::stream)
                .distinct()
                .toList();
        }

        if (this.dungeonIds != null && this.dungeonIds.length > 0) {
            this.allDungeonIds = Arrays.stream(this.dungeonIds).boxed().toList();
        }
    }

    public void updateDailyDungeon() {
        if (this.dungeonRandomList != null && this.dungeonRandomList.length > 0) {
            this.dungeonIds = Arrays.stream(this.dungeonRandomList)
                .mapToObj(GameData.getDailyDungeonDataMap()::get).filter(Objects::nonNull)
                .map(data -> data.getDungeonsByDay(Grasscutter.getCurrentDayOfWeek()))
                .flatMapToInt(Arrays::stream)
                .filter(GameData.getDungeonDataMap()::containsKey)
                .toArray();
        }
    }
}
