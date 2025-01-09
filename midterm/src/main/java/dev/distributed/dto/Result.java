package dev.distributed.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResultSum.class, name = "ResultSum"),
        @JsonSubTypes.Type(value = ResultAverage.class, name = "ResultAverage")
})
public abstract class Result {
    private String id;
    private int partId;

    private long executionTimeMs;
    private int threadsUsed;
    private double cpuLoad;
}
