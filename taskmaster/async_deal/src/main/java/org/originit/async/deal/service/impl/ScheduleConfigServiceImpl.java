package org.originit.async.deal.service.impl;

import org.originit.async.deal.mapper.ScheduleConfigMapper;
import org.originit.async.deal.service.ScheduleConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author pshao
 */
@Service
public class ScheduleConfigServiceImpl implements ScheduleConfigService {

    @Autowired
    ScheduleConfigMapper scheduleConfigMapper;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Integer getTaskTypeMaxLimit(String taskType) {
        return scheduleConfigMapper.selectMaxLimitByTaskType(taskType);
    }

    @Override
    public List<String> getTaskTypes() {
        return scheduleConfigMapper.selectAllTaskTypes();
    }

}
