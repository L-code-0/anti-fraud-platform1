import request from '@/utils/request'

/**
 * 发送简单邮件
 */
export function sendSimpleMail(data: {
  to: string
  subject: string
  content: string
}) {
  return request({
    url: '/api/mail/send/simple',
    method: 'post',
    params: data
  })
}

/**
 * 发送HTML邮件
 */
export function sendHtmlMail(data: {
  to: string
  subject: string
  htmlContent: string
}) {
  return request({
    url: '/api/mail/send/html',
    method: 'post',
    params: data
  })
}

/**
 * 发送带附件的邮件
 */
export function sendAttachmentsMail(data: {
  to: string
  subject: string
  content: string
  filePath: string
}) {
  return request({
    url: '/api/mail/send/attachments',
    method: 'post',
    params: data
  })
}

/**
 * 发送模板邮件
 */
export function sendTemplateMail(data: {
  to: string
  subject: string
  templateName: string
  templateParams: Record<string, any>
}) {
  return request({
    url: '/api/mail/send/template',
    method: 'post',
    params: {
      to: data.to,
      subject: data.subject,
      templateName: data.templateName
    },
    data: data.templateParams
  })
}
