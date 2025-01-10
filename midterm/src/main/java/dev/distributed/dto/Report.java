package dev.distributed.dto;
import lombok.Data;

@Data
public class Report {
    private int id;
    private int group;
    private int subgroup;
    private int[] barcodes;
    private String jsonData;
}
