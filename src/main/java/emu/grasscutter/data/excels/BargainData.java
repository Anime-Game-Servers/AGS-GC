package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ResourceType(name = "BargainExcelConfigData.json")
public class BargainData extends GameResource {
    @SerializedName(value = "id", alternate = "Id")
    private int id;
    private int questId;
    private List<Integer> dialogId;
    /**
     * This is a list of 2 integers.
     * The first integer is the minimum value of the bargain.
     * The second integer is the maximum value of the bargain.
     */
    private List<Integer> expectedValue;
    private int space;
    private List<Integer> successTalkId;
    private int failTalkId;
    private int moodNpcId;
    private int moodUpperLimit;
    /**
     * This is a list of 2 integers.
     * The first integer is the minimum value of the mood.
     * The second integer is the maximum value of the mood.
     */
    private List<Integer> randomMood;
    private int moodAlertLimit;
    private int moodLowLimit;
    private long moodLowLimitTextTextMapHash;
    private int singleFailMoodDeduction;
    private List<Integer> singleFailTalkId;
    private boolean deleteItem;
    private int itemId;
    private long titleTextTextMapHash;
    private long affordTextTextMapHash;
    private long storageTextTextMapHash;
    private long moodHintTextTextMapHash;
    private long moodDescTextTextMapHash;
}
