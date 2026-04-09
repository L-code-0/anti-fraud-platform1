package com.anti.fraud.modules.works.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.works.dto.WorksSubmitDTO;
import com.anti.fraud.modules.works.entity.Works;
import com.anti.fraud.modules.works.mapper.WorksMapper;
import com.anti.fraud.modules.works.service.WorksService;
import com.anti.fraud.modules.works.vo.WorksVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorksServiceImpl implements WorksService {

    private final WorksMapper worksMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long submitWorks(WorksSubmitDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        Works works = new Works();
        works.setTitle(dto.getTitle());
        works.setWorksType(dto.getWorksType());
        works.setActivityId(dto.getActivityId());
        works.setAuthorId(userId);
        works.setAuthorName(user.getRealName());
        works.setAuthorPhone(user.getPhone());
        works.setContent(dto.getContent());
        works.setFileUrl(dto.getFileUrl());
        works.setCoverImage(dto.getCoverImage());
        works.setDescription(dto.getDescription());
        works.setStatus(0);
        works.setViewCount(0);
        works.setLikeCount(0);
        works.setIsExcellent(0);
        works.setCreateTime(LocalDateTime.now());

        worksMapper.insert(works);
        return works.getId();
    }

    @Override
    public Page<WorksVO> getMyWorks(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<Works> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Works> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Works::getAuthorId, userId)
                .orderByDesc(Works::getCreateTime);

        Page<Works> result = worksMapper.selectPage(pageParam, wrapper);

        Page<WorksVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Page<WorksVO> getWorksList(Integer page, Integer size, String worksType, Long activityId, Boolean excellent) {
        Page<Works> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Works> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Works::getStatus, 1);

        if (worksType != null) {
            wrapper.eq(Works::getWorksType, worksType);
        }
        if (activityId != null) {
            wrapper.eq(Works::getActivityId, activityId);
        }
        if (Boolean.TRUE.equals(excellent)) {
            wrapper.eq(Works::getIsExcellent, 1);
        }

        wrapper.orderByDesc(Works::getIsExcellent)
                .orderByDesc(Works::getCreateTime);

        Page<Works> result = worksMapper.selectPage(pageParam, wrapper);

        Page<WorksVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public WorksVO getWorksDetail(Long id) {
        Works works = worksMapper.selectById(id);
        if (works == null) {
            throw new BusinessException("作品不存在");
        }

        // 增加浏览量
        works.setViewCount(works.getViewCount() + 1);
        worksMapper.updateById(works);

        return convertToVO(works);
    }

    @Override
    @Transactional
    public void auditWorks(Long id, Integer status, String remark) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        Works works = worksMapper.selectById(id);
        if (works == null) {
            throw new BusinessException("作品不存在");
        }

        works.setStatus(status);
        works.setAuditRemark(remark);
        works.setAuditorId(userId);
        works.setAuditorName(user.getRealName());
        works.setAuditTime(LocalDateTime.now());

        worksMapper.updateById(works);
    }

    @Override
    @Transactional
    public void setExcellent(Long id, Boolean excellent, Integer rank, Integer points) {
        Works works = worksMapper.selectById(id);
        if (works == null) {
            throw new BusinessException("作品不存在");
        }

        works.setIsExcellent(excellent ? 1 : 0);
        works.setRank(rank);
        works.setPoints(points);

        worksMapper.updateById(works);
    }

    @Override
    @Transactional
    public void deleteWorks(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Works works = worksMapper.selectById(id);

        if (works == null) {
            throw new BusinessException("作品不存在");
        }

        if (!works.getAuthorId().equals(userId)) {
            throw new BusinessException("无权删除此作品");
        }

        worksMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void likeWorks(Long id) {
        Works works = worksMapper.selectById(id);
        if (works == null) {
            throw new BusinessException("作品不存在");
        }

        works.setLikeCount(works.getLikeCount() + 1);
        worksMapper.updateById(works);
    }

    private WorksVO convertToVO(Works works) {
        WorksVO vo = new WorksVO();
        vo.setId(works.getId());
        vo.setTitle(works.getTitle());
        vo.setWorksType(works.getWorksType());
        vo.setWorksTypeName(getWorksTypeName(works.getWorksType()));
        vo.setActivityId(works.getActivityId());
        vo.setActivityName(works.getActivityName());
        vo.setAuthorId(works.getAuthorId());
        vo.setAuthorName(works.getAuthorName());
        vo.setDepartment(works.getDepartment());
        vo.setContent(works.getContent());
        vo.setFileUrl(works.getFileUrl());
        vo.setCoverImage(works.getCoverImage());
        vo.setDescription(works.getDescription());
        vo.setStatus(works.getStatus());
        vo.setStatusName(getStatusName(works.getStatus()));
        vo.setAuditRemark(works.getAuditRemark());
        vo.setAuditorName(works.getAuditorName());
        vo.setAuditTime(works.getAuditTime());
        vo.setViewCount(works.getViewCount());
        vo.setLikeCount(works.getLikeCount());
        vo.setIsExcellent(works.getIsExcellent() == 1);
        vo.setRank(works.getRank());
        vo.setPoints(works.getPoints());
        vo.setCreateTime(works.getCreateTime());

        return vo;
    }

    private String getWorksTypeName(String worksType) {
        switch (worksType) {
            case "ESSAY": return "征文";
            case "VIDEO": return "短视频";
            default: return worksType;
        }
    }

    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已拒绝";
            default: return "未知";
        }
    }
}