package com.zdf.flowsvr.dao;

import com.zdf.flowsvr.data.ScheduleConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleConfigDao {
    /**
     * 根据任务类型获取任务配置
     *
     * @param task_type
     * @return
     */
    ScheduleConfig getTaskTypeCfg(@Param("task_type") String task_type);

    /**
     * 新增
     *
     * @param scheduleConfig
     */
    void save(@Param("scheduleConfig") ScheduleConfig scheduleConfig);

    /**
     * 获取所有任务配置列表
     *
     * @return
     */
    List<ScheduleConfig> getTaskTypeCfgList();

}
