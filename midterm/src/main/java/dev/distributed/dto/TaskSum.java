package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSum extends Task {
    private int[] numbers;

    @Override
    public Task[] distribute(int parts) {
        TaskSum[] tasks = new TaskSum[parts];
        int partSize = numbers.length / parts;
        for (int i = 0; i < parts; i++) {
            TaskSum taskSum = new TaskSum();
            taskSum.setId(getId());
            taskSum.setPartId(i);
            taskSum.setNumbers(new int[partSize]);
            System.arraycopy(numbers, i * partSize, taskSum.getNumbers(), 0, partSize);
            tasks[i] = taskSum;
        }
        return tasks;
    }

    @Override
    public Result join(Result[] partialResults) {
        long start = System.currentTimeMillis();

        int total = 0;
        for (Result result : partialResults) {
            total += ((ResultSum) result).getSum();
        }

        long end = System.currentTimeMillis();

        ResultSum resultSum = new ResultSum();
        resultSum.setId(getId());
        resultSum.setExecutionTimeMs(end - start);
        resultSum.setThreadsUsed(1);
        resultSum.setCpuLoad(0.5);
        resultSum.setSum(total);
        return resultSum;
    }

    @Override
    public Result compute() {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }

        ResultSum resultSum = new ResultSum();
        resultSum.setId(getId());
        resultSum.setExecutionTimeMs(System.currentTimeMillis());
        resultSum.setThreadsUsed(1);
        resultSum.setCpuLoad(0.5);
        resultSum.setSum(sum);
        return resultSum;
    }
}
