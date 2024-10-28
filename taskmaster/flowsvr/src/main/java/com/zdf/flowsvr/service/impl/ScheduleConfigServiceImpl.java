package com.zdf.flowsvr.service.impl;

import com.zdf.flowsvr.constant.ErrorStatusReturn;
import com.zdf.flowsvr.dao.ScheduleConfigDao;
import com.zdf.flowsvr.data.ConfigReturn;
import com.zdf.flowsvr.data.ReturnStatus;
import com.zdf.flowsvr.data.ScheduleConfig;
import com.zdf.flowsvr.enums.ErrorStatus;
import com.zdf.flowsvr.service.ScheduleConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleConfigServiceImpl implements ScheduleConfigService {
    @Autowired
    private ScheduleConfigDao scheduleConfigDao;

    Logger logger = LoggerFactory.getLogger(ScheduleConfigServiceImpl.class);

    @Override
    public <T> ReturnStatus<T> getTaskTypeCfgList() {
        List<ScheduleConfig> taskTypeCfgList;
        try {
            taskTypeCfgList = scheduleConfigDao.getTaskTypeCfgList();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ErrorStatus.ERR_GET_TASK_CFG_FROM_DB.getMsg());
            return ErrorStatusReturn.ERR_GET_TASK_CFG_FROM_DB;
        }
        ConfigReturn configReturn = new ConfigReturn(taskTypeCfgList);
        return new ReturnStatus(configReturn);
    }

    @Override
    public <T> ReturnStatus<T> save(ScheduleConfig scheduleConfig) {
        long currentTimeMillis = System.currentTimeMillis();
        scheduleConfig.setCreate_time(currentTimeMillis);
        scheduleConfig.setModify_time(currentTimeMillis);
        try {
            scheduleConfigDao.save(scheduleConfig);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ErrorStatus.ERR_SET_TASK_CFG_FROM_DB.getMsg());
        }
        return ErrorStatusReturn.SUCCESS;
    }
}
