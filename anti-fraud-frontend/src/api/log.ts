import request from '@/utils/request'

/**
 * 获取操作日志列表
 */
export function getOperationLogList(params: {
  page: number
  size: number
  username?: string
  actionType?: string
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/log/operation/list',
    method: 'get',
    params
  })
}

/**
 * 获取操作日志详情
 */
export function getOperationLogDetail(id: number) {
  return request({
    url: `/api/log/operation/${id}`,
    method: 'get'
  })
}

/**
 * 导出操作日志
 */
export function exportOperationLogs(params: {
  username?: string
  actionType?: string
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/log/operation/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
