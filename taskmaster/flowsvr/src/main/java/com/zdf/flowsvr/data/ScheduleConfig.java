package com.zdf.flowsvr.data;

import lombok.Data;

@Data
public class ScheduleConfig {
    private String task_type;
    private Integer schedule_limit;
    private Integer schedule_interval;
    private Integer max_processing_time;
    private Integer max_retry_num;
    private Integer retry_interval;
    private Long create_time;
    private Long modify_time;
}
