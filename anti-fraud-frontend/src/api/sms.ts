import request from '@/utils/request'

/**
 * 发送短信验证码
 */
export function sendVerificationCode(phone: string) {
  return request({
    url: '/api/sms/send/code',
    method: 'post',
    params: {
      phone
    }
  })
}

/**
 * 验证短信验证码
 */
export function verifyCode(data: {
  phone: string
  code: string
}) {
  return request({
    url: '/api/sms/verify/code',
    method: 'post',
    params: data
  })
}

/**
 * 发送通知短信
 */
export function sendNotification(data: {
  phone: string
  templateId: string
  params: string[]
}) {
  return request({
    url: '/api/sms/send/notification',
    method: 'post',
    params: data
  })
}
