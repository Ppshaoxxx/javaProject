package com.zdf.flowsvr.dao;

import com.zdf.flowsvr.data.TSchedulePos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TSchedulePosDao {

    /**
     * 新增或修改任务位置
     *
     * @param tSchedulePos
     */
    void save(@Param("tSchedulePos") TSchedulePos tSchedulePos);

    /**
     * 获取任务位置信息
     *
     * @param task_type
     * @return
     */
    TSchedulePos getTaskPos(@Param("task_type") String task_type);

    List<TSchedulePos> getTaskPosList();
}
