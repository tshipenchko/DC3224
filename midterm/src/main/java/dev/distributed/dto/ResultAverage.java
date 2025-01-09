package dev.distributed.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultAverage extends Result {
    private int sum;
    private int count;
}
