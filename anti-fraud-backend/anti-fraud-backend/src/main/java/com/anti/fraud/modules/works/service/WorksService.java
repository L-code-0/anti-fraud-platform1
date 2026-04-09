package com.anti.fraud.modules.works.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.modules.works.dto.WorksSubmitDTO;
import com.anti.fraud.modules.works.vo.WorksVO;

public interface WorksService {
    /**
     * 提交作品
     */
    Long submitWorks(WorksSubmitDTO dto);

    /**
     * 获取我的作品列表
     */
    Page<WorksVO> getMyWorks(Integer page, Integer size);

    /**
     * 获取作品列表（公开）
     */
    Page<WorksVO> getWorksList(Integer page, Integer size, String worksType, Long activityId, Boolean excellent);

    /**
     * 获取作品详情
     */
    WorksVO getWorksDetail(Long id);

    /**
     * 审核作品（管理员）
     */
    void auditWorks(Long id, Integer status, String remark);

    /**
     * 设置优秀作品
     */
    void setExcellent(Long id, Boolean excellent, Integer rank, Integer points);

    /**
     * 删除作品
     */
    void deleteWorks(Long id);

    /**
     * 点赞作品
     */
    void likeWorks(Long id);
}