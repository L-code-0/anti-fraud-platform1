package com.anti.fraud.modules.expert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专家案例分析实体
 */
@Data
@TableName("expert_analysis")
public class ExpertAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 案例标题
     */
    private String title;

    /**
     * 案例类型：1-典型案例 2-新型诈骗
     */
    private Integer type;

    /**
     * 案例摘要
     */
    private String summary;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 发布专家ID
     */
    private Long expertId;

    /**
     * 发布专家姓名
     */
    private String expertName;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer thumbCount;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
