package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.data.common.ItemParamData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ResourceType(name = "GivingExcelConfigData.json")
public class GivingData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    private int id;
    private int talkId;
    private int mistakeTalkId;
    private BagTab tab;
    private GiveMethod givingMethod;
    private List<ItemParamData> exactItems;
    private int exactFinishTalkId;
    private List<Integer> givingGroupIds;
    private int givingGroupCount;
    private boolean isRemoveItem;
    private GiveType giveType;

    public enum GiveMethod {
        GIVING_METHOD_NONE,
        GIVING_METHOD_EXACT,
        GIVING_METHOD_GROUP,
        GIVING_METHOD_VAGUE_GROUP,
        GIVING_METHOD_ANY_NO_FINISH
    }

    public enum BagTab {
        TAB_NONE,
        TAB_WEAPON,
        TAB_EQUIP,
        TAB_AVATAR,
        TAB_FOOD,
        TAB_MATERIAL,
        TAB_QUEST,
        TAB_CONSUME,
        TAB_WIDGET,
        TAB_HOMEWORLD
    }

    public enum GiveType {
        @SerializedName("GIVING_TYPE_QUEST")
        QUEST,
        @SerializedName("GIVING_TYPE_GROUP")
        GROUP
    }
}
