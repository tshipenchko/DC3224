package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultAverage extends Result {
    private int sum;
    private int count;

    @Override
    public Task generateTask() {
        return new TaskAverage();
    }

    @Override
    public String toString() {
        return "ResultAverage{" +
                "average=" + (double) sum / count +
                ", sum=" + sum +
                ", count=" + count +
                ", id=" + getId() +
                ", partId=" + getPartId() +
                ", executionTimeMs=" + getExecutionTimeNs() / 1000000.0 +
                ", threadsUsed=" + getThreadsUsed() +
                ", cpuLoad=" + getCpuLoad() +
                '}';
    }
}
