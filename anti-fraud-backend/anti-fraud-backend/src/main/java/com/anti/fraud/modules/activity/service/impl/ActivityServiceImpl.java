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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务实现类
 * <p>
 * 提供活动管理的核心业务逻辑，包括活动的CRUD、用户报名管理、活动状态自动更新等功能。
 * 所有涉及数据修改的操作都使用事务保证数据一致性。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;

    /**
     * 分页查询活动列表
     * <p>
     * 支持按活动状态和类型进行筛选，按创建时间倒序排列。
     * </p>
     *
     * @param page   页码，从1开始
     * @param size   每页记录数
     * @param status 活动状态筛选条件（可选）
     * @param type   活动类型筛选条件（可选）
     * @return 分页后的活动视图对象列表
     */
    @Override
    public Page<ActivityVO> getActivityPage(Integer page, Integer size, Integer status, Integer type) {
        log.debug("查询活动分页列表: page={}, size={}, status={}, type={}", page, size, status, type);
        
        // 构建分页对象
        Page<Activity> activityPage = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 按状态筛选
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }

        // 按类型筛选
        if (type != null) {
            wrapper.eq(Activity::getActivityType, type);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Activity::getCreateTime);

        // 执行分页查询
        activityMapper.selectPage(activityPage, wrapper);

        // 获取当前用户ID用于检查报名状态
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("查询到{}条活动记录", activityPage.getTotal());

        // 手动转换避免类型不兼容
        Page<ActivityVO> result = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        result.setRecords(activityPage.getRecords().stream().map(activity -> convertToActivityVO(activity, userId)).collect(Collectors.toList()));
        return result;
    }

    /**
     * 获取活动详情
     * <p>
     * 根据活动ID获取详细信息，包括当前用户的报名状态。
     * </p>
     *
     * @param id 活动ID
     * @return 活动详情视图对象
     * @throws BusinessException 当活动不存在时抛出
     */
    @Override
    public ActivityVO getActivityDetail(Long id) {
        log.debug("查询活动详情: id={}", id);
        
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            log.warn("活动不存在: id={}", id);
            throw new BusinessException("活动不存在");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        return convertToActivityVO(activity, userId);
    }

    /**
     * 创建新活动
     * <p>
     * 根据创建信息创建新活动，系统会自动根据时间设置初始状态：
     * - 如果开始时间在当前时间之后，状态设为"报名中"(1)
     * - 如果开始时间已过但结束时间未过，状态设为"进行中"(2)
     * - 如果结束时间已过，状态设为"已结束"(3)
     * </p>
     *
     * @param createDTO 活动创建信息
     * @throws BusinessException 当数据验证失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityCreateDTO createDTO) {
        log.info("创建新活动: name={}, type={}, startTime={}", 
                createDTO.getActivityName(), createDTO.getActivityType(), createDTO.getStartTime());
        
        // 构建活动实体
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
            log.debug("活动状态设置为: 报名中");
        } else if (createDTO.getEndTime().isAfter(now)) {
            activity.setStatus(2); // 进行中
            log.debug("活动状态设置为: 进行中");
        } else {
            activity.setStatus(3); // 已结束
            log.debug("活动状态设置为: 已结束");
        }

        // 保存到数据库
        activityMapper.insert(activity);
        log.info("活动创建成功: id={}", activity.getId());
    }

    /**
     * 更新活动信息
     * <p>
     * 更新指定活动的信息，注意不会自动更新活动状态。
     * </p>
     *
     * @param id        活动ID
     * @param createDTO 活动更新信息
     * @throws BusinessException 当活动不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, ActivityCreateDTO createDTO) {
        log.info("更新活动信息: id={}", id);
        
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            log.warn("更新活动失败，活动不存在: id={}", id);
            throw new BusinessException("活动不存在");
        }

        // 更新活动信息
        activity.setActivityName(createDTO.getActivityName());
        activity.setActivityType(createDTO.getActivityType());
        activity.setCoverImage(createDTO.getCoverImage());
        activity.setDescription(createDTO.getDescription());
        activity.setStartTime(createDTO.getStartTime());
        activity.setEndTime(createDTO.getEndTime());
        activity.setMaxParticipants(createDTO.getMaxParticipants());
        activity.setPointsReward(createDTO.getPointsReward());

        activityMapper.updateById(activity);
        log.info("活动更新成功: id={}", id);
    }

    /**
     * 删除活动
     * <p>
     * 物理删除活动记录（由于使用了@TableLogic注解，实际为逻辑删除）。
     * </p>
     *
     * @param id 活动ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        log.info("删除活动: id={}", id);
        activityMapper.deleteById(id);
        log.info("活动删除成功: id={}", id);
    }

    /**
     * 用户报名参加活动
     * <p>
     * 业务逻辑：
     * 1. 验证用户登录状态
     * 2. 验证活动存在性和状态
     * 3. 检查报名人数是否已满
     * 4. 检查用户是否已报名
     * 5. 创建报名记录
     * 6. 更新活动的当前参与人数
     * </p>
     *
     * @param activityId 活动ID
     * @throws BusinessException 当用户未登录、活动不存在、活动不在报名阶段、人数已满或已报名时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerActivity(Long activityId) {
        log.info("用户报名活动: activityId={}", activityId);
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            log.warn("报名失败：用户未登录");
            throw new BusinessException("请先登录");
        }

        // 查询活动信息
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            log.warn("报名失败：活动不存在, activityId={}", activityId);
            throw new BusinessException("活动不存在");
        }

        // 检查活动是否在报名阶段
        if (activity.getStatus() != 1) {
            log.warn("报名失败：活动不在报名阶段, activityId={}, status={}", activityId, activity.getStatus());
            throw new BusinessException("活动不在报名阶段");
        }

        // 检查报名人数是否已满
        if (activity.getMaxParticipants() != null &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            log.warn("报名失败：报名人数已满, activityId={}, max={}, current={}", 
                    activityId, activity.getMaxParticipants(), activity.getCurrentParticipants());
            throw new BusinessException("报名人数已满");
        }

        // 检查是否已报名
        Long count = registrationMapper.selectCount(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId)
        );

        if (count > 0) {
            log.warn("报名失败：用户已报名, userId={}, activityId={}", userId, activityId);
            throw new BusinessException("您已报名此活动");
        }

        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setStatus(1);
        registrationMapper.insert(registration);

        // 更新参与人数（使用乐观锁机制）
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
        
        log.info("用户报名成功: userId={}, activityId={}", userId, activityId);
    }

    /**
     * 取消活动报名
     * <p>
     * 用户可以取消已报名的活动，系统会更新活动的参与人数。
     * </p>
     *
     * @param activityId 活动ID
     * @throws BusinessException 当用户未登录或活动不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRegistration(Long activityId) {
        log.info("用户取消报名: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            log.warn("取消报名失败：用户未登录");
            throw new BusinessException("请先登录");
        }

        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            log.warn("取消报名失败：活动不存在, activityId={}", activityId);
            throw new BusinessException("活动不存在");
        }

        // 删除报名记录
        int deleted = registrationMapper.delete(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId)
        );

        // 更新参与人数
        if (deleted > 0 && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityMapper.updateById(activity);
            log.info("取消报名成功，更新参与人数: activityId={}, currentParticipants={}", 
                    activityId, activity.getCurrentParticipants());
        } else {
            log.warn("取消报名失败：未找到报名记录, activityId={}, userId={}", activityId, userId);
        }
    }

    /**
     * 获取当前用户报名的活动列表
     * <p>
     * 分页查询当前用户已报名的所有活动。
     * </p>
     *
     * @param page 页码
     * @param size 每页数量
     * @return 用户报名的活动分页列表
     * @throws BusinessException 当用户未登录时抛出
     */
    @Override
    public Page<ActivityVO> getMyActivities(Integer page, Integer size) {
        log.debug("查询用户报名的活动: page={}, size={}", page, size);
        
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            log.warn("查询失败：用户未登录");
            throw new BusinessException("请先登录");
        }

        // 查询用户的报名记录
        List<ActivityRegistration> registrations = registrationMapper.selectList(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getUserId, userId)
                        .orderByDesc(ActivityRegistration::getCreateTime)
        );

        // 提取活动ID列表
        List<Long> activityIds = registrations.stream()
                .map(ActivityRegistration::getActivityId)
                .collect(Collectors.toList());

        if (activityIds.isEmpty()) {
            log.debug("用户暂无报名记录: userId={}", userId);
            return new Page<>(page, size);
        }

        // 分页查询活动详情
        Page<Activity> activityPage = new Page<>(page, size);
        activityMapper.selectPage(activityPage,
                new LambdaQueryWrapper<Activity>()
                        .in(Activity::getId, activityIds)
        );

        // 转换为视图对象
        Page<ActivityVO> result = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        result.setRecords(activityPage.getRecords().stream().map(activity -> convertToActivityVO(activity, userId)).collect(Collectors.toList()));
        
        log.debug("查询到{}条报名记录", activityPage.getTotal());
        return result;
    }

    /**
     * 获取进行中的活动列表
     * <p>
     * 查询当前处于报名中或进行中的活动，最多返回10条，按开始时间升序排列。
     * </p>
     *
     * @return 进行中的活动列表
     */
    @Override
    public List<ActivityVO> getOngoingActivities() {
        log.debug("查询进行中的活动");
        
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .in(Activity::getStatus, 1, 2)
                        .orderByAsc(Activity::getStartTime)
                        .last("LIMIT 10")
        );

        Long userId = SecurityUtils.getCurrentUserId();

        List<ActivityVO> result = activities.stream()
                .map(activity -> convertToActivityVO(activity, userId))
                .collect(Collectors.toList());
                
        log.debug("查询到{}个进行中的活动", result.size());
        return result;
    }

    /**
     * 定时更新活动状态
     * <p>
     * 由调度任务调用，用于自动更新活动状态：
     * - 将开始时间已到的"报名中"活动更新为"进行中"
     * - 将结束时间已到的"进行中"活动更新为"已结束"
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus() {
        log.info("开始更新活动状态");
        LocalDateTime now = LocalDateTime.now();

        // 更新报名中的活动为进行中
        List<Activity> startingActivities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 1)
                        .le(Activity::getStartTime, now)
        );
        startingActivities.forEach(activity -> {
            activity.setStatus(2);
            activityMapper.updateById(activity);
            log.debug("活动状态更新为进行中: id={}", activity.getId());
        });

        // 更新进行中的活动为已结束
        List<Activity> endingActivities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getStatus, 2)
                        .lt(Activity::getEndTime, now)
        );
        endingActivities.forEach(activity -> {
            activity.setStatus(3);
            activityMapper.updateById(activity);
            log.debug("活动状态更新为已结束: id={}", activity.getId());
        });
        
        log.info("活动状态更新完成: 开始{}个, 结束{}个", startingActivities.size(), endingActivities.size());
    }

    /**
     * 将活动实体转换为视图对象
     * <p>
     * 在转换过程中会查询当前用户对该活动的报名状态。
     * </p>
     *
     * @param activity 活动实体
     * @param userId    当前用户ID（可为null表示未登录）
     * @return 活动视图对象
     */
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
            // 查询报名数量
            Long count = registrationMapper.selectCount(
                    new LambdaQueryWrapper<ActivityRegistration>()
                            .eq(ActivityRegistration::getActivityId, activity.getId())
                            .eq(ActivityRegistration::getUserId, userId)
            );
            vo.setIsRegistered(count > 0);

            // 查询报名记录获取报名时间
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
