package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.data.excels.ActivityWatcherData.WatcherTrigger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@ResourceType(name = "InvestigationTargetConfigData.json", loadPriority = ResourceType.LoadPriority.LOW)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestigationTargetData extends GameResource {
    @Getter(onMethod = @__(@Override))
    int id;
    int questId;
    int investigationId;
    WatcherTrigger triggerConfig;

    int progress;

    @Override
    public void onLoad() {
        triggerConfig.onLoad();
    }
}