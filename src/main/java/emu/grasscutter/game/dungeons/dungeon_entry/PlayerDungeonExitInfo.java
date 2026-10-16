package emu.grasscutter.game.dungeons.dungeon_entry;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.PointData;
import emu.grasscutter.data.excels.SceneData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.SceneType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import lombok.*;

import java.util.Optional;

@Getter
@ToString
@Builder(builderMethodName = "of", setterPrefix = "set")
public class PlayerDungeonExitInfo {
    // allows optional lambda check operation
    @Setter private Integer sceneId;
    @Setter private Integer pointId;
    @Setter private Position pos;
    @Setter private Position rot;

    public static PlayerDungeonExitInfo create() {
        return PlayerDungeonExitInfo.of().build();
    }

    public void setAll(Player player, int dungeonId, int pointId) {
        val fromBigWorld = Optional.ofNullable(player.getScene()).map(Scene::getSceneData).map(SceneData::getSceneType)
            .filter(sceneType -> sceneType == SceneType.SCENE_WORLD);
        if (fromBigWorld.isEmpty()) return; // only set player exits location if player comes in from big world

        // set location to data if present, otherwise get player's current location
        val dungeonEntries = Optional.ofNullable(GameData.getDungeonEntriesMap().get(dungeonId))
            .map(DungeonEntries::getExitPoint);

        // pos
        dungeonEntries.map(PointData::getTransPosWithFallback)
            .or(() -> Optional.of(player.getPosition()))
            .map(Position::clone)
            .ifPresent(this::setPos);

        // rot
        dungeonEntries.map(PointData::getTransRotWithFallback)
            .or(() -> Optional.of(player.getRotation()))
            .map(Position::clone)
            .ifPresent(this::setRot);

        this.sceneId = player.getSceneId();
        this.pointId = pointId;
    }
}
