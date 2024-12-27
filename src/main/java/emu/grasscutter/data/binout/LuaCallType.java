package emu.grasscutter.data.binout;

import emu.grasscutter.Grasscutter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum LuaCallType {
    GADGET("Gadget"),
    OWNER_GADGET("OwnerGadget"),
    FROM_GROUP("FromGroup"),
    OWNER_FROM_GROUP("OwnerFromGroup"),
    SPECIFIC_GROUP("SpecificGroup"),
    CUR_SCENE_PLAY("CurScenePlay"),
    CUR_CHALLENGE_GROUP("CurChallengeGroup"),
    CUR_GALLERY_CONTROL_GROUP("CurGalleryControlGroup"),
    CUR_ROGUE_BOSS_GROUP("CurRogueBossGroup"),
    ABILITY_GROUP_SOURCE_GROUP("AbilityGroupSourceGroup"),
    UNKNOWN("UNKNOWN");
    private static final Map<String, LuaCallType> keyEnumMap = Stream.of(values())
        .collect(Collectors.toMap(LuaCallType::getName, e -> e));

    private final String name;
    public static LuaCallType fromString(String str) {
        if (str == null) {
            return UNKNOWN;
        }
        val result = keyEnumMap.get(str);
        if(result == null) {
            Grasscutter.getLogger().warn("Unknown LuaCallType: {}", str);
            return UNKNOWN;
        }
        return result;
    }
}
