<template>
  <el-input
    v-model="innerValue"
    :type="type"
    :placeholder="placeholder"
    :disabled="loading || disabled"
    :clearable="clearable"
    :show-password="showPassword"
    :prefix-icon="loading ? undefined : prefixIcon"
    :suffix-icon="loading ? undefined : suffixIcon"
    class="loading-input"
    :class="{ 'is-loading': loading }"
    v-bind="$attrs"
    @input="handleInput"
    @change="handleChange"
  >
    <template v-if="loading" #prefix>
      <svg class="input-loading-icon" viewBox="0 0 24 24">
        <circle 
          cx="12" 
          cy="12" 
          r="10" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round"
        >
          <animate 
            attributeName="stroke-dasharray" 
            values="0 63;63 63;0 63" 
            dur="1.5s" 
            repeatCount="indefinite"
          />
        </circle>
      </svg>
    </template>
    <slot></slot>
  </el-input>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  modelValue?: string | number
  type?: string
  placeholder?: string
  disabled?: boolean
  loading?: boolean
  clearable?: boolean
  showPassword?: boolean
  prefixIcon?: any
  suffixIcon?: any
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  type: 'text',
  placeholder: '请输入',
  disabled: false,
  loading: false,
  clearable: true,
  showPassword: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  input: [value: string | number]
  change: [value: string | number]
}>()

const innerValue = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  innerValue.value = newVal
})

const handleInput = (value: string | number) => {
  emit('update:modelValue', value)
  emit('input', value)
}

const handleChange = (value: string | number) => {
  emit('change', value)
}
</script>

<style scoped>
.loading-input {
  transition: all 0.3s ease;
}

.loading-input.is-loading {
  opacity: 0.8;
}

.input-loading-icon {
  width: 16px;
  height: 16px;
  color: var(--color-text-muted, #718096);
  animation: input-spin 1.5s linear infinite;
}

@keyframes input-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
