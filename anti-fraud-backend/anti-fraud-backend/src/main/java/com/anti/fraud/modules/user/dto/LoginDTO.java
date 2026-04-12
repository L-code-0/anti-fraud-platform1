package com.anti.fraud.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100个字符")
    private String password;

    @Schema(description = "验证码Key（从获取验证码接口获得）")
    @Size(max = 100, message = "验证码Key长度不能超过100个字符")
    private String captchaKey;

    @Schema(description = "验证码答案")
    @Size(max = 10, message = "验证码答案长度不能超过10个字符")
    private String captchaCode;
}
