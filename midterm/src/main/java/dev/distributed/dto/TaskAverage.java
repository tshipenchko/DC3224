package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAverage extends Task {
    private int[] numbers;

    @Override
    public Task[] distribute(int parts) {
        return new Task[]{};
    }

    @Override
    public Result join(Result[] partialResults) {
        int totalSum = 0;
        int totalCount = 0;
        for (Result result : partialResults) {
            ResultAverage avgResult = (ResultAverage) result;
            totalSum += avgResult.getSum();
            totalCount += avgResult.getCount();
        }

        ResultAverage resultAverage = new ResultAverage();
        resultAverage.setId(getId());
        resultAverage.setPartId(getPartId());
        resultAverage.setExecutionTimeMs(System.currentTimeMillis());
        resultAverage.setThreadsUsed(1);
        resultAverage.setCpuLoad(0.5);
        resultAverage.setSum(totalSum);
        resultAverage.setCount(totalCount);
        return resultAverage;
    }

    @Override
    public Result compute() {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }

        ResultAverage resultAverage = new ResultAverage();
        resultAverage.setId(getId());
        resultAverage.setPartId(getPartId());
        resultAverage.setExecutionTimeMs(System.currentTimeMillis());
        resultAverage.setThreadsUsed(1);
        resultAverage.setCpuLoad(0.5);
        resultAverage.setSum(sum);
        resultAverage.setCount(numbers.length);
        return resultAverage;
    }
}
