package com.anti.fraud.modules.activity.service;

import com.anti.fraud.modules.activity.dto.ActivityCreateDTO;
import com.anti.fraud.modules.activity.vo.ActivityVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ActivityService {

    Page<ActivityVO> getActivityPage(Integer page, Integer size, Integer status, Integer type);

    ActivityVO getActivityDetail(Long id);

    void createActivity(ActivityCreateDTO createDTO);

    void updateActivity(Long id, ActivityCreateDTO createDTO);

    void deleteActivity(Long id);

    void registerActivity(Long activityId);

    void cancelRegistration(Long activityId);

    Page<ActivityVO> getMyActivities(Integer page, Integer size);

    List<ActivityVO> getOngoingActivities();

    void updateActivityStatus();
}
