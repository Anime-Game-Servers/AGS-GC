package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@ResourceType(name = "InvestigationConfigData.json", loadPriority = ResourceType.LoadPriority.LOW)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestigationData extends GameResource {
    @Getter(onMethod = @__(@Override))
    int id;
    int cityId;
    List<Integer> nextInvestigationIdList;
    String unlockOpenStateType; //TODO
    int rewardId;

    String investigationType; //TODO

    @Override
    public void onLoad() {

    }
}