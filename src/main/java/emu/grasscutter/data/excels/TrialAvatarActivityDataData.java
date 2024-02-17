package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.game_data_models.gi.data.watcher.WatcherTriggerConfig;

@ResourceType(name = "TrialAvatarActivityDataExcelConfigData.json")
@EqualsAndHashCode(callSuper=false)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrialAvatarActivityDataData extends GameResource {
    @Getter(onMethod = @__(@Override))
    private int id;
    private int trialAvatarIndexId;
    private int trialAvatarId;
    private int dungeonId;
    private String battleAvatarsList;
    private int firstPassReward;
    private WatcherTriggerConfig triggerConfig;
    private int progress;

    @Override
    public void onLoad() {
        GameData.getTrialAvatarIndexIdTrialActivityDataDataMap().put(trialAvatarIndexId, id);
    }
}
