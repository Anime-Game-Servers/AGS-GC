package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ResourceType(name = "GivingGroupExcelConfigData.json")
public class GivingGroupData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    private int id;
    @SerializedName(value = "itemIds", alternate = "ItemIds")
    private List<Integer> itemIds;
    private int finishTalkId;
    private int mistakeTalkId;
}
