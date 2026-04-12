package com.anti.fraud.modules.sms.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.sms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短信服务控制器
 */
@Tag(name = "短信服务", description = "短信相关操作")
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {
    
    private final SmsService smsService;
    
    @Operation(summary = "发送短信验证码")
    @PostMapping("/send/code")
    public Result<Boolean> sendVerificationCode(
            @Parameter(description = "手机号") @RequestParam("phone") String phone
    ) {
        // 生成验证码
        String code = smsService.generateCode(6);
        // 发送验证码
        boolean result = smsService.sendVerificationCode(phone, code);
        return Result.success(result);
    }
    
    @Operation(summary = "验证短信验证码")
    @PostMapping("/verify/code")
    public Result<Boolean> verifyCode(
            @Parameter(description = "手机号") @RequestParam("phone") String phone,
            @Parameter(description = "验证码") @RequestParam("code") String code
    ) {
        boolean result = smsService.verifyCode(phone, code);
        return Result.success(result);
    }
    
    @Operation(summary = "发送通知短信")
    @PostMapping("/send/notification")
    public Result<Boolean> sendNotification(
            @Parameter(description = "手机号") @RequestParam("phone") String phone,
            @Parameter(description = "模板ID") @RequestParam("templateId") String templateId,
            @Parameter(description = "模板参数") @RequestParam("params") String[] params
    ) {
        boolean result = smsService.sendNotification(phone, templateId, params);
        return Result.success(result);
    }
}
