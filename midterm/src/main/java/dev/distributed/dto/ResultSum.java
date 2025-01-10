package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultSum extends Result {
    private int sum;

    @Override
    public Task generateTask() {
        return new TaskSum();
    }

    @Override
    public String toString() {
        return "ResultSum{" +
                "sum=" + sum +
                ", id=" + getId() +
                ", partId=" + getPartId() +
                ", executionTimeMs=" + getExecutionTimeMs() / 1000000.0 +
                ", threadsUsed=" + getThreadsUsed() +
                ", cpuLoad=" + getCpuLoad() +
                '}';
    }
}
