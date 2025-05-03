package emu.grasscutter.game.managers.cooking;

import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity @AllArgsConstructor
public class ActiveCookCompoundData {
    @Getter
    private int compoundId;
    private int costTime;
    @Getter
    private int totalCount;
    private int startTime;

    @Deprecated // Do not use. Morphia only!
    public ActiveCookCompoundData() {
    }

    public int getOutputCount(int currentTime) {
        int cnt = (currentTime - startTime) / costTime;
        if (cnt > totalCount) return totalCount;
        else return cnt;
    }

    public int getWaitCount(int currentTime) {
        return totalCount - getOutputCount(currentTime);
    }

    /**
     * Get the timestamp of next output.
     * If all finished,return 0
     */
    public int getOutputTime(int currentTime) {
        int cnt = getOutputCount(currentTime);
        if (cnt == totalCount) return 0;
        else return startTime + (cnt + 1) * costTime;
    }

    public void addCompound(int count, int currentTime) {
        if (getOutputCount(currentTime) == totalCount) startTime = currentTime - totalCount * costTime;
        totalCount += count;
    }

    /**
     * Take away all finished compound.
     *
     * @return The number of finished items.
     */
    public int takeCompound(int currentTime) {
        int count = getOutputCount(currentTime);
        startTime += costTime * count;
        totalCount -= count;
        return count;
    }
}
