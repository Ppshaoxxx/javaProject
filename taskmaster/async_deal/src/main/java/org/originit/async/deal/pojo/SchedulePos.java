package org.originit.async.deal.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 任务调度位置表
 * @author pshao
 * @TableName t_schedule_pos
 */
@Data
public class SchedulePos implements Serializable {
    /**
     * 任务id
     */
    private Long id;

    /**
     * 任务类型
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

    private Long createTime;

    private Long modifyTime;



}