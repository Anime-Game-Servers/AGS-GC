package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketItemGivingRsp;
import emu.grasscutter.server.packet.send.PacketItemGivingRsp.Mode;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.ItemGivingReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HandlerItemGivingReq extends TypedPacketHandler<ItemGivingReq> {
    @Override
    public void handle(GameSession session, byte[] header, ItemGivingReq req) throws Exception {
        var player = session.getPlayer();
        var inventory = player.getInventory();

        var giveId = req.getGivingId();
        var items = req.getItemParamList();

        switch (req.getItemGivingType()) {
            case QUEST -> {
                var givingManager = player.getGivingManager();
                var questManager = player.getQuestManager();

                if (!givingManager.containsGiveItemAction(giveId)) return;

                // Check the items against the resources.
                var data = GameData.getGivingDataMap().get(giveId);
                if (data == null)
                    throw new IllegalArgumentException("No giving data found for " + giveId + ".");

                switch (data.getGivingMethod()) {
                    case GIVING_METHOD_EXACT -> {
                        // Check if the player has all the items.
                        if (!inventory.hasAllItems(items)) {
                            player.sendPacket(new PacketItemGivingRsp());
                            return;
                        }

                        // Remove the items if the quest specifies.
                        if (data.isRemoveItem()) {
                            for (var item : items) {
                                inventory.removeItemById(item.getItemId(), item.getCount());
                            }
                        }

                        // Send the response packet.
                        player.sendPacket(new PacketItemGivingRsp(giveId, Mode.EXACT_SUCCESS));
                        // Remove the action from the active givings.
                        givingManager.removeGivingItemAction(giveId);
                        // Queue the content and condition actions.
                        questManager.queueEvent(QuestContent.QUEST_CONTENT_FINISH_ITEM_GIVING, giveId, 0);
                        questManager.queueEvent(QuestCond.QUEST_COND_ITEM_GIVING_FINISHED, giveId, 0);
                    }
                    case GIVING_METHOD_VAGUE_GROUP, GIVING_METHOD_GROUP -> {
                        var matchedGroups = new ArrayList<Integer>();
                        var givenItems = new HashMap<Integer, Integer>();

                        // Resolve potential item IDs.
                        var groupData = GameData.getGivingGroupDataMap();
                        data.getGivingGroupIds().stream()
                            .map(groupId -> groupData.get((int) groupId))
                            .filter(Objects::nonNull)
                            .forEach(
                                group -> {
                                    var itemIds = group.getItemIds();

                                    // Match item stacks to the group items.
                                    items.forEach(
                                        param -> {
                                            // Get the item instance.
                                            var itemInstance = inventory.getFirstItem(param.getItemId());
                                            if (itemInstance == null) return;

                                            // Get the item ID.
                                            var itemId = itemInstance.getItemId();
                                            if (!itemIds.contains(itemId)) return;

                                            // Add the item to the given items.
                                            givenItems.put(itemId, param.getCount());
                                            matchedGroups.add(group.getId());
                                        });
                                });

                        // Check if the player has any items.
                        if (givenItems.isEmpty() && matchedGroups.isEmpty()) {
                            player.sendPacket(new PacketItemGivingRsp());
                        } else {
                            // Remove the items if the quest specifies.
                            if (data.isRemoveItem()) {
                                for (var item : items) {
                                    inventory.removeItemById(item.getItemId(), item.getCount());
                                }
                            }

                            // Send the response packet.
                            player.sendPacket(new PacketItemGivingRsp(matchedGroups.get(0), Mode.GROUP_SUCCESS));
                            // Mark the giving action as completed.
                            givingManager.markCompleted(giveId);
                            // Queue the content and condition actions.
                            questManager.queueEvent(QuestContent.QUEST_CONTENT_FINISH_ITEM_GIVING, giveId, matchedGroups.get(0));
                            questManager.queueEvent(QuestCond.QUEST_COND_ITEM_GIVING_FINISHED, giveId, matchedGroups.get(0));
                        }
                    }
                }
            }
            case GADGET -> {
                Grasscutter.getLogger().debug("Unimplemented gadget giving was executed for {}.", giveId);
                player.sendPacket(new PacketItemGivingRsp());
            }
        }
    }
}
