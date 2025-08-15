package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@ResourceType(name = "DailyTaskLevelExcelConfigData.json")
@Getter
public class DailyTaskLevelData extends GameResource {
    @Getter(onMethod = @__(@Override))
    @SerializedName("ID")
    private int id;
    private int scorePreviewRewardId;
}
