package emu.grasscutter.game.activity.dragonspine;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class DragonspinePlayerData {
    int glimmeringEssence;
    int warmEssence;
    int miraculousEssence;

    public static DragonspinePlayerData create() {
        return DragonspinePlayerData.of()
            .build();
    }
}


