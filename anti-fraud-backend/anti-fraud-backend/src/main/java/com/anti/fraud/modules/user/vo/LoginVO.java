package com.anti.fraud.modules.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录返回信息")
public class LoginVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色ID")
    private Integer roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "等级")
    private Integer level;
}
