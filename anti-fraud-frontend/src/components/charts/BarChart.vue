<template>
  <div class="bar-chart" ref="chartRef"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface DataItem {
  name: string
  value: number
}

interface Props {
  data: DataItem[]
  title?: string
  color?: string
  height?: string
  horizontal?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  color: '#409EFF',
  height: '280px',
  horizontal: false
})

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  
  // 检查是否已存在实例，避免重复初始化
  const existingInstance = chartInstance || echarts.getInstanceByDom(chartRef.value)
  if (existingInstance) {
    chartInstance = existingInstance
  } else {
    chartInstance = echarts.init(chartRef.value)
  }
  
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: {
        color: '#4a5568'
      },
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: props.horizontal ? 'value' : 'category',
      data: props.horizontal ? undefined : props.data.map(item => item.name),
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisLabel: {
        color: '#718096'
      }
    },
    yAxis: {
      type: props.horizontal ? 'category' : 'value',
      data: props.horizontal ? props.data.map(item => item.name) : undefined,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      },
      axisLabel: {
        color: '#718096'
      }
    },
    series: [
      {
        name: props.title,
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(
            props.horizontal ? 0 : 0,
            props.horizontal ? 0 : 1,
            props.horizontal ? 1 : 0,
            props.horizontal ? 0 : 0,
            [
              { offset: 0, color: props.color },
              { offset: 1, color: props.color + '80' }
            ]
          ),
          borderRadius: props.horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0]
        },
        data: props.data.map(item => item.value)
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

watch(() => props.data, () => {
  if (chartInstance) {
    updateChart()
  } else {
    initChart()
  }
}, { deep: true })

const updateChart = () => {
  if (!chartInstance || !chartRef.value) return
  
  const option: echarts.EChartsOption = {
    xAxis: {
      type: props.horizontal ? 'value' : 'category',
      data: props.horizontal ? undefined : props.data.map(item => item.name)
    },
    yAxis: {
      type: props.horizontal ? 'category' : 'value',
      data: props.horizontal ? props.data.map(item => item.name) : undefined
    },
    series: [
      {
        data: props.data.map(item => item.value)
      }
    ]
  }
  
  chartInstance.setOption(option)
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.bar-chart {
  width: 100%;
  height: v-bind(height);
}
</style>
