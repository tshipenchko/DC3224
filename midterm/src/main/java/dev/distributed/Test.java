package dev.distributed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

public class Test {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        int[] numbers = new int[1000];
        for (int i = 0; i < 1000; i++) {
            numbers[i] = i;
        }

        TaskSum task = new TaskSum();
        task.setId("task-1");
        task.setPartId(-1);
        task.setNumbers(numbers);

        Task[] distributedTasks = task.distribute(4);

        Result[] partialResults = new Result[distributedTasks.length];
        for (int i = 0; i < distributedTasks.length; i++) {
            String taskJson = objectMapper.writeValueAsString(distributedTasks[i]);
            Task deserializedTask = objectMapper.readValue(taskJson, Task.class);
            partialResults[i] = deserializedTask.compute();
        }

        Result finalResult = task.join(partialResults);
        System.out.println("Final Result: " + finalResult);

        String resultJson = objectMapper.writeValueAsString(finalResult);
        System.out.println("Serialized ResultSum: " + resultJson);

        Result deserializedResult = objectMapper.readValue(resultJson, Result.class);
        System.out.println("Deserialized Result: " + deserializedResult);
    }
}
