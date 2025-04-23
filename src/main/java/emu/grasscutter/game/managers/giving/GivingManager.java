package emu.grasscutter.game.managers.giving;

import com.mongodb.lang.Nullable;
import emu.grasscutter.Loggers;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.GivingData;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.ItemGiveRecord;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.server.packet.send.PacketGivingRecordNotify;
import emu.grasscutter.server.packet.send.PacketItemGivingRsp;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecord;
import org.slf4j.Logger;

import java.util.*;

/**
 * Handles logic related to the giving system.
 * This system handles the player actively giving npcs items or placing items in the world to complete quests and puzzles.
 * In case of puzzles the player might be able to Take back the items to place them somewhere else.
 * TODO:
 *  Implement and verify Gadget giving
 *  Implement takeback via TakeBackGivingItemReq/Rsp
 *  Find out what the isReset field in the giving data is used for
 *  handle isRepeatable
 */
public class GivingManager extends BasePlayerDataManager {
    private static Logger logger = Loggers.getDefaultLogger();

    private Map<Integer, ItemGiveRecord> itemGivings;

    public GivingManager(Player player) {
        super(player);
        this.itemGivings = new Int2ObjectOpenHashMap<>();
    }

    public void onLogin(){
        sendGivingRecordNotify();
    }

    /**
     * Sends the giving records to the player.
     */
    public void sendGivingRecordNotify() {
        // Send notification.
        this.player.sendPacket(new PacketGivingRecordNotify(this.getGivingRecords()));
    }

    /**
     * @return Serialized giving records to be used in a packet.
     */
    public List<GivingRecord> getGivingRecords() {
        return this.itemGivings.values().stream()
            .map(ItemGiveRecord::toProto)
            .toList();
    }

    /**
     * Attempts to remove the giving action.
     *
     * @param givingId The giving action ID.
     */
    public void removeGivingItemAction(int givingId) {
        // Remove the action.
        this.itemGivings.remove(givingId);
        // Save the givings.
        player.save();

        this.sendGivingRecordNotify();
    }

    /**
     * Marks a giving action as completed.
     *
     * @param givingId The giving action ID.
     */
    public void markCompleted(int givingId) throws IllegalStateException {
        // Check if the action is already present.
        if (!this.itemGivings.containsKey(givingId)) {
            throw new IllegalStateException("Giving action " + givingId + " is not active.");
        }

        // Mark the action as finished.
        this.itemGivings.get(givingId).setFinished(true);
        // Save the givings.
        player.save();

        this.sendGivingRecordNotify();
    }

    /**
     * Attempts to add the giving action.
     *
     * @param givingId The giving action ID.
     */
    public boolean addGiveItemAction(int givingId) {
        // Add action if absent.
        val success = this.itemGivings.computeIfAbsent(givingId, ItemGiveRecord::resolve);

        // Save the givings.
        player.save();

        this.sendGivingRecordNotify();
        return success == null;
    }

    /**
     * Checks if itemGivings contains the key.
     *
     * @param givingId The giving action ID.
     */
    public boolean containsGiveItemAction(int givingId) {
        return this.itemGivings.containsKey(givingId);
    }


    /**
     * Verifies, performs and notifies a requested giving action.
     * @param giveId the giving id to process the action for
     * @param items items the client is trying to give
     * @param givingType type of giving action
     */
    public void handleGivingRequest(int giveId, List<ItemParam> items, GivingData.GiveType givingType) {
        if(givingType == null){
            logger.error("Giving type is null for {}, so probably an unknown giving type",  giveId );
            player.sendPacket(new PacketItemGivingRsp(Retcode.RET_GIVING_NOT_ACTIVE));
            return;
        }

        val givingData = checkAndGetGivingData(giveId);
        if(givingData == null) {
            logger.warn("unknown giving data for givingId {}",  giveId );
            player.sendPacket(new PacketItemGivingRsp(Retcode.RET_GIVING_NOT_ACTIVE));
            return;
        }

        val result = switch (givingData.getGivingMethod()) {
            case GIVING_METHOD_EXACT -> handleGivingExact(items, givingData);
            case GIVING_METHOD_VAGUE_GROUP, GIVING_METHOD_GROUP
                -> handleGivingGroup(items, givingData);
            default -> {
                logger.warn("Unknown giving method {} for givingId {}", givingData.getGivingMethod(), giveId);
                yield new HandleGivingResult(giveId, Retcode.RET_GIVING_NOT_ACTIVE);
            }
        };

        // Send the response packet.
        player.sendPacket(new PacketItemGivingRsp(result));
        if(result.isSuccess()) {
            // Mark the giving action as completed.
            markCompleted(giveId);

            // notify quest manager about finished quest giving
            switch (givingType) {
                case QUEST -> // Queue the quest events
                    notifyFinishQuest(result.givingId, result.givingGroupId);
                case GADGET -> notifyFinishGadget();
            }
        }
    }


