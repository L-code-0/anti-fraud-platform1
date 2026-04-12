/**
 * WebSocket工具类
 */
class WebSocketClient {
  private ws: WebSocket | null = null
  private url: string = ''
  private reconnectInterval: number = 3000
  private reconnectTimer: number | null = null
  private messageHandlers: Map<string, Function[]> = new Map()
  private isConnected: boolean = false
  
  /**
   * 初始化WebSocket连接
   * @param url WebSocket服务地址
   */
  init(url: string): void {
    this.url = url
    this.connect()
  }
  
  /**
   * 连接WebSocket
   */
  private connect(): void {
    try {
      this.ws = new WebSocket(this.url)
      
      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.isConnected = true
        this.clearReconnectTimer()
      }
      
      this.ws.onmessage = (event) => {
        this.handleMessage(event.data)
      }
      
      this.ws.onclose = () => {
        console.log('WebSocket连接关闭')
        this.isConnected = false
        this.scheduleReconnect()
      }
      
      this.ws.onerror = (error) => {
        console.error('WebSocket错误:', error)
        this.isConnected = false
      }
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.isConnected = false
      this.scheduleReconnect()
    }
  }
  
  /**
   * 处理接收到的消息
   * @param message 消息内容
   */
  private handleMessage(message: string): void {
    try {
      const data = JSON.parse(message)
      const { type, data: messageData } = data
      
      // 调用对应的消息处理器
      if (this.messageHandlers.has(type)) {
        const handlers = this.messageHandlers.get(type)
        handlers?.forEach(handler => {
          try {
            handler(messageData)
          } catch (error) {
            console.error(`处理${type}类型消息失败:`, error)
          }
        })
      }
    } catch (error) {
      console.error('解析WebSocket消息失败:', error)
    }
  }
  
  /**
   * 发送消息
   * @param message 消息内容
   */
  send(message: any): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      try {
        this.ws.send(JSON.stringify(message))
      } catch (error) {
        console.error('发送WebSocket消息失败:', error)
      }
    } else {
      console.warn('WebSocket未连接，无法发送消息')
    }
  }
  
  /**
   * 注册消息处理器
   * @param type 消息类型
   * @param handler 消息处理函数
   */
  on(type: string, handler: Function): void {
    if (!this.messageHandlers.has(type)) {
      this.messageHandlers.set(type, [])
    }
    this.messageHandlers.get(type)?.push(handler)
  }
  
  /**
   * 取消注册消息处理器
   * @param type 消息类型
   * @param handler 消息处理函数
   */
  off(type: string, handler: Function): void {
    if (this.messageHandlers.has(type)) {
      const handlers = this.messageHandlers.get(type)
      const index = handlers?.indexOf(handler)
      if (index !== undefined && index > -1) {
        handlers?.splice(index, 1)
      }
    }
  }
  
  /**
   * 安排重连
   */
  private scheduleReconnect(): void {
    this.clearReconnectTimer()
    this.reconnectTimer = window.setTimeout(() => {
      console.log('尝试重新连接WebSocket...')
      this.connect()
    }, this.reconnectInterval)
  }
  
  /**
   * 清除重连定时器
   */
  private clearReconnectTimer(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }
  
  /**
   * 关闭WebSocket连接
   */
  close(): void {
    this.clearReconnectTimer()
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.isConnected = false
  }
  
  /**
   * 获取连接状态
   * @return 是否连接
   */
  getConnected(): boolean {
    return this.isConnected
  }
}

// 导出单例实例
export const webSocketClient = new WebSocketClient()
