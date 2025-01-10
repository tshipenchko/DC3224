package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSum extends Task {
    private int[] numbers;

    @Override
    public Task[] distribute(int parts) {
        TaskSum[] tasks = new TaskSum[parts];
        int partSize = numbers.length / parts;
        int remainder = numbers.length % parts;
        int start = 0;
        for (int i = 0; i < parts; i++) {
            TaskSum taskSum = new TaskSum();
            taskSum.setId(getId());
            taskSum.setPartId(i);
            int currentPartSize = partSize + (i < remainder ? 1 : 0);
            taskSum.setNumbers(Arrays.copyOfRange(numbers, start, start + currentPartSize));
            start += currentPartSize;
            tasks[i] = taskSum;
        }
        return tasks;
    }

    @Override
    public Result join(Result[] partialResults) {
        int totalSum = 0;
        for (Result result : partialResults) {
            ResultSum sumResult = (ResultSum) result;
            totalSum += sumResult.getSum();
        }

        ResultSum resultSum = new ResultSum();
        resultSum.setId(getId());
        resultSum.setPartId(getPartId());
        resultSum.setExecutionTimeNs((long) Arrays.stream(partialResults)
                .mapToLong(Result::getExecutionTimeNs)
                .average()
                .orElse(0.0));
        resultSum.setThreadsUsed(Arrays.stream(partialResults)
                .mapToInt(Result::getThreadsUsed)
                .sum());
        resultSum.setCpuLoad(Arrays.stream(partialResults)
                .mapToDouble(Result::getCpuLoad)
                .average()
                .orElse(0.0));
        resultSum.setSum(totalSum);
        return resultSum;
    }

    @Override
    public Result compute() {
        long start = System.nanoTime();
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        long end = System.nanoTime();

        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getProcessCpuLoad();

        ResultSum resultSum = new ResultSum();
        resultSum.setId(getId());
        resultSum.setPartId(getPartId());
        resultSum.setExecutionTimeNs(end - start);
        resultSum.setThreadsUsed(1);
        resultSum.setCpuLoad(cpuLoad);
        resultSum.setSum(sum);
        return resultSum;
    }
}
