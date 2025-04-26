package emu.grasscutter.game.quest;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.BargainData;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainResultType;
import org.anime_game_servers.multi_proto.gi.messages.quest.bargain.BargainSnapshot;
import emu.grasscutter.utils.Utils;
import lombok.*;

@Data
@Entity
@Builder
public class BargainRecord {
    private int bargainId;
    private int lowestPrice;
    private int expectedPrice;
    private int currentMood;
    private boolean finished;
    private BargainResultType result;

    /**
     * Provides an instance of a bargain record. Uses information from game resources.
     *
     * @param bargainId The ID of the bargain.
     * @return An instance of a bargain record.
     */
    public static BargainRecord resolve(int bargainId) {
        var bargainData = GameData.getBargainDataMap().get(bargainId);
        if (bargainData == null)
            throw new IllegalArgumentException("No bargain data found for " + bargainId + ".");

        return BargainRecord.builder().bargainId(bargainId).build().determineBase(bargainData);
    }

    /**
     * Determines the price of the bargain.
     */
    public BargainRecord determineBase(BargainData data) {
        // Set the expected price.
        var price = data.getExpectedValue();
        this.setExpectedPrice(Utils.randomRange(price.get(0), price.get(1)));
        // Set the lowest price.
        this.setLowestPrice(price.get(0));

        // Set the base mood.
        var mood = data.getRandomMood();
        this.setCurrentMood(Utils.randomRange(mood.get(0), mood.get(1)));

        return this;
    }

    /**
     * Computes an offer's validity.
     * Adjusts mood.
     *
     * @param offer The offer to compute.
     * @return The result of the offer.
     */

    //TODO: IMPORTANT!!! needs to return a BARGAIN_COMPLETE_FAIL in some sort of case
    public BargainResultType applyOffer(int offer) {
        if (offer < this.getLowestPrice()) {
            // Decrease the mood.
            this.currentMood -= Utils.randomRange(1, 3);
            // Return a failure.
            this.result = BargainResultType.BARGAIN_SINGLE_FAIL;
            return this.result;
        }

        if (offer > this.getExpectedPrice()) {
            // Complete the bargain.
            this.setFinished(true);
            // Return a success.
            this.result = BargainResultType.BARGAIN_COMPLETE_SUCC;
            return this.result;
        }

        // Compare the offer against the mood and expected price.
        // The mood is out of 100; 1 mood should decrease the price by 100.
        var moodAdjustment = (int) Math.floor(this.getCurrentMood() / 100.0);
        expectedPrice = this.getExpectedPrice() - moodAdjustment;
        if (offer < expectedPrice) {
            // Decrease the mood.
            this.currentMood -= Utils.randomRange(1, 3);
            // Return a failure.
            this.result = BargainResultType.BARGAIN_SINGLE_FAIL;
        } else {
            // Complete the bargain.
            this.setFinished(true);
            // Return a success.
            this.result = BargainResultType.BARGAIN_COMPLETE_SUCC;
        }
        return this.result;
    }

    /**
     * @return A snapshot of this bargain record.
     */
    public BargainSnapshot toBargainSnapshotProto() {
        val proto = new BargainSnapshot();
        proto.setBargainId(this.getBargainId());
        proto.setCurMood(this.getCurrentMood());
        proto.setExpectedPrice(this.getExpectedPrice());
        proto.setPriceLowLimit(this.getLowestPrice());
        return proto;
    }
}
