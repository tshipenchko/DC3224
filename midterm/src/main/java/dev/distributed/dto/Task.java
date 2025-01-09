package dev.distributed.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskSum.class, name = "TaskSum"),
        @JsonSubTypes.Type(value = TaskAverage.class, name = "TaskAverage")
})
public abstract class Task {
    private String id;
    private int partId;

    public abstract Task[] distribute(int parts);
    public abstract Result join(Result[] partialResults);
    public abstract Result compute();
}
