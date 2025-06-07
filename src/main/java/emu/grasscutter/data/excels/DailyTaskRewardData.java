package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@ResourceType(name = "DailyTaskRewardExcelConfigData.json")
@Getter
public class DailyTaskRewardData extends GameResource {
    @Getter(onMethod = @__(@Override))
    @SerializedName("ID")
    private int id;
    private List<DropEntry> dropVec;

    @Data
    public static class DropEntry {
        private int dropId;
        private int previewRewardId;
    }
}
