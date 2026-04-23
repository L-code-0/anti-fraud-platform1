package com.anti.fraud.modules.collaboration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 协作演练参与者实体
 */
@Data
@TableName("collaboration_participant")
public class CollaborationParticipant {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色：1-主持人，2-参与者，3-观察者
     */
    private Integer role;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 状态：1-在线，2-离线
     */
    private Integer status;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 完成情况：1-已完成，2-未完成
     */
    private Integer completionStatus;

    /**
     * 创建时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Integer deleted;
}
