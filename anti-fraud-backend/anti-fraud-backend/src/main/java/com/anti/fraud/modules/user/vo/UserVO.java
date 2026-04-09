package com.anti.fraud.modules.user.vo;

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

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
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
}
