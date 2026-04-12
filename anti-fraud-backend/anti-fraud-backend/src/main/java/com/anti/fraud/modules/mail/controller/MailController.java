package com.anti.fraud.modules.mail.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮件发送控制器
 */
@Tag(name = "邮件发送", description = "邮件发送相关操作")
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {
    
    private final MailService mailService;
    
    @Operation(summary = "发送简单邮件")
    @PostMapping("/send/simple")
    public Result<Boolean> sendSimpleMail(
            @Parameter(description = "收件人邮箱") @RequestParam("to") String to,
            @Parameter(description = "邮件主题") @RequestParam("subject") String subject,
            @Parameter(description = "邮件内容") @RequestParam("content") String content
    ) {
        boolean result = mailService.sendSimpleMail(to, subject, content);
        return Result.success(result);
    }
    
    @Operation(summary = "发送HTML邮件")
    @PostMapping("/send/html")
    public Result<Boolean> sendHtmlMail(
            @Parameter(description = "收件人邮箱") @RequestParam("to") String to,
            @Parameter(description = "邮件主题") @RequestParam("subject") String subject,
            @Parameter(description = "HTML内容") @RequestParam("htmlContent") String htmlContent
    ) {
        boolean result = mailService.sendHtmlMail(to, subject, htmlContent);
        return Result.success(result);
    }
    
    @Operation(summary = "发送带附件的邮件")
    @PostMapping("/send/attachments")
    public Result<Boolean> sendAttachmentsMail(
            @Parameter(description = "收件人邮箱") @RequestParam("to") String to,
            @Parameter(description = "邮件主题") @RequestParam("subject") String subject,
            @Parameter(description = "邮件内容") @RequestParam("content") String content,
            @Parameter(description = "附件路径") @RequestParam("filePath") String filePath
    ) {
        boolean result = mailService.sendAttachmentsMail(to, subject, content, filePath);
        return Result.success(result);
    }
    
    @Operation(summary = "发送模板邮件")
    @PostMapping("/send/template")
    public Result<Boolean> sendTemplateMail(
            @Parameter(description = "收件人邮箱") @RequestParam("to") String to,
            @Parameter(description = "邮件主题") @RequestParam("subject") String subject,
            @Parameter(description = "模板名称") @RequestParam("templateName") String templateName,
            @Parameter(description = "模板参数") @RequestBody Map<String, Object> templateParams
    ) {
        boolean result = mailService.sendTemplateMail(to, subject, templateName, templateParams);
        return Result.success(result);
    }
}
