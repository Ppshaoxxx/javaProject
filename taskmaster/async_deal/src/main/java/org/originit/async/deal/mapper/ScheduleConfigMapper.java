package org.originit.async.deal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pshao
 */
@Mapper
public interface ScheduleConfigMapper {

    /**
     * 获取指定任务类型的最大执行时间,单位秒
     * @param taskType 任务类型
     * @return 最大执行时间，若为null表示任务不存在
     */
    Integer selectMaxLimitByTaskType(@Param("taskType") String taskType);

    /**
     * 获取所有的任务类型
     * @return
     */
    List<String> selectAllTaskTypes();
}
