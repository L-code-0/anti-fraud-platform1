import { WebRTCUtils } from './webrtcUtils';

/**
 * WebRTC连接状态
 */
export enum WebRTCConnectionState {
  DISCONNECTED = 'disconnected',
  CONNECTING = 'connecting',
  CONNECTED = 'connected',
  FAILED = 'failed'
}

/**
 * 消息类型
 */
export enum MessageType {
  JOIN_ROOM = 'join_room',
  LEAVE_ROOM = 'leave_room',
  ICE_CANDIDATE = 'ice_candidate',
  OFFER = 'offer',
  ANSWER = 'answer',
  CHAT = 'chat',
  POSITION = 'position',
  ACTION = 'action'
}

/**
 * 消息结构
 */
export interface WebRTCMessage {
  type: MessageType;
  roomId: string;
  userId: string;
  data: any;
  timestamp: number;
}

/**
 * 用户信息
 */
export interface UserInfo {
  userId: string;
  username: string;
  avatar?: string;
  position?: { x: number; y: number; z: number };
  rotation?: { x: number; y: number; z: number };
}

/**
 * WebRTC管理类
 */
export class WebRTCManager {
  private webrtcUtils: WebRTCUtils;
  private roomId: string = '';
  private userId: string = '';
  private userInfo: UserInfo | null = null;
  private peerConnections: Map<string, RTCPeerConnection> = new Map();
  private dataChannels: Map<string, RTCDataChannel> = new Map();
  private users: Map<string, UserInfo> = new Map();
  private signalingServerUrl: string = '';
  private ws: WebSocket | null = null;
  
  /**
   * 构造函数
   * @param signalingServerUrl 信令服务器地址
   */
  constructor(signalingServerUrl: string) {
    this.signalingServerUrl = signalingServerUrl;
    this.webrtcUtils = new WebRTCUtils();
  }
  
  /**
   * 初始化
   */
  public async initialize(): Promise<void> {
    await this.webrtcUtils.initialize();
  }
  
  /**
   * 连接到信令服务器
   */
  public connectToSignalingServer(): void {
    this.ws = new WebSocket(this.signalingServerUrl);
    
    this.ws.onopen = () => {
      console.log('Connected to signaling server');
    };
    
    this.ws.onmessage = (event) => {
      this.handleSignalingMessage(event.data);
    };
    
    this.ws.onclose = () => {
      console.log('Disconnected from signaling server');
    };
    
    this.ws.onerror = (error) => {
      console.error('Signaling server error:', error);
    };
  }
  
  /**
   * 加入房间
   * @param roomId 房间ID
   * @param userInfo 用户信息
   */
  public async joinRoom(roomId: string, userInfo: UserInfo): Promise<void> {
    this.roomId = roomId;
    this.userId = userInfo.userId;
    this.userInfo = userInfo;
    
    // 发送加入房间消息
    this.sendSignalingMessage({
      type: MessageType.JOIN_ROOM,
      roomId,
      userId: userInfo.userId,
      data: userInfo,
      timestamp: Date.now()
    });
  }
  
  /**
   * 离开房间
   */
  public leaveRoom(): void {
    // 发送离开房间消息
    this.sendSignalingMessage({
      type: MessageType.LEAVE_ROOM,
      roomId: this.roomId,
      userId: this.userId,
      data: {},
      timestamp: Date.now()
    });
    
    // 关闭所有连接
    this.peerConnections.forEach((peerConnection, userId) => {
      peerConnection.close();
    });
    
    this.peerConnections.clear();
    this.dataChannels.clear();
    this.users.clear();
  }
  
