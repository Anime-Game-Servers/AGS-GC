package emu.grasscutter.game.activity.aster;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class AsterGamePlayerData {
    int asterToken;
    int asterCredit;
    boolean specialRewardTaken = false;

    public static AsterGamePlayerData create() {
        return AsterGamePlayerData.of()
            .build();
    }
}


