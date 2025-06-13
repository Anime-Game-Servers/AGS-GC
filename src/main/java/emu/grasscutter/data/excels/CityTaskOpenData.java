package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

import java.util.List;

@ResourceType(name = "CityTaskOpenExcelConfigData.json")
@Getter
public class CityTaskOpenData extends GameResource {
    private int cityId;
    private int questId;
    @SerializedName(value = "cityGuaranteedPool", alternate = {"BACPNEADBAE", "MHICGGPGPLA"})
    List<Integer> cityGuaranteedPool;

    @Override
    public int getId() {
        return this.cityId;
    }
}