  /**
   * 发送信令消息
   * @param message 消息
   */
  private sendSignalingMessage(message: WebRTCMessage): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    }
  }
  
  /**
   * 处理信令消息
   * @param message 消息内容
   */
  private handleSignalingMessage(message: string): void {
    try {
      const parsedMessage: WebRTCMessage = JSON.parse(message);
      
      switch (parsedMessage.type) {
        case MessageType.JOIN_ROOM:
          this.handleJoinRoom(parsedMessage);
          break;
        case MessageType.LEAVE_ROOM:
          this.handleLeaveRoom(parsedMessage);
          break;
        case MessageType.ICE_CANDIDATE:
          this.handleIceCandidate(parsedMessage);
          break;
        case MessageType.OFFER:
          this.handleOffer(parsedMessage);
          break;
        case MessageType.ANSWER:
          this.handleAnswer(parsedMessage);
          break;
        case MessageType.CHAT:
          this.handleChat(parsedMessage);
          break;
        case MessageType.POSITION:
          this.handlePosition(parsedMessage);
          break;
        case MessageType.ACTION:
          this.handleAction(parsedMessage);
          break;
      }
    } catch (error) {
      console.error('Failed to handle signaling message:', error);
    }
  }
  
  /**
   * 处理加入房间消息
   * @param message 消息
   */
  private async handleJoinRoom(message: WebRTCMessage): Promise<void> {
    if (message.userId === this.userId) return;
    
    // 添加用户
    this.users.set(message.userId, message.data);
    
    // 创建PeerConnection
    const peerConnection = new RTCPeerConnection({
      iceServers: [
        {
          urls: ['stun:stun.l.google.com:19302', 'stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302']
        }
      ]
    });
    
    // 监听ICE候选事件
    peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignalingMessage({
          type: MessageType.ICE_CANDIDATE,
          roomId: this.roomId,
          userId: this.userId,
          data: event.candidate,
          timestamp: Date.now()
        });
      }
    };
    
    // 监听远程流事件
    peerConnection.ontrack = (event) => {
      if (event.streams && event.streams.length > 0) {
        // 处理远程流
        this.handleRemoteStream(message.userId, event.streams[0]);
      }
    };
    
    // 监听数据通道事件
    peerConnection.ondatachannel = (event) => {
      const dataChannel = event.channel;
      this.dataChannels.set(message.userId, dataChannel);
      this.setupDataChannel(dataChannel, message.userId);
    };
    
    // 添加本地流
    const localStream = this.webrtcUtils.getLocalStreamInstance();
    if (localStream) {
      localStream.getTracks().forEach(track => {
        peerConnection.addTrack(track, localStream);
      });
    }
    
    // 创建数据通道
    const dataChannel = peerConnection.createDataChannel('data');
    this.dataChannels.set(message.userId, dataChannel);
    this.setupDataChannel(dataChannel, message.userId);
    
    // 创建offer
    const offer = await peerConnection.createOffer();
    await peerConnection.setLocalDescription(offer);
    
    // 发送offer
    this.sendSignalingMessage({
      type: MessageType.OFFER,
      roomId: this.roomId,
      userId: this.userId,
      data: offer,
      timestamp: Date.now()
    });
    
    // 保存PeerConnection
    this.peerConnections.set(message.userId, peerConnection);
  }
  
  /**
   * 处理离开房间消息
   * @param message 消息
   */
  private handleLeaveRoom(message: WebRTCMessage): void {
    if (message.userId === this.userId) return;
    
    // 移除用户
    this.users.delete(message.userId);
    
    // 关闭连接
    const peerConnection = this.peerConnections.get(message.userId);
    if (peerConnection) {
      peerConnection.close();
      this.peerConnections.delete(message.userId);
    }
    
    // 移除数据通道
    this.dataChannels.delete(message.userId);
  }
  
  /**
   * 处理ICE候选消息
   * @param message 消息
   */
  private async handleIceCandidate(message: WebRTCMessage): Promise<void> {
    if (message.userId === this.userId) return;
    
    const peerConnection = this.peerConnections.get(message.userId);
    if (peerConnection) {
      await peerConnection.addIceCandidate(new RTCIceCandidate(message.data));
    }
  }
  
  /**
   * 处理offer消息
   * @param message 消息
   */
  private async handleOffer(message: WebRTCMessage): Promise<void> {
    if (message.userId === this.userId) return;
    
    // 创建PeerConnection
    const peerConnection = new RTCPeerConnection({
      iceServers: [
        {
          urls: ['stun:stun.l.google.com:19302', 'stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302']
        }
      ]
    });
    
    // 监听ICE候选事件
    peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignalingMessage({
          type: MessageType.ICE_CANDIDATE,
          roomId: this.roomId,
          userId: this.userId,
          data: event.candidate,
          timestamp: Date.now()
        });
      }
    };
    
    // 监听远程流事件
    peerConnection.ontrack = (event) => {
      if (event.streams && event.streams.length > 0) {
        // 处理远程流
        this.handleRemoteStream(message.userId, event.streams[0]);
      }
    };
    
    // 监听数据通道事件
    peerConnection.ondatachannel = (event) => {
      const dataChannel = event.channel;
      this.dataChannels.set(message.userId, dataChannel);
      this.setupDataChannel(dataChannel, message.userId);
    };
    
    // 添加本地流
    const localStream = this.webrtcUtils.getLocalStreamInstance();
    if (localStream) {
      localStream.getTracks().forEach(track => {
        peerConnection.addTrack(track, localStream);
      });
    }
    
    // 设置远程描述
    await peerConnection.setRemoteDescription(new RTCSessionDescription(message.data));
    
    // 创建answer
    const answer = await peerConnection.createAnswer();
    await peerConnection.setLocalDescription(answer);
    
    // 发送answer
    this.sendSignalingMessage({
      type: MessageType.ANSWER,
      roomId: this.roomId,
      userId: this.userId,
      data: answer,
      timestamp: Date.now()
    });
    
    // 保存PeerConnection
    this.peerConnections.set(message.userId, peerConnection);
  }
  
  /**
   * 处理answer消息
   * @param message 消息
   */
  private async handleAnswer(message: WebRTCMessage): Promise<void> {
    if (message.userId === this.userId) return;
    
    const peerConnection = this.peerConnections.get(message.userId);
    if (peerConnection) {
      await peerConnection.setRemoteDescription(new RTCSessionDescription(message.data));
    }
  }
  
  /**
   * 处理聊天消息
   * @param message 消息
   */
  private handleChat(message: WebRTCMessage): void {
    if (message.userId === this.userId) return;
    
    // 处理聊天消息
    console.log('Chat message from', message.userId, ':', message.data);
  }
  
  /**
   * 处理位置消息
   * @param message 消息
   */
  private handlePosition(message: WebRTCMessage): void {
    if (message.userId === this.userId) return;
    
    // 更新用户位置
    const user = this.users.get(message.userId);
    if (user) {
      user.position = message.data.position;
      user.rotation = message.data.rotation;
    }
  }
  
  /**
   * 处理动作消息
   * @param message 消息
   */
  private handleAction(message: WebRTCMessage): void {
    if (message.userId === this.userId) return;
    
    // 处理动作消息
    console.log('Action from', message.userId, ':', message.data);
  }
  
  /**
   * 设置数据通道
   * @param channel 数据通道
   * @param userId 用户ID
   */
  private setupDataChannel(channel: RTCDataChannel, userId: string): void {
    channel.onopen = () => {
      console.log('Data channel opened with', userId);
    };
    
    channel.onclose = () => {
      console.log('Data channel closed with', userId);
    };
    
    channel.onerror = (error) => {
      console.error('Data channel error with', userId, ':', error);
    };
    
    channel.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        // 处理消息
        console.log('Message from', userId, ':', message);
      } catch (error) {
        console.error('Failed to parse message from', userId, ':', error);
      }
    };
  }
  
  /**
   * 处理远程流
   * @param userId 用户ID
   * @param stream 远程流
   */
  private handleRemoteStream(userId: string, stream: MediaStream): void {
    // 处理远程流
    console.log('Remote stream from', userId, ':', stream);
  }
  
  /**
   * 发送消息
   * @param type 消息类型
   * @param data 消息数据
   */
  public sendMessage(type: MessageType, data: any): void {
    this.sendSignalingMessage({
      type,
      roomId: this.roomId,
      userId: this.userId,
      data,
      timestamp: Date.now()
    });
  }
  
  /**
   * 发送聊天消息
   * @param content 聊天内容
   */
  public sendChatMessage(content: string): void {
    this.sendMessage(MessageType.CHAT, { content });
  }
  
  /**
   * 发送位置消息
   * @param position 位置
   * @param rotation 旋转
   */
  public sendPositionMessage(position: { x: number; y: number; z: number }, rotation: { x: number; y: number; z: number }): void {
    this.sendMessage(MessageType.POSITION, { position, rotation });
  }
  
  /**
   * 发送动作消息
   * @param action 动作
   * @param data 动作数据
   */
  public sendActionMessage(action: string, data: any): void {
    this.sendMessage(MessageType.ACTION, { action, data });
  }
  
  /**
   * 获取房间内的用户列表
   */
  public getUsers(): Map<string, UserInfo> {
    return this.users;
  }
  
  /**
   * 获取房间ID
   */
  public getRoomId(): string {
    return this.roomId;
  }
  
  /**
   * 获取用户ID
   */
  public getUserId(): string {
    return this.userId;
  }
  
  /**
   * 获取用户信息
   */
  public getUserInfo(): UserInfo | null {
    return this.userInfo;
  }
  
  /**
   * 关闭连接
   */
  public close(): void {
    // 离开房间
    this.leaveRoom();
    
    // 关闭WebSocket连接
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    
    // 关闭WebRTC连接
    this.webrtcUtils.close();
  }
}
