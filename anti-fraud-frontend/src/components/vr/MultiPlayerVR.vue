<template>
  <div class="multi-player-vr">
    <el-card class="room-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('multiPlayerVR.roomManagement') }}</span>
        </div>
      </template>
      
      <div class="room-management">
        <!-- 房间创建 -->
        <el-form :inline="true" @submit.prevent="createRoom">
          <el-form-item :label="$t('multiPlayerVR.roomName')">
            <el-input v-model="roomForm.name" placeholder="{{ $t('multiPlayerVR.enterRoomName') }}" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit">{{ $t('multiPlayerVR.createRoom') }}</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 房间加入 -->
        <el-form :inline="true" @submit.prevent="joinRoom">
          <el-form-item :label="$t('multiPlayerVR.roomId')">
            <el-input v-model="roomForm.id" placeholder="{{ $t('multiPlayerVR.enterRoomId') }}" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" native-type="submit">{{ $t('multiPlayerVR.joinRoom') }}</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 房间信息 -->
        <div v-if="isInRoom" class="room-info">
          <el-divider>{{ $t('multiPlayerVR.roomInfo') }}</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item :label="$t('multiPlayerVR.roomId')">{{ roomId }}</el-descriptions-item>
            <el-descriptions-item :label="$t('multiPlayerVR.roomName')">{{ roomName }}</el-descriptions-item>
            <el-descriptions-item :label="$t('multiPlayerVR.userCount')">{{ users.size }}</el-descriptions-item>
            <el-descriptions-item :label="$t('multiPlayerVR.status')">
              <el-tag type="success" size="small">{{ $t('multiPlayerVR.connected') }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
          <el-button type="danger" @click="leaveRoom" style="margin-top: 10px;">{{ $t('multiPlayerVR.leaveRoom') }}</el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 用户列表 -->
    <el-card class="users-card" v-if="isInRoom">
      <template #header>
        <div class="card-header">
          <span>{{ $t('multiPlayerVR.usersInRoom') }}</span>
        </div>
      </template>
      
      <el-list>
        <el-list-item v-for="user in users.values()" :key="user.userId">
          <template #prefix>
            <el-avatar :size="40" :src="user.avatar || ''">
              {{ user.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
          <div class="user-info">
            <div class="user-name">{{ user.username }}</div>
            <div class="user-position" v-if="user.position">
              {{ $t('multiPlayerVR.position') }}: ({{ user.position.x.toFixed(2) }}, {{ user.position.y.toFixed(2) }}, {{ user.position.z.toFixed(2) }})
            </div>
          </div>
        </el-list-item>
      </el-list>
    </el-card>
    
    <!-- 聊天功能 -->
    <el-card class="chat-card" v-if="isInRoom">
      <template #header>
        <div class="card-header">
          <span>{{ $t('multiPlayerVR.chat') }}</span>
        </div>
      </template>
      
      <div class="chat-container">
        <div class="chat-messages">
          <div v-for="message in chatMessages" :key="message.id" class="chat-message" :class="{ 'own': message.userId === userId }">
            <div class="message-avatar">{{ message.username.charAt(0).toUpperCase() }}</div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-username">{{ message.username }}</span>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
              <div class="message-text">{{ message.content }}</div>
            </div>
          </div>
        </div>
        
        <el-form @submit.prevent="sendChatMessage">
          <el-input
            v-model="chatMessage"
            placeholder="{{ $t('multiPlayerVR.enterMessage') }}"
            @keyup.enter.native="sendChatMessage"
          />
          <el-button type="primary" native-type="submit" style="margin-top: 10px;">{{ $t('multiPlayerVR.send') }}</el-button>
        </el-form>
      </div>
    </el-card>
    
    <!-- VR场景 -->
    <el-card class="vr-scene-card" v-if="isInRoom">
      <template #header>
        <div class="card-header">
          <span>{{ $t('multiPlayerVR.vrScene') }}</span>
        </div>
      </template>
      
      <div class="vr-scene-container">
        <div ref="vrScene" class="vr-scene"></div>
        <div class="vr-controls">
          <el-button type="primary" @click="startVR">{{ $t('multiPlayerVR.startVR') }}</el-button>
          <el-button @click="stopVR">{{ $t('multiPlayerVR.stopVR') }}</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { WebRTCManager, MessageType, UserInfo } from '@/utils/webrtcManager';
import { WebRTCUtils } from '@/utils/webrtcUtils';

// 响应式数据
const roomForm = ref({ id: '', name: '' });
const roomId = ref('');
const roomName = ref('');
const isInRoom = ref(false);
const users = ref<Map<string, UserInfo>>(new Map());
const chatMessages = ref<any[]>([]);
const chatMessage = ref('');
const userId = ref('');
const username = ref('');
const vrScene = ref<HTMLElement | null>(null);

// WebRTC管理器
let webrtcManager: WebRTCManager | null = null;
let webrtcUtils: WebRTCUtils | null = null;

// 计算属性
const isConnected = computed(() => webrtcUtils?.isConnected.value || false);

// 方法
const createRoom = async () => {
  if (!roomForm.value.name) {
    ElMessage.warning('请输入房间名称');
    return;
  }
  
  try {
    // 生成房间ID
    const newRoomId = `room_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    roomId.value = newRoomId;
    roomName.value = roomForm.value.name;
    
    // 加入房间
    await joinRoomWithId(newRoomId, roomForm.value.name);
  } catch (error) {
    ElMessage.error('创建房间失败');
    console.error('创建房间失败:', error);
  }
};

const joinRoom = async () => {
  if (!roomForm.value.id) {
    ElMessage.warning('请输入房间ID');
    return;
  }
  
  try {
    await joinRoomWithId(roomForm.value.id, roomForm.value.id);
  } catch (error) {
    ElMessage.error('加入房间失败');
    console.error('加入房间失败:', error);
  }
};

const joinRoomWithId = async (id: string, name: string) => {
  try {
    // 初始化WebRTC
    webrtcUtils = new WebRTCUtils();
    await webrtcUtils.initialize();
    
    // 获取本地流
    const localStream = await webrtcUtils.getLocalStream();
    
    // 生成用户ID
    const newUserId = `user_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    userId.value = newUserId;
    username.value = 'User_' + Math.floor(Math.random() * 1000);
    
    // 初始化WebRTC管理器
    webrtcManager = new WebRTCManager('wss://your-signaling-server.com');
    await webrtcManager.initialize();
    
    // 连接到信令服务器
    webrtcManager.connectToSignalingServer();
    
    // 加入房间
    await webrtcManager.joinRoom(id, {
      userId: newUserId,
      username: username.value
    });
    
    // 更新状态
    roomId.value = id;
    roomName.value = name;
    isInRoom.value = true;
    
    ElMessage.success('加入房间成功');
  } catch (error) {
    throw error;
  }
};

const leaveRoom = () => {
  if (webrtcManager) {
    webrtcManager.leaveRoom();
    webrtcManager.close();
  }
  
  if (webrtcUtils) {
    webrtcUtils.close();
  }
  
  // 重置状态
  roomId.value = '';
  roomName.value = '';
  isInRoom.value = false;
  users.value = new Map();
  chatMessages.value = [];
  chatMessage.value = '';
  
  ElMessage.success('离开房间成功');
};

const sendChatMessage = () => {
  if (!chatMessage.value.trim()) {
    return;
  }
  
  if (webrtcManager) {
    // 发送聊天消息
    webrtcManager.sendChatMessage(chatMessage.value);
    
    // 添加到本地聊天记录
    chatMessages.value.push({
      id: Date.now(),
      userId: userId.value,
      username: username.value,
      content: chatMessage.value,
      timestamp: Date.now()
    });
    
    // 清空输入框
    chatMessage.value = '';
  }
};

const startVR = () => {
  // 启动VR模式
  console.log('启动VR模式');
  // 这里需要集成WebXR API来启动VR模式
};

const stopVR = () => {
  // 停止VR模式
  console.log('停止VR模式');
  // 这里需要集成WebXR API来停止VR模式
};

const formatTime = (timestamp: number) => {
  const date = new Date(timestamp);
  return date.toLocaleTimeString();
};

// 生命周期
onMounted(() => {
  // 组件挂载时的初始化
});

onUnmounted(() => {
  // 组件卸载时的清理
  if (webrtcManager) {
    webrtcManager.close();
  }
  
  if (webrtcUtils) {
    webrtcUtils.close();
  }
});
</script>

<style scoped>
.multi-player-vr {
  padding: 20px;
}

.room-card,
.users-card,
.chat-card,
.vr-scene-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-management {
  padding: 10px 0;
}

.room-info {
  margin-top: 20px;
}

.user-info {
  margin-left: 10px;
}

.user-name {
  font-weight: bold;
}

.user-position {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.chat-container {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 10px;
}

.chat-message {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start;
}

.chat-message.own {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  margin-right: 10px;
}

.chat-message.own .message-avatar {
  margin-right: 0;
  margin-left: 10px;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.message-username {
  font-weight: bold;
  font-size: 12px;
}

.message-time {
  font-size: 10px;
  color: #909399;
}

.message-text {
  font-size: 14px;
  line-height: 1.4;
}

.vr-scene-container {
  position: relative;
}

.vr-scene {
  width: 100%;
  height: 400px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.vr-controls {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}
</style>
