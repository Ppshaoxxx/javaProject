package com.zdf.flowsvr.service;

import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.flowsvr.data.ScheduleConfig;

public interface ScheduleConfigService {
    /**
     * 获取任务列表
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> getTaskTypeCfgList();

    /**
     * 新增任务配置项
     * @param scheduleConfig
     * @param <T>
     * @return
     */
    <T> ReturnStatus<T> save(ScheduleConfig scheduleConfig);
}