    /**
     * Checks if the giving is active and gets the giving data.
     * @param giveId The giving action ID.
     * @return The giving data when active and known, or null if not active or found.
     */
    @Nullable
    public GivingData checkAndGetGivingData(int giveId){
        if (!containsGiveItemAction(giveId)) return null;

        // Check the items against the resources.
        var data = GameData.getGivingDataMap().get(giveId);
        if (data == null) {
            logger.error("No giving data found for {}.",  giveId );
        }
        return data;
    }


    /**
     * Handles givings of the type GIVING_METHOD_EXACT, this means that the player has to give specifically a list of items,
     * with not options.
     * Depending on the givingData, the items might be removed from the inventory.
     * @param items the items the player is trying to give
     * @param data the giving data definition configuring the giving action
     * @return A [HandleGivingResult] object containing the result of the giving action
     */
    private HandleGivingResult handleGivingExact(List<ItemParam> items, GivingData data){
        val giveId = data.getId();
        var inventory = player.getInventory();
        // Check if the player has all the items.
        if (!inventory.hasAllItems(items)) {
            return new HandleGivingResult(giveId, Retcode.RET_GIVING_ITEM_WRONG);
        }

        // Remove the items if the quest specifies.
        if (data.isRemoveItem()) {
            for (var item : items) {
                inventory.removeItemById(item.getItemId(), item.getCount());
            }
        }

        return new HandleGivingResult(giveId, 0, Mode.EXACT_SUCCESS);
    }

    /**
     * Handles givings of the types GIVING_METHOD_VAGUE_GROUP, GIVING_METHOD_GROUP.
     * This type requires the player to give items from a group of choices.
     * TODO: check whats the exact difference between the two types and what difference in handling my result from it
     * @param items the items the player is trying to give
     * @param data the giving data definition configuring the giving action
     * @return A [HandleGivingResult] object containing the result of the giving action
     */
    private HandleGivingResult handleGivingGroup(List<ItemParam> items, GivingData data){
        val giveId = data.getId();
        var inventory = player.getInventory();
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
            return new HandleGivingResult(giveId, Retcode.RET_GIVING_ITEM_WRONG);
        } else {
            // Remove the items if the quest specifies.
            if (data.isRemoveItem()) {
                for (var item : items) {
                    inventory.removeItemById(item.getItemId(), item.getCount());
                }
            }

            return new HandleGivingResult(giveId, matchedGroups.get(0),  Mode.GROUP_SUCCESS);
        }
    }

    /**
     * Notifies the quest manager about the finished item giving.
     * @param giveId the giving id of the giving that got completed
     * @param givingGroupId the giving group id the giving got completed with
     */
    private void notifyFinishQuest(int giveId, int givingGroupId) {
        val questManager = player.getQuestManager();

        // Queue the content and condition actions.
        questManager.queueEvent(QuestContent.QUEST_CONTENT_FINISH_ITEM_GIVING, giveId, givingGroupId);
        questManager.queueEvent(QuestCond.QUEST_COND_ITEM_GIVING_FINISHED, giveId, givingGroupId);
    }

    /**
     * Notifies the gadgets group about the finished gadget giving.
     * TODO implement
     */
    private void notifyFinishGadget(){
        // TODO EVENT_GADGET_GIVING_FINISHED should be thrown to the gadgets group, with param1 as the cfgId and param2 as the givingId
    }

    @Data
    @AllArgsConstructor
    public static class HandleGivingResult {
        private final int givingId;
        private final int givingGroupId;
        private final Mode mode;
        private final Retcode retcode;

        public HandleGivingResult(int givingId, Retcode retcode) {
            this(givingId, 0, Mode.FAILURE, retcode);
        }
        public HandleGivingResult(int givingId, int givingGroupId, Mode mode) {
            this(givingId, givingGroupId, mode, Retcode.RET_SUCC);
        }

        public boolean isSuccess() {
            return retcode == Retcode.RET_SUCC;
        }

    }

    public enum Mode {
        GROUP_SUCCESS,
        EXACT_SUCCESS,
        FAILURE
    }
}
