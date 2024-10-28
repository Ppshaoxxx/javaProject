package com.zdf.flowsvr.data;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName t_schedule_pos
 */
@Data
public class TSchedulePos implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private String taskType;

    /**
     * 调度开始于几号表
     */
    private Integer scheduleBeginPos;

    /**
     * 调度结束于几号表
     */
    private Integer scheduleEndPos;

    /**
     * 
     */
    private Long createTime;

    /**
     * 
     */
    private Long modifyTime;



}