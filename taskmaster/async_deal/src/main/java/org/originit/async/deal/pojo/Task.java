package org.originit.async.deal.pojo;


import lombok.Data;

/**
 * 任务
 * @author pshao
 */
@Data
public class Task {
    
    private String id;

    /**
     * 所属用户id
     */
    private String userId;

    /**
     * 任务的id
     */
    private String taskId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务当前所处的阶段
     */
    private String taskStage;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 当前已经重试了几次
     */
    private Integer crtRetryNum;

    /**
     * 最大重试次数
     */
    private Integer maxRetryNum;

    /**
     * 用于综合当前时间、优先级、重试延迟等因素的排序字段
     */
    private Long orderTime;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 最大重试间隔
     */
    private Integer maxRetryInterval;

    /**
     * 调度日志
     */
    private String scheduleLog;

    /**
     * 任务上下文
     */
    private String taskContext;
    
    private Long createTime;
    
    private Long modifyTime;

    /**
     * 获取任务类型的表名
     * @param taskType 任务类型
     * @param tableIndex 第几张表
     * @return
     */
    public static String acquireTableName(String taskType, Integer tableIndex) {
        return "t_" + taskType.toLowerCase() + "_task_" + tableIndex;
    }

    /**
     * 综合计算当前的重试时间
     * @param now 当前时间
     * @param priority 优先级
     * @param maxRetryInterval 最大重试间隔
     * @param retryTime 当前重试次数
     * @return
     */
    public static Long calcOrderTime(Long now, Integer maxRetryInterval, Integer retryTime) {
        Long delay;
        if (maxRetryInterval > 0) {
            // 二进制移位，当retryTime是1的时候相当于2^0,当retryTime是2的时候相当于2^1
            delay = Math.min( (1L << retryTime - 1),maxRetryInterval);
        } else {
            delay = - maxRetryInterval.longValue();
        }
        return now + delay * 1000;
    }
}
