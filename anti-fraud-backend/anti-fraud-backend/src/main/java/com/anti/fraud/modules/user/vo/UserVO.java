package com.anti.fraud.modules.user.vo;

import com.anti.fraud.common.utils.DesensitizationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号（脱敏后）")
    private String phone;

    @Schema(description = "邮箱（脱敏后）")
    private String email;

    @Schema(description = "学号/工号")
    private String userNo;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "院系ID")
    private Long departmentId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "角色ID")
    private Integer roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "等级")
    private Integer level;

    /**
     * 敏感字段的原始值（仅用于内部处理，不返回给前端）
     */
    @JsonIgnore
    private String rawPhone;

    @JsonIgnore
    private String rawEmail;

    @JsonIgnore
    private String rawRealName;

    /**
     * 获取脱敏后的手机号
     */
    public String getPhone() {
        if (phone != null) {
            return phone;
        }
        if (rawPhone != null) {
            return DesensitizationUtil.desensitizePhone(rawPhone);
        }
        return null;
    }

    /**
     * 获取脱敏后的邮箱
     */
    public String getEmail() {
        if (email != null) {
            return email;
        }
        if (rawEmail != null) {
            return DesensitizationUtil.desensitizeEmail(rawEmail);
        }
        return null;
    }

    /**
     * 获取脱敏后的真实姓名
     */
    public String getRealName() {
        if (realName != null && !realName.contains("*")) {
            return DesensitizationUtil.desensitizeRealName(realName);
        }
        return realName;
    }

    /**
     * 设置原始手机号（用于后续脱敏）
     */
    public void setRawPhone(String rawPhone) {
        this.rawPhone = rawPhone;
    }

    /**
     * 设置原始邮箱（用于后续脱敏）
     */
    public void setRawEmail(String rawEmail) {
        this.rawEmail = rawEmail;
    }

    /**
     * 设置原始真实姓名（用于后续脱敏）
     */
    public void setRawRealName(String rawRealName) {
        this.rawRealName = rawRealName;
    }

    /**
     * 设置原始手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 设置原始邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 设置原始真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }
}
