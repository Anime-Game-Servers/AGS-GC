package emu.grasscutter.game.activity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityConfigItem {
    int activityId;
    int scheduleId;
    Date beginTime;
    Date openTime;
    Date closeTime;
    Date endTime;
    List<Integer> condForceActiveList;
    List<Integer> condForceDisabledList;

    transient ActivityHandler<?> activityHandler;

    void onLoad(){
        if(openTime == null){
            this.openTime = beginTime;
        }
        if(closeTime == null){
            this.closeTime = endTime;
        }
        if(condForceActiveList == null){
            this.condForceActiveList = Collections.emptyList();
        }
        if(condForceDisabledList == null){
            this.condForceDisabledList = Collections.emptyList();
        }
    }
}
