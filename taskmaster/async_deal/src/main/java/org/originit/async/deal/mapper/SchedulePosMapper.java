package org.originit.async.deal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author pshao
 */
@Mapper
public interface SchedulePosMapper {

    /**
     * 查找指定任务类型的开始表位置
     * @param taskType 任务类型
     * @return
     */
    Integer selectBeginTableByType(@Param("taskType") String taskType);

    /**
     * 查找指定任务类型的结束表位置
     * @param taskType 任务类型
     * @return
     */
    Integer selectEndTablePos(@Param("taskType") String taskType);

    /**
     * 更新任务类型的写入位置
     * @param taskType 任务类型
     * @param newEndPos 写入位置
     * @return
     */
    int updateEndPos(@Param("taskType") String taskType, @Param("newEndPos") int newEndPos);

    /**
     * 更新任务类型的读取位置
     * @param taskType 任务类型
     * @param newBeginPos 新的读取位置
     * @return
     */
    int updateBeginPos(@Param("taskType") String taskType, @Param("newBeginPos")int newBeginPos);
}
