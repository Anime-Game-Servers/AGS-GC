package emu.grasscutter.data.custom;

import emu.grasscutter.data.common.ItemParamData;
import lombok.Data;
import lombok.val;
import org.anime_game_servers.game_data_models.gi.data.rewards.RewardPreviewData;
import org.anime_game_servers.game_data_models.gi.data.rewards.RewardPreviewItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@Data
public class RewardPreviewFallbackItemCache {
    // baseData
    private int rewardId;
    private List<ItemParamData> items;
    public RewardPreviewFallbackItemCache(RewardPreviewData data) {
        this.rewardId = data.getId();

        val previewItems = data.getPreviewItems();

        if (previewItems != null && !previewItems.isEmpty()) {
            this.items = previewItems.stream()
                .filter(d -> d.getId() > 0 && d.getCount() != null && !d.getCount().isEmpty())
                .map(this::toItemParamData)
                .filter(Objects::nonNull)
                .toList();
        } else {
            this.items = List.of();
        }
    }


    @Nullable
    public ItemParamData toItemParamData(RewardPreviewItem previewItem) {
        if(previewItem.getCount() == null || previewItem.getCount().isBlank()){
            return null;
        }

        var count = previewItem.getCount();
        var id = previewItem.getId();
        if (count.contains(";")) {
            String[] split = count.split(";");
            count = count.split(";")[split.length - 1];
        } else if (count.contains(".")) {
            return new ItemParamData(id, (int) Math.ceil(Double.parseDouble(count)));
        }
        return new ItemParamData(id, Integer.parseInt(count));
    }
}
