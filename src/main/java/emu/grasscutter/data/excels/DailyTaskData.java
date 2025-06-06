package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@ResourceType(name = "DailyTaskExcelConfigData.json")
@Getter
public class DailyTaskData extends GameResource {
    @Getter(onMethod = @__(@Override))
    @SerializedName("ID")
    private int id;
    private int cityId;
    private int poolId;
}
