/**
 * 文件上传工具类
 */
import request from './request'

/**
 * 上传单个文件
 * @param file 要上传的文件
 * @param module 模块名称
 * @returns 上传结果
 */
export async function uploadFile(file: File, module: string = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('module', module)
  
  return request({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传文件
 * @param files 要上传的文件数组
 * @param module 模块名称
 * @returns 上传结果
 */
export async function uploadFiles(files: File[], module: string = 'common') {
  const formData = new FormData()
  files.forEach((file, index) => {
    formData.append('files', file)
  })
  formData.append('module', module)
  
  return request({
    url: '/api/file/upload/batch',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 * @param filePath 文件路径
 * @returns 删除结果
 */
export async function deleteFile(filePath: string) {
  return request({
    url: '/api/file/delete',
    method: 'delete',
    params: {
      filePath
    }
  })
}

/**
 * 获取文件访问URL
 * @param filePath 文件路径
 * @returns 文件访问URL
 */
export async function getFileUrl(filePath: string) {
  return request({
    url: '/api/file/url',
    method: 'get',
    params: {
      filePath
    }
  })
}
