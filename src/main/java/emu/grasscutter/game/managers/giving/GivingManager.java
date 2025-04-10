package emu.grasscutter.game.managers.giving;

import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.ItemGiveRecord;
import emu.grasscutter.server.packet.send.PacketGivingRecordNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.anime_game_servers.multi_proto.gi.messages.quest.giving.GivingRecord;

import java.util.List;
import java.util.Map;

public class GivingManager extends BasePlayerDataManager {

    private Map<Integer, ItemGiveRecord> itemGivings;

    public GivingManager(Player player) {
        super(player);
        this.itemGivings = new Int2ObjectOpenHashMap<>();
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
        var success = this.itemGivings.putIfAbsent(givingId, ItemGiveRecord.resolve(givingId));
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

}
