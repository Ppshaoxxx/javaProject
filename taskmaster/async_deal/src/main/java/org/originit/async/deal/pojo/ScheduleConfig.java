package org.originit.async.deal.pojo;

import lombok.Data;

/**
 * 任务配置表
 * @author pshao
 */
@Data
public class ScheduleConfig {
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 一次调度的数量
     */
    private Integer scheduleLimit;
    /**
     * 调度的间隔
     */
    private Integer scheduleInterval;
    /**
     * 任务的最大处理时间
     */
    private Integer maxProcessingTime;
    /**
     * 任务全局默认最大重试次数
     */
    private Integer maxRetryNum;
    /**
     * 任务全局默认的最大重试时间
     */
    private Integer retryInterval;

    private Long createTime;

    private Long modifyTime;
}
