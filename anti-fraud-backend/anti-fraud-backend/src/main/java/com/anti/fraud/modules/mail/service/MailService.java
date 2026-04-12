package com.anti.fraud.modules.mail.service;

import java.util.Map;

/**
 * 邮件发送服务
 */
public interface MailService {
    
    /**
     * 发送简单邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendSimpleMail(String to, String subject, String content);
    
    /**
     * 发送HTML邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlMail(String to, String subject, String htmlContent);
    
    /**
     * 发送带附件的邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param filePath 附件路径
     * @return 是否发送成功
     */
    boolean sendAttachmentsMail(String to, String subject, String content, String filePath);
    
    /**
     * 发送模板邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param templateName 模板名称
     * @param templateParams 模板参数
     * @return 是否发送成功
     */
    boolean sendTemplateMail(String to, String subject, String templateName, Map<String, Object> templateParams);
}
