package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.stream.IntStream;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAverage extends Task {
    private int[] numbers;

    @Override
    public Task[] distribute(int parts) {
        TaskAverage[] tasks = new TaskAverage[parts];
        int partSize = numbers.length / parts;
        int remainder = numbers.length % parts;
        int start = 0;
        for (int i = 0; i < parts; i++) {
            TaskAverage taskAverage = new TaskAverage();
            taskAverage.setId(getId());
            taskAverage.setPartId(i);
            int currentPartSize = partSize + (i < remainder ? 1 : 0);
            taskAverage.setNumbers(Arrays.copyOfRange(numbers, start, start + currentPartSize));
            start += currentPartSize;
            tasks[i] = taskAverage;
        }
        return tasks;
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
        resultAverage.setExecutionTimeMs((long) Arrays.stream(partialResults)
                .mapToLong(Result::getExecutionTimeMs)
                .average()
                .orElse(0.0));
        resultAverage.setThreadsUsed(Arrays.stream(partialResults)
                .mapToInt(Result::getThreadsUsed)
                .sum());
        resultAverage.setCpuLoad(Arrays.stream(partialResults)
                .mapToDouble(Result::getCpuLoad)
                .average()
                .orElse(0.0));
        resultAverage.setSum(totalSum);
        resultAverage.setCount(totalCount);
        return resultAverage;
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

        ResultAverage resultAverage = new ResultAverage();
        resultAverage.setId(getId());
        resultAverage.setPartId(getPartId());
        resultAverage.setExecutionTimeMs(end - start);
        resultAverage.setThreadsUsed(1);
        resultAverage.setCpuLoad(cpuLoad);
        resultAverage.setSum(sum);
        resultAverage.setCount(numbers.length);
        return resultAverage;
    }
}
