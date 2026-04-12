package com.anti.fraud.modules.mail.service.impl;

import com.anti.fraud.modules.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * 邮件发送服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    @Override
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Simple mail sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Send simple mail failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML mail sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Send HTML mail failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendAttachmentsMail(String to, String subject, String content, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            File file = new File(filePath);
            if (file.exists()) {
                helper.addAttachment(file.getName(), file);
            }
            
            mailSender.send(message);
            log.info("Attachments mail sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Send attachments mail failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendTemplateMail(String to, String subject, String templateName, Map<String, Object> templateParams) {
        try {
            // 这里可以集成模板引擎，如Thymeleaf或Freemarker
            // 目前简单实现，直接使用模板名称作为内容
            String content = "Template: " + templateName + "\nParams: " + templateParams.toString();
            return sendHtmlMail(to, subject, content);
        } catch (Exception e) {
            log.error("Send template mail failed: {}", e.getMessage(), e);
            return false;
        }
    }
}
