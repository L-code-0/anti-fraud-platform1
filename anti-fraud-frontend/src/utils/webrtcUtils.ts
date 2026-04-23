import { ref, computed } from 'vue';

/**
 * WebRTC工具类
 */
export class WebRTCUtils {
  private peerConnection: RTCPeerConnection | null = null;
  private dataChannel: RTCDataChannel | null = null;
  private localStream: MediaStream | null = null;
  private remoteStreams: Map<string, MediaStream> = new Map();
  private iceServers: RTCIceServer[] = [
    {
      urls: ['stun:stun.l.google.com:19302', 'stun:stun1.l.google.com:19302', 'stun:stun2.l.google.com:19302']
    }
  ];
  
  // 状态管理
  private _isConnected = ref(false);
  private _isInCall = ref(false);
  private _error = ref<string | null>(null);
  
  public isConnected = computed(() => this._isConnected.value);
  public isInCall = computed(() => this._isInCall.value);
  public error = computed(() => this._error.value);
  
  /**
   * 初始化WebRTC连接
   */
  public async initialize(): Promise<void> {
    try {
      // 检查浏览器是否支持WebRTC
      if (!('RTCPeerConnection' in window)) {
        throw new Error('WebRTC is not supported in this browser');
      }
      
      // 创建PeerConnection
      this.peerConnection = new RTCPeerConnection({ iceServers: this.iceServers });
      
      // 监听ICE候选事件
      this.peerConnection.onicecandidate = (event) => {
        if (event.candidate) {
          this.onIceCandidate(event.candidate);
        }
      };
      
      // 监听远程流事件
      this.peerConnection.ontrack = (event) => {
        this.onTrack(event);
      };
      
      // 监听连接状态变化
      this.peerConnection.onconnectionstatechange = () => {
        this.onConnectionStateChange();
      };
      
      // 监听数据通道事件
      this.peerConnection.ondatachannel = (event) => {
        this.onDataChannel(event);
      };
      
      this._isConnected.value = true;
    } catch (error) {
      console.error('Failed to initialize WebRTC:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to initialize WebRTC';
      throw error;
    }
  }
  
  /**
   * 获取本地媒体流
   * @param constraints 媒体约束
   */
  public async getLocalStream(constraints: MediaStreamConstraints = {
    video: true,
    audio: true
  }): Promise<MediaStream> {
    try {
      if (!('mediaDevices' in navigator)) {
        throw new Error('Media devices are not supported in this browser');
      }
      
      this.localStream = await navigator.mediaDevices.getUserMedia(constraints);
      return this.localStream;
    } catch (error) {
      console.error('Failed to get local stream:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to get local stream';
      throw error;
    }
  }
  
  /**
   * 添加本地流到PeerConnection
   * @param stream 本地媒体流
   */
  public addLocalStream(stream: MediaStream): void {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    stream.getTracks().forEach(track => {
      this.peerConnection?.addTrack(track, stream);
    });
  }
  
  /**
   * 创建数据通道
   * @param label 通道标签
   */
  public createDataChannel(label: string = 'data'): RTCDataChannel {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    this.dataChannel = this.peerConnection.createDataChannel(label, {
      ordered: true,
      maxRetransmits: 3
    });
    
    this.setupDataChannel(this.dataChannel);
    return this.dataChannel;
  }
  
  /**
   * 建立数据通道
   * @param channel 数据通道
   */
  private setupDataChannel(channel: RTCDataChannel): void {
    channel.onopen = () => {
      console.log('Data channel opened');
    };
    
    channel.onclose = () => {
      console.log('Data channel closed');
    };
    
    channel.onerror = (error) => {
      console.error('Data channel error:', error);
      this._error.value = error instanceof Error ? error.message : 'Data channel error';
    };
    
    channel.onmessage = (event) => {
      this.onMessage(event);
    };
  }
  
  /**
   * 创建offer
   */
  public async createOffer(): Promise<RTCSessionDescriptionInit> {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    try {
      const offer = await this.peerConnection.createOffer();
      await this.peerConnection.setLocalDescription(offer);
      return offer;
    } catch (error) {
      console.error('Failed to create offer:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to create offer';
      throw error;
    }
  }
  
  /**
   * 创建answer
   * @param offer 收到的offer
   */
  public async createAnswer(offer: RTCSessionDescriptionInit): Promise<RTCSessionDescriptionInit> {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    try {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
      const answer = await this.peerConnection.createAnswer();
      await this.peerConnection.setLocalDescription(answer);
      return answer;
    } catch (error) {
      console.error('Failed to create answer:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to create answer';
      throw error;
    }
  }
  
  /**
   * 设置远程描述
   * @param description 远程描述
   */
  public async setRemoteDescription(description: RTCSessionDescriptionInit): Promise<void> {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    try {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(description));
    } catch (error) {
      console.error('Failed to set remote description:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to set remote description';
      throw error;
    }
  }
  
  /**
   * 添加ICE候选
   * @param candidate ICE候选
   */
  public async addIceCandidate(candidate: RTCIceCandidateInit): Promise<void> {
    if (!this.peerConnection) {
      throw new Error('PeerConnection is not initialized');
    }
    
    try {
      await this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    } catch (error) {
      console.error('Failed to add ICE candidate:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to add ICE candidate';
      throw error;
    }
  }
  
  /**
   * 发送消息
   * @param message 消息内容
   */
  public sendMessage(message: any): void {
    if (!this.dataChannel || this.dataChannel.readyState !== 'open') {
      throw new Error('Data channel is not open');
    }
    
    try {
      this.dataChannel.send(JSON.stringify(message));
    } catch (error) {
      console.error('Failed to send message:', error);
      this._error.value = error instanceof Error ? error.message : 'Failed to send message';
      throw error;
    }
  }
  
  /**
   * 关闭连接
   */
  public close(): void {
    // 关闭数据通道
    if (this.dataChannel) {
      this.dataChannel.close();
      this.dataChannel = null;
    }
    
    // 停止本地流
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }
    
    // 关闭PeerConnection
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
    
    // 清除远程流
    this.remoteStreams.clear();
    
    // 重置状态
    this._isConnected.value = false;
    this._isInCall.value = false;
    this._error.value = null;
  }
  
  /**
   * 监听ICE候选事件
   * @param candidate ICE候选
   */
  protected onIceCandidate(candidate: RTCIceCandidate): void {
    // 子类实现
    console.log('ICE candidate:', candidate);
  }
  
  /**
   * 监听远程流事件
   * @param event 轨道事件
   */
  protected onTrack(event: RTCTrackEvent): void {
    // 子类实现
    console.log('Remote track:', event.streams[0]);
    if (event.streams && event.streams.length > 0) {
      this.remoteStreams.set('remote', event.streams[0]);
    }
  }
  
  /**
   * 监听连接状态变化
   */
  protected onConnectionStateChange(): void {
    if (!this.peerConnection) return;
    
    console.log('Connection state:', this.peerConnection.connectionState);
    
    switch (this.peerConnection.connectionState) {
      case 'connected':
        this._isInCall.value = true;
        break;
      case 'disconnected':
      case 'closed':
      case 'failed':
        this._isInCall.value = false;
        break;
    }
  }
  
  /**
   * 监听数据通道事件
   * @param event 数据通道事件
   */
  protected onDataChannel(event: RTCDataChannelEvent): void {
    // 子类实现
    console.log('Data channel received:', event.channel);
    this.dataChannel = event.channel;
    this.setupDataChannel(this.dataChannel);
  }
  
  /**
   * 监听消息事件
   * @param event 消息事件
   */
  protected onMessage(event: MessageEvent): void {
    // 子类实现
    console.log('Message received:', event.data);
  }
  
  /**
   * 获取本地流
   */
  public getLocalStreamInstance(): MediaStream | null {
    return this.localStream;
  }
  
  /**
   * 获取远程流
   * @param id 流ID
   */
  public getRemoteStream(id: string = 'remote'): MediaStream | undefined {
    return this.remoteStreams.get(id);
  }
  
  /**
   * 获取所有远程流
   */
  public getAllRemoteStreams(): Map<string, MediaStream> {
    return this.remoteStreams;
  }
}
