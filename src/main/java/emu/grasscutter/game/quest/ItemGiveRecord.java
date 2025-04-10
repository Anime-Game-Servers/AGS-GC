package emu.grasscutter.game.quest;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.GivingData.GiveMethod;
import lombok.*;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecord;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Builder
public class ItemGiveRecord {
    private int givingId;
    private int configId;
    private int groupId;
    private int lastGroupId;
    private boolean finished;
    private Map<Integer, Integer> givenItems;

    /**
     * Provides a builder for an item give record. Uses information from game resources.
     *
     * @param givingId The ID of the giving action.
     * @return A builder for an item give record.
     */
    public static ItemGiveRecord resolve(int givingId) {
        var givingData = GameData.getGivingDataMap().get(givingId);
        if (givingData == null)
            throw new RuntimeException("No giving data found for " + givingId + ".");

        var builder = ItemGiveRecord.builder().givingId(givingId).finished(false);

        // Create a map.
        var givenItems = new HashMap<Integer, Integer>();
        if (givingData.getGivingMethod() == GiveMethod.GIVING_METHOD_EXACT) {
            givingData.getExactItems().forEach(item -> givenItems.put(item.getItemId(), 0));
        } else {
            givingData
                .getGivingGroupIds()
                .forEach(
                    groupId -> {
                        var groupData = GameData.getGivingGroupDataMap().get((int) groupId);
                        if (groupData == null) return;

                        // Add all items in the group.
                        groupData.getItemIds().forEach(itemId -> givenItems.put(itemId, 0));
                        builder.groupId(groupId);
                    });
        }

        return builder.givenItems(givenItems).build();
    }

    /**
     * @return A serialized protobuf object.
     */
    public GivingRecord toProto() {
        val proto = new GivingRecord();
        proto.setGivingId(this.getGivingId());
        proto.setConfigId(this.getConfigId());
        proto.setGroupId(this.getGroupId());
        proto.setLastGroupId(this.getLastGroupId());
        proto.setFinished(this.isFinished());
        proto.setGadgetGiving(false); //TODO: Gadget giving
        proto.setMaterialCntMap(this.getGivenItems());
        return proto;
    }
}
