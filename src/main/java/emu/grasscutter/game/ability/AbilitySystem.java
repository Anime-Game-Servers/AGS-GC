package emu.grasscutter.game.ability;

import emu.grasscutter.data.binout.AbilityGroup;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class AbilitySystem {
    // resources loaded from BinOutput/AbilityGroup
    @Getter private static final Map<String, AbilityGroup> abilityGroupMap = new HashMap<>();
}
