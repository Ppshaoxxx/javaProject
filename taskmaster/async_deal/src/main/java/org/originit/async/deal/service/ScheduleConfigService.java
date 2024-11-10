package org.originit.async.deal.service;


import java.util.List;

/**
 * 调度配置服务
 * @author pshao
 */
public interface ScheduleConfigService {

    /**
     * 根据任务类型获取任务的最大处理时间
     * 如果超出了最大处理时间的任务算作超时任务，需要进行治理
     * @param taskType 任务类型
     * @return
     */
    Integer getTaskTypeMaxLimit(String taskType);

    /**
     * 获取所有的任务类型
     * @return 任务类型字符列表
     */
    List<String> getTaskTypes();

}
