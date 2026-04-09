package com.anti.fraud.modules.activity.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.activity.dto.ActivityCreateDTO;
import com.anti.fraud.modules.activity.entity.Activity;
import com.anti.fraud.modules.activity.entity.ActivityRegistration;
import com.anti.fraud.modules.activity.mapper.ActivityMapper;
import com.anti.fraud.modules.activity.mapper.ActivityRegistrationMapper;
import com.anti.fraud.modules.activity.service.ActivityService;
import com.anti.fraud.modules.activity.vo.ActivityVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;

    @Override
    public Page<ActivityVO> getActivityPage(Integer page, Integer size, Integer status, Integer type) {
        Page<Activity> activityPage = new Page<>(page, size);

        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }

        if (type != null) {
            wrapper.eq(Activity::getActivityType, type);
        }

        wrapper.orderByDesc(Activity::getCreateTime);

        activityMapper.selectPage(activityPage, wrapper);

        Long userId = SecurityUtils.getCurrentUserId();

        // 手动转换避免类型不兼容
        Page<ActivityVO> result = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        result.setRecords(activityPage.getRecords().stream().map(activity -> convertToActivityVO(activity, userId)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public ActivityVO getActivityDetail(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        return convertToActivityVO(activity, userId);
    }

    @Override
    @Transactional
    public void createActivity(ActivityCreateDTO createDTO) {
        Activity activity = new Activity();
        activity.setActivityName(createDTO.getActivityName());
        activity.setActivityType(createDTO.getActivityType());
        activity.setCoverImage(createDTO.getCoverImage());
        activity.setDescription(createDTO.getDescription());
        activity.setStartTime(createDTO.getStartTime());
        activity.setEndTime(createDTO.getEndTime());
        activity.setMaxParticipants(createDTO.getMaxParticipants());
        activity.setPointsReward(createDTO.getPointsReward());
        activity.setCurrentParticipants(0);

        // 自动设置状态
        LocalDateTime now = LocalDateTime.now();
        if (createDTO.getStartTime().isAfter(now)) {
            activity.setStatus(1); // 报名中
        } else if (createDTO.getEndTime().isAfter(now)) {
            activity.setStatus(2); // 进行中
        } else {
            activity.setStatus(3); // 已结束
        }

        activityMapper.insert(activity);
    }

    @Override
    @Transactional
    public void updateActivity(Long id, ActivityCreateDTO createDTO) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        activity.setActivityName(createDTO.getActivityName());
        activity.setActivityType(createDTO.getActivityType());
        activity.setCoverImage(createDTO.getCoverImage());
        activity.setDescription(createDTO.getDescription());
        activity.setStartTime(createDTO.getStartTime());
        activity.setEndTime(createDTO.getEndTime());
        activity.setMaxParticipants(createDTO.getMaxParticipants());
        activity.setPointsReward(createDTO.getPointsReward());

        activityMapper.updateById(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void registerActivity(Long activityId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if (activity.getStatus() != 1) {
            throw new BusinessException("活动不在报名阶段");
        }

        if (activity.getMaxParticipants() != null &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException("报名人数已满");
        }

        // 检查是否已报名
        Long count = registrationMapper.selectCount(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId)
        );

        if (count > 0) {
            throw new BusinessException("您已报名此活动");
        }

        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setStatus(1);
        registrationMapper.insert(registration);

        // 更新参与人数
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long activityId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        // 删除报名记录
        int deleted = registrationMapper.delete(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId)
        );

        if (deleted > 0 && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityMapper.updateById(activity);
        }
    }

    @Override
    public Page<ActivityVO> getMyActivities(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        List<ActivityRegistration> registrations = registrationMapper.selectList(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getUserId, userId)
                        .orderByDesc(ActivityRegistration::getCreateTime)
        );

        List<Long> activityIds = registrations.stream()
                .map(ActivityRegistration::getActivityId)
                .collect(Collectors.toList());

        if (activityIds.isEmpty()) {
            return new Page<>(page, size);
        }

        Page<Activity> activityPage = new Page<>(page, size);
        activityMapper.selectPage(activityPage,
                new LambdaQueryWrapper<Activity>()
                        .in(Activity::getId, activityIds)
        );

        // 手动转换避免类型不兼容
        Page<ActivityVO> result = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        result.setRecords(activityPage.getRecords().stream().map(activity -> convertToActivityVO(activity, userId)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<ActivityVO> getOngoingActivities() {
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .in(Activity::getStatus, 1, 2)
                        .orderByAsc(Activity::getStartTime)
                        .last("LIMIT 10")
        );

        Long userId = SecurityUtils.getCurrentUserId();

        return activities.stream()
                .map(activity -> convertToActivityVO(activity, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateActivityStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 更新报名中的活动为进行中
        activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 1)
                        .le(Activity::getStartTime, now)
        ).forEach(activity -> {
            activity.setStatus(2);
            activityMapper.updateById(activity);
        });

        // 更新进行中的活动为已结束
        activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 2)
                        .lt(Activity::getEndTime, now)
        ).forEach(activity -> {
            activity.setStatus(3);
            activityMapper.updateById(activity);
        });
    }

    private ActivityVO convertToActivityVO(Activity activity, Long userId) {
        ActivityVO vo = new ActivityVO();
        vo.setId(activity.getId());
        vo.setActivityName(activity.getActivityName());
        vo.setActivityType(activity.getActivityType());
        vo.setCoverImage(activity.getCoverImage());
        vo.setDescription(activity.getDescription());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setMaxParticipants(activity.getMaxParticipants());
        vo.setCurrentParticipants(activity.getCurrentParticipants());
        vo.setPointsReward(activity.getPointsReward());
        vo.setStatus(activity.getStatus());

        // 检查是否已报名
        if (userId != null) {
            Long count = registrationMapper.selectCount(
                    new LambdaQueryWrapper<ActivityRegistration>()
                            .eq(ActivityRegistration::getActivityId, activity.getId())
                            .eq(ActivityRegistration::getUserId, userId)
            );
            vo.setIsRegistered(count > 0);

            if (count > 0) {
                ActivityRegistration registration = registrationMapper.selectOne(
                        new LambdaQueryWrapper<ActivityRegistration>()
                                .eq(ActivityRegistration::getActivityId, activity.getId())
                                .eq(ActivityRegistration::getUserId, userId)
                                .last("LIMIT 1")
                );
                if (registration != null) {
                    vo.setRegisterTime(registration.getCreateTime());
                }
            }
        } else {
            vo.setIsRegistered(false);
        }

        return vo;
    }
}