package emu.grasscutter.game.activity.summer_time_2_8;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class SummerTime28PlayerData {
    int stratagemShard;

    public static SummerTime28PlayerData create() {
        return SummerTime28PlayerData.of()
            .build();
    }
}


