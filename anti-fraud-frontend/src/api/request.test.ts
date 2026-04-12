import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import axios from 'axios'
import { request, get, post, put, del } from './request'
import { secureStorage } from '@/utils/security'

// 模拟依赖
vi.mock('axios')
vi.mock('@/utils/security')
vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
    warning: vi.fn()
  }
}))

const mockAxios = axios as jest.Mocked<typeof axios>
const mockSecureStorage = secureStorage as jest.Mocked<typeof secureStorage>

describe('API Request Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('request function', () => {
    it('should make a successful request', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Test' }
      }

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockResolvedValue({ data: mockResponse })
      })) as any

      const config = { method: 'GET', url: '/test' }
      const result = await request(config)

      expect(result).toEqual(mockResponse)
    })

    it('should handle request failure', async () => {
      const mockError = new Error('Network Error')

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockRejectedValue(mockError)
      })) as any

      const config = { method: 'GET', url: '/test' }
      await expect(request(config)).rejects.toThrow('Network Error')
    })
  })

  describe('HTTP methods', () => {
    it('should make a GET request', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Test' }
      }

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockResolvedValue({ data: mockResponse })
      })) as any

      const result = await get('/test', { id: 1 })
      expect(result).toEqual(mockResponse)
    })

    it('should make a POST request', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Test' }
      }

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockResolvedValue({ data: mockResponse })
      })) as any

      const result = await post('/test', { name: 'Test' })
      expect(result).toEqual(mockResponse)
    })

    it('should make a PUT request', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Updated Test' }
      }

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockResolvedValue({ data: mockResponse })
      })) as any

      const result = await put('/test', { id: 1, name: 'Updated Test' })
      expect(result).toEqual(mockResponse)
    })

    it('should make a DELETE request', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { success: true }
      }

      mockAxios.create = vi.fn(() => ({
        request: vi.fn().mockResolvedValue({ data: mockResponse })
      })) as any

      const result = await del('/test')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('Request Interceptor', () => {
    it('should add token to headers if token exists', () => {
      mockSecureStorage.getItem = vi.fn().mockReturnValue('test-token')

      mockAxios.create = vi.fn(() => ({
        interceptors: {
          request: {
            use: vi.fn((config) => {
              expect(config.headers.Authorization).toBe('Bearer test-token')
              return config
            })
          },
          response: {
            use: vi.fn()
          }
        },
        request: vi.fn()
      })) as any

      // 触发初始化
      import('./request')
    })

    it('should not add token to headers if token does not exist', () => {
      mockSecureStorage.getItem = vi.fn().mockReturnValue(null)

      mockAxios.create = vi.fn(() => ({
        interceptors: {
          request: {
            use: vi.fn((config) => {
              expect(config.headers.Authorization).toBeUndefined()
              return config
            })
          },
          response: {
            use: vi.fn()
          }
        },
        request: vi.fn()
      })) as any

      // 触发初始化
      import('./request')
    })
  })

  describe('Response Interceptor', () => {
    it('should handle successful response', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Test' }
      }

      mockAxios.create = vi.fn(() => ({
        interceptors: {
          request: {
            use: vi.fn((config) => config)
          },
          response: {
            use: vi.fn((successHandler) => {
              const result = successHandler({ data: mockResponse } as any)
              expect(result).toEqual(mockResponse)
            })
          }
        },
        request: vi.fn()
      })) as any

      // 触发初始化
      import('./request')
    })

    it('should handle error response', async () => {
      const mockErrorResponse = {
        code: 401,
        message: 'Unauthorized'
      }

      mockSecureStorage.removeItem = vi.fn()
      const mockWindowLocation = vi.spyOn(window.location, 'href', 'set')

      mockAxios.create = vi.fn(() => ({
        interceptors: {
          request: {
            use: vi.fn((config) => config)
          },
          response: {
            use: vi.fn((successHandler, errorHandler) => {
              expect(() => successHandler({ data: mockErrorResponse } as any)).toThrow('Unauthorized')
            })
          }
        },
        request: vi.fn()
      })) as any

      // 触发初始化
      import('./request')
    })
  })

  describe('Request Retry Mechanism', () => {
    it('should retry on network errors', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, name: 'Test' }
      }

      let callCount = 0
      mockAxios.create = vi.fn(() => ({
        interceptors: {
          request: {
            use: vi.fn((config) => config)
          },
          response: {
            use: vi.fn((successHandler, errorHandler) => {
              return {
                request: async (config: any) => {
                  callCount++
                  if (callCount < 3) {
                    throw { code: 'ECONNABORTED' } as any
                  }
                  return { data: mockResponse } as any
                }
              }
            })
          }
        },
        request: vi.fn()
      })) as any

      const config = { method: 'GET', url: '/test' }
      const result = await request(config)

      expect(result).toEqual(mockResponse)
      expect(callCount).toBe(3)
    })
  })
})
