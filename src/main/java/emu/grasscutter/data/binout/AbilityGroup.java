package emu.grasscutter.data.binout;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.utils.Utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AbilityGroup {
    @SerializedName(value="abilityGroupSourceType")
    public AbilityGroupSource sourceType;

    @SerializedName(value="abilityGroupTargetType")
    public AbilityGroupTarget targetType;

    @SerializedName(value="abilities", alternate={"targetAbilities"})
    public List<AvatarConfigAbility> targetAbilities;

    // for custom ability groups (useful when group or ability names are unknown)
    private Set<Integer> hashedAbilities;

    public enum AbilityGroupSource {
        ABILITY_GROUP_SOURCE_NONE,
        ABILITY_GROUP_SOURCE_CHALLENGE,
        ABILITY_GROUP_SOURCE_QUEST,
        ABILITY_GROUP_SOURCE_ACTIVITY,
        ABILITY_GROUP_SOURCE_HUNTING,
        ABILITY_GROUP_SOURCE_WATCHER,
        ABILITY_GROUP_SOURCE_AVATAR_SKILL_DEPOT,
        ABILITY_GROUP_SOURCE_GALLERY,
        ABILITY_GROUP_SOURCE_WIDGET,
        ABILITY_GROUP_SOURCE_FISHING,
        ABILITY_GROUP_SOURCE_LEVEL_BANK,
        ABILITY_GROUP_SOURCE_WORLD_AREA,
        ABILITY_GROUP_SOURCE_TOWER_HALF_LEVEL,
    }

    public enum AbilityGroupTarget {
        ABILITY_GROUP_TARGET_NONE,
        ABILITY_GROUP_TARGET_AVATAR,
        ABILITY_GROUP_TARGET_TEAM,
    }

    public static class AvatarConfigAbility {
        public String abilityName;
        public String toString() {
            return abilityName;
        }
    }

    public Set<Integer> getAvatarAbilities() {
        if (targetType != AbilityGroupTarget.ABILITY_GROUP_TARGET_AVATAR) return Set.of();
        if (hashedAbilities != null) return hashedAbilities;
        return targetAbilities.stream().map(Object::toString).map(Utils::abilityHash).collect(Collectors.toSet());
    }
}
