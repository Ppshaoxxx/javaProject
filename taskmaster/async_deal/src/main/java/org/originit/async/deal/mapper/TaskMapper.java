package org.originit.async.deal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.originit.async.deal.pojo.Task;

import java.util.List;

@Mapper
public interface TaskMapper {
    /**
     * 查询指定表中超时的任务
     * @param tableName 表名
     * @param minModifyTime 最小的更改时间，如果超过这个说明超时
     * @param limitCount 一次性处理的最多任务数量,为空或者-1则查询所有
     * @param executingStatus 状态为执行中的值
     * @return 超时任务
     */
    List<Task> selectTimeoutTasks(@Param("executingStatus") Integer executingStatus, @Param("tableName") String tableName,@Param("minModifyTime") Long minModifyTime, @Param("limitCount") Integer limitCount);


    /**
     * 批量更新超时的任务状态
     * @param tableName 表名
     * @param failStatus 失败状态值
     * @param executingStatus 执行中的状态值
     * @param timeoutTasks 超时的任务
     * @return 更新数量
     */
    int batchUpdateRetryTask(@Param("tableName") String tableName,@Param("failStatus") Integer failStatus,@Param("executingStatus")Integer executingStatus, @Param("timeoutTasks") List<Task> timeoutTasks);

    /**
     * 获取表的所有记录总数
     * @param tableName 表名
     * @return 记录总数
     */
    Integer selectCount(@Param("tableName") String tableName);

    /**
     * 创建新表
     * @param newTableName 新表的名字
     * @return 如果创建成功返回1
     */
    int createNewTable(@Param("newTableName") String newTableName);

    /**
     * 查找表中对应状态的记录数量
     * @param tableName 表名
     * @param status 状态
     * @return
     */
    int selectCountByStatus(@Param("tableName") String tableName, @Param("status") int status);
}
