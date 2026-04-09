<template>
  <div class="line-chart" ref="chartRef"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface Props {
  data: { label: string; value: number }[]
  title?: string
  color?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  color: '#409EFF',
  height: '280px'
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
  
  // 确保 data 是数组
  const chartData = Array.isArray(props.data) ? props.data : []
  
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: {
        color: '#4a5568'
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
      type: 'category',
      boundaryGap: false,
      data: chartData.map(item => item.label),
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
      type: 'value',
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
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: props.color
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: props.color + '40' },
            { offset: 1, color: props.color + '05' }
          ])
        },
        itemStyle: {
          color: props.color
        },
        data: chartData.map(item => item.value)
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
  
  // 确保 data 是数组
  const chartData = Array.isArray(props.data) ? props.data : []
  
  const option: echarts.EChartsOption = {
    xAxis: {
      data: chartData.map(item => item.label)
    },
    series: [
      {
        data: chartData.map(item => item.value)
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
.line-chart {
  width: 100%;
  height: v-bind(height);
}
</style>

