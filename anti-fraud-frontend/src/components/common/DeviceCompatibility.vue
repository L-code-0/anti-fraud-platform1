<template>
  <div class="device-compatibility">
    <el-card :title="$t('deviceCompatibility.title')" class="compatibility-card">
      <div class="compatibility-content">
        <div class="loading" v-if="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>{{ $t('deviceCompatibility.loading') }}</span>
        </div>
        <div v-else>
          <!-- 设备信息 -->
          <div class="device-info">
            <h3>{{ $t('deviceCompatibility.deviceInfo') }}</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="{{ $t('deviceCompatibility.browser') }}">
                {{ deviceInfo.browser.name }} {{ deviceInfo.browser.version }}
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.os') }}">
                {{ deviceInfo.os.name }} {{ deviceInfo.os.version }}
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.deviceType') }}">
                {{ deviceInfo.device.type }}
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.screen') }}">
                {{ deviceInfo.device.screen.width }}x{{ deviceInfo.device.screen.height }} ({{ deviceInfo.device.screen.pixelRatio }}x)
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- WebXR支持情况 -->
          <div class="webxr-support">
            <h3>{{ $t('deviceCompatibility.webxrSupport') }}</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="{{ $t('deviceCompatibility.webxrSupported') }}">
                <el-tag :type="deviceInfo.webxr.supported ? 'success' : 'danger'">
                  {{ deviceInfo.webxr.supported ? $t('deviceCompatibility.yes') : $t('deviceCompatibility.no') }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.immersiveVR') }}">
                <el-tag :type="deviceInfo.webxr.sessions['immersive-vr'] ? 'success' : 'info'">
                  {{ deviceInfo.webxr.sessions['immersive-vr'] ? $t('deviceCompatibility.supported') : $t('deviceCompatibility.notSupported') }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.immersiveAR') }}">
                <el-tag :type="deviceInfo.webxr.sessions['immersive-ar'] ? 'success' : 'info'">
                  {{ deviceInfo.webxr.sessions['immersive-ar'] ? $t('deviceCompatibility.supported') : $t('deviceCompatibility.notSupported') }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="{{ $t('deviceCompatibility.inline') }}">
                <el-tag :type="deviceInfo.webxr.sessions['inline'] ? 'success' : 'info'">
                  {{ deviceInfo.webxr.sessions['inline'] ? $t('deviceCompatibility.supported') : $t('deviceCompatibility.notSupported') }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 兼容性建议 -->
          <div class="compatibility-suggestions">
            <h3>{{ $t('deviceCompatibility.suggestions') }}</h3>
            <el-alert
              v-for="(suggestion, index) in suggestions"
              :key="index"
              :title="suggestion.title"
              :type="suggestion.type"
              :closable="false"
              show-icon
            />
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button type="primary" @click="checkCompatibilityAgain">
              {{ $t('deviceCompatibility.checkAgain') }}
            </el-button>
            <el-button @click="copyDeviceInfo">
              {{ $t('deviceCompatibility.copyInfo') }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { DeviceDetector } from '@/utils/deviceDetector';
import { Loading } from '@element-plus/icons-vue';

// 设备信息
const deviceInfo = ref<Record<string, any>>({});
// 加载状态
const loading = ref(true);

// 兼容性建议
const suggestions = computed(() => {
  const suggestions: Array<{ title: string; type: 'success' | 'warning' | 'danger' | 'info' }> = [];

  // WebXR支持建议
  if (!deviceInfo.value.webxr?.supported) {
    suggestions.push({
      title: '您的设备不支持WebXR API，无法体验VR/AR功能',
      type: 'danger'
    });
  } else {
    if (deviceInfo.value.webxr?.sessions?.['immersive-vr']) {
      suggestions.push({
        title: '您的设备支持VR功能，可以体验沉浸式VR演练',
        type: 'success'
      });
    } else {
      suggestions.push({
        title: '您的设备不支持VR功能，无法体验沉浸式VR演练',
        type: 'warning'
      });
    }

    if (deviceInfo.value.webxr?.sessions?.['immersive-ar']) {
      suggestions.push({
        title: '您的设备支持AR功能，可以体验增强现实功能',
        type: 'success'
      });
    } else {
      suggestions.push({
        title: '您的设备不支持AR功能，无法体验增强现实功能',
        type: 'warning'
      });
    }

    if (deviceInfo.value.webxr?.sessions?.['inline']) {
      suggestions.push({
        title: '您的设备支持内联模式，可以体验基础的3D功能',
        type: 'success'
      });
    } else {
      suggestions.push({
        title: '您的设备不支持内联模式，无法体验3D功能',
        type: 'danger'
      });
    }
  }

  // 浏览器建议
  const browser = deviceInfo.value.browser?.name;
  if (browser && !['Chrome', 'Edge'].includes(browser)) {
    suggestions.push({
      title: '建议使用Chrome或Edge浏览器以获得最佳体验',
      type: 'warning'
    });
  }

  // 设备类型建议
  const deviceType = deviceInfo.value.device?.type;
  if (deviceType === 'Mobile') {
    suggestions.push({
      title: '您正在使用移动设备，部分功能可能受限',
      type: 'info'
    });
  }

  return suggestions;
});

// 检查兼容性
const checkCompatibility = async () => {
  loading.value = true;
  try {
    deviceInfo.value = await DeviceDetector.getDeviceInfoWithWebXRSupport();
  } catch (error) {
    console.error('Error checking compatibility:', error);
    ElMessage.error('检测设备兼容性失败');
  } finally {
    loading.value = false;
  }
};

// 重新检查兼容性
const checkCompatibilityAgain = () => {
  checkCompatibility();
};

// 复制设备信息
const copyDeviceInfo = () => {
  const infoString = JSON.stringify(deviceInfo.value, null, 2);
  navigator.clipboard.writeText(infoString)
    .then(() => {
      ElMessage.success('设备信息已复制到剪贴板');
    })
    .catch(err => {
      console.error('Failed to copy device info:', err);
      ElMessage.error('复制设备信息失败');
    });
};

// 组件挂载时检查兼容性
onMounted(() => {
  checkCompatibility();
});
</script>

<style scoped>
.device-compatibility {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.compatibility-card {
  margin-bottom: 20px;
}

.compatibility-content {
  padding: 20px;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #606266;
}

.loading span {
  margin-left: 10px;
}

.device-info,
.webxr-support,
.compatibility-suggestions {
  margin-bottom: 30px;
}

h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .compatibility-content {
    padding: 10px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons button {
    width: 100%;
  }
}
</style>
