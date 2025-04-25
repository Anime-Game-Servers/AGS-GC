package emu.grasscutter.game.managers.bargain;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.game.player.BasePlayerDataManager;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.BargainRecord;
import emu.grasscutter.server.packet.send.PacketBargainStartNotify;
import emu.grasscutter.server.packet.send.PacketBargainTerminateNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Handles logic related to the bargaining system.
 * This system handles haggling a price with an NPC in certain quests.
 */
public class BargainManager extends BasePlayerDataManager {
    private static Logger logger = Loggers.getDefaultLogger();

    @Getter private Map<Integer, BargainRecord> bargains;

    public BargainManager(Player player) {
        super(player);
        this.bargains = new Int2ObjectOpenHashMap<>();
    }

    /**
     * Attempts to start the bargain.
     *
     * @param bargainId The bargain ID.
     */
    public boolean startBargain(int bargainId) {
        // Check if the bargain is already present.
        if (this.bargains.containsKey(bargainId)) {
            Grasscutter.getLogger().warn("Bargain {} is already active.", bargainId);
            return false;
        }

        // Add the action.
        var bargain = BargainRecord.resolve(bargainId);
        bargains.put(bargainId, bargain);
        // Save the bargains.
        this.player.save();

        // Send the player the start packet.
        this.player.sendPacket(new PacketBargainStartNotify(bargain));
        return true;
    }

    /**
     * Attempts to stop the bargain.
     *
     * @param bargainId The bargain ID.
     */
    public boolean stopBargain(int bargainId) {
        // Check if the bargain is already present.
        if (!this.bargains.containsKey(bargainId)) {
            Grasscutter.getLogger().warn("Bargain {} is not active. Cannot stop.", bargainId);
            return false;
        }

        // Remove the action.
        this.bargains.remove(bargainId);
        // Save the bargains.
        this.player.save();

        // Send the player the stop packet.
        this.player.sendPacket(new PacketBargainTerminateNotify(bargainId));
        return true;
    }
}
