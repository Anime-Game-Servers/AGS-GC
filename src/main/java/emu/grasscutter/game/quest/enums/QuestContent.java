package emu.grasscutter.game.quest.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum QuestContent implements QuestTrigger {
    QUEST_CONTENT_NONE (0),
    QUEST_CONTENT_KILL_MONSTER (1),                         //#Unused
    QUEST_CONTENT_COMPLETE_TALK (2),
    QUEST_CONTENT_MONSTER_DIE (3),
    QUEST_CONTENT_FINISH_PLOT (4),
    QUEST_CONTENT_OBTAIN_ITEM (5),
    QUEST_CONTENT_TRIGGER_FIRE (6),
    QUEST_CONTENT_CLEAR_GROUP_MONSTER (7),
    QUEST_CONTENT_NOT_FINISH_PLOT (8),                      //#Missing the trigger #RandomQuestExcel
    QUEST_CONTENT_ENTER_DUNGEON (9),
    QUEST_CONTENT_ENTER_MY_WORLD (10),
    QUEST_CONTENT_FINISH_DUNGEON (11),
    QUEST_CONTENT_DESTROY_GADGET (12),
    QUEST_CONTENT_OBTAIN_MATERIAL_WITH_SUBTYPE (13),        //#Missing
    QUEST_CONTENT_NICK_NAME (14),                           //#Missing #Unused
    QUEST_CONTENT_WORKTOP_SELECT (15),                      //#Unused
    QUEST_CONTENT_SEAL_BATTLE_RESULT (16),                  //#Missing #Unused
    QUEST_CONTENT_ENTER_ROOM (17),
    QUEST_CONTENT_GAME_TIME_TICK (18),
    QUEST_CONTENT_FAIL_DUNGEON (19),
    QUEST_CONTENT_LUA_NOTIFY (20),
    QUEST_CONTENT_TEAM_DEAD (21),                           //#Missing
    QUEST_CONTENT_COMPLETE_ANY_TALK (22),
    QUEST_CONTENT_UNLOCK_TRANS_POINT (23),
    QUEST_CONTENT_ADD_QUEST_PROGRESS (24),
    QUEST_CONTENT_INTERACT_GADGET (25),
    QUEST_CONTENT_DAILY_TASK_COMP_FINISH (26),              //#Missing #Unused
    QUEST_CONTENT_FINISH_ITEM_GIVING (27),
    QUEST_CONTENT_SKILL (107),
    QUEST_CONTENT_CITY_LEVEL_UP (109),
    QUEST_CONTENT_PATTERN_GROUP_CLEAR_MONSTER (110),        //#Missing #RandomQuestExcel
    QUEST_CONTENT_ITEM_LESS_THAN (111),
    QUEST_CONTENT_PLAYER_LEVEL_UP (112),
    QUEST_CONTENT_DUNGEON_OPEN_STATUE (113),                //#Missing #Unused
    QUEST_CONTENT_UNLOCK_AREA (114),                        //#Unused
    QUEST_CONTENT_OPEN_CHEST_WITH_GADGET_ID (115),          //#Missing #Unused
    QUEST_CONTENT_UNLOCK_TRANS_POINT_WITH_TYPE (116),       //#Missing #Unused
    QUEST_CONTENT_FINISH_DAILY_DUNGEON (117),               //#Missing #Unused
    QUEST_CONTENT_FINISH_WEEKLY_DUNGEON (118),              //#Missing #Unused
    QUEST_CONTENT_QUEST_VAR_EQUAL (119),
    QUEST_CONTENT_QUEST_VAR_GREATER (120),
    QUEST_CONTENT_QUEST_VAR_LESS (121),
    QUEST_CONTENT_OBTAIN_VARIOUS_ITEM (122),
    QUEST_CONTENT_FINISH_TOWER_LEVEL (123),                 //#Missing #Unused
    QUEST_CONTENT_BARGAIN_SUCC (124),
    QUEST_CONTENT_BARGAIN_FAIL (125),
    QUEST_CONTENT_ITEM_LESS_THAN_BARGAIN (126),
    QUEST_CONTENT_ACTIVITY_TRIGGER_FAILED (127),            //#Missing
    QUEST_CONTENT_MAIN_COOP_ENTER_SAVE_POINT (128),
    QUEST_CONTENT_ANY_MANUAL_TRANSPORT (129),
    QUEST_CONTENT_USE_ITEM (130),
    QUEST_CONTENT_MAIN_COOP_ENTER_ANY_SAVE_POINT (131),
    QUEST_CONTENT_ENTER_MY_HOME_WORLD (132),                //#Missing
    QUEST_CONTENT_ENTER_MY_WORLD_SCENE (133),
    QUEST_CONTENT_TIME_VAR_GT_EQ (134),
    QUEST_CONTENT_TIME_VAR_PASS_DAY (135),
    QUEST_CONTENT_QUEST_STATE_EQUAL (136),
    QUEST_CONTENT_QUEST_STATE_NOT_EQUAL (137),
    QUEST_CONTENT_UNLOCKED_RECIPE (138),                    //#Missing
    QUEST_CONTENT_NOT_UNLOCKED_RECIPE (139),                //#Missing
    QUEST_CONTENT_FISHING_SUCC (140),                       //#Missing
    QUEST_CONTENT_ENTER_ROGUE_DUNGEON (141),                //#Missing
    QUEST_CONTENT_USE_WIDGET (142),                         //#Missing
    QUEST_CONTENT_CAPTURE_SUCC (143),                       //#Missing #Unused
    QUEST_CONTENT_CAPTURE_USE_CAPTURETAG_LIST (144),        //#Missing #Unused
    QUEST_CONTENT_CAPTURE_USE_MATERIAL_LIST (145),          //#Missing
    QUEST_CONTENT_ENTER_VEHICLE (147),
    QUEST_CONTENT_SCENE_LEVEL_TAG_EQ (148),
    QUEST_CONTENT_LEAVE_SCENE (149),
    QUEST_CONTENT_LEAVE_SCENE_RANGE (150),                  //#Missing
    QUEST_CONTENT_IRODORI_FINISH_FLOWER_COMBINATION (151),  //#Missing
    QUEST_CONTENT_IRODORI_POETRY_REACH_MIN_PROGRESS (152),  //#Missing
    QUEST_CONTENT_IRODORI_POETRY_FINISH_FILL_POETRY (153),  //#Missing
    QUEST_CONTENT_ACTIVITY_TRIGGER_UPDATE (154),            //#Missing
    QUEST_CONTENT_GADGET_STATE_CHANGE (155),                //#Missing
    QUEST_CONTENT_UNKNOWN (9999);

    private final int value;

    QuestContent(int id) {
        this.value = id;
    }

    public int getValue() {
        return value;
    }


    private static final Int2ObjectMap<QuestContent> contentMap = new Int2ObjectOpenHashMap<>();
    private static final Map<String, QuestContent> contentStringMap = new HashMap<>();

    static {
        Stream.of(values())
            .forEach(e -> {
                contentMap.put(e.getValue(), e);
                contentStringMap.put(e.name(), e);
            });
    }

    public static QuestContent getContentTriggerByValue(int value) {
        return contentMap.getOrDefault(value, QUEST_CONTENT_NONE);
    }

    public static QuestContent getContentTriggerByName(String name) {
        return contentStringMap.getOrDefault(name, QUEST_CONTENT_NONE);
    }
}
