<template>
  <div class="knowledge-graph">
    <el-card class="graph-card">
      <template #header>
        <div class="card-header">
          <span>知识图谱</span>
          <div class="graph-controls">
            <el-select v-model="depth" size="small" @change="loadGraph">
              <el-option label="1级" :value="1" />
              <el-option label="2级" :value="2" />
              <el-option label="3级" :value="3" />
            </el-select>
            <el-button size="small" @click="resetGraph">重置</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="error" class="error-container">
        <el-icon><Warning /></el-icon>
        <span>{{ error }}</span>
      </div>
      
      <div v-else class="graph-container">
        <div ref="graphRef" class="graph" :style="{ width: '100%', height: '600px' }"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { get } from '@/utils/request'
import { Loading, Warning } from '@element-plus/icons-vue'

const props = defineProps({
  knowledgeId: {
    type: Number,
    required: true
  }
})

const depth = ref(2)
const loading = ref(false)
const error = ref('')
const graphRef = ref<HTMLElement>()
let graphInstance: any = null

// 加载知识图谱数据
const loadGraph = async () => {
  if (!props.knowledgeId) return
  
  loading.value = true
  error.value = ''
  
  try {
    const res = await get(`/knowledge/relation/graph/${props.knowledgeId}`, {
      params: { depth: depth.value }
    })
    
    if (res.code === 200 && res.data) {
      await nextTick()
      renderGraph(res.data)
    } else {
      error.value = '获取知识图谱数据失败'
      // 模拟数据
      const mockData = {
        nodes: [
          { id: props.knowledgeId, title: '电话诈骗防范', category: '电信诈骗', depth: 0 },
          { id: 2, title: '短信诈骗防范', category: '电信诈骗', depth: 1 },
          { id: 3, title: '网络诈骗防范', category: '网络诈骗', depth: 1 },
          { id: 4, title: '银行卡诈骗案例', category: '金融诈骗', depth: 1 },
          { id: 5, title: '验证码诈骗手段', category: '电信诈骗', depth: 2 },
          { id: 6, title: '钓鱼网站识别', category: '网络诈骗', depth: 2 }
        ],
        links: [
          { source: props.knowledgeId, target: 2, type: 'related', name: '相关', weight: 3 },
          { source: props.knowledgeId, target: 3, type: 'related', name: '相关', weight: 2 },
          { source: props.knowledgeId, target: 4, type: 'related', name: '相关', weight: 1 },
          { source: 2, target: 5, type: 'related', name: '相关', weight: 3 },
          { source: 3, target: 6, type: 'related', name: '相关', weight: 3 }
        ]
      }
      await nextTick()
      renderGraph(mockData)
    }
  } catch (err) {
    console.error('加载知识图谱失败:', err)
    error.value = '加载失败，请稍后再试'
    // 模拟数据
    const mockData = {
      nodes: [
        { id: props.knowledgeId, title: '电话诈骗防范', category: '电信诈骗', depth: 0 },
        { id: 2, title: '短信诈骗防范', category: '电信诈骗', depth: 1 },
        { id: 3, title: '网络诈骗防范', category: '网络诈骗', depth: 1 }
      ],
      links: [
        { source: props.knowledgeId, target: 2, type: 'related', name: '相关', weight: 3 },
        { source: props.knowledgeId, target: 3, type: 'related', name: '相关', weight: 2 }
      ]
    }
    await nextTick()
    renderGraph(mockData)
  } finally {
    loading.value = false
  }
}

// 渲染知识图谱
const renderGraph = (data: any) => {
  if (!graphRef.value) return
  
  // 简单的图谱渲染（实际项目中可以使用D3.js等库）
  const container = graphRef.value
  container.innerHTML = ''
  
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg')
  svg.setAttribute('width', '100%')
  svg.setAttribute('height', '100%')
  container.appendChild(svg)
  
  // 计算节点位置
  const nodes = data.nodes || []
  const links = data.links || []
  
  // 简单的力导向布局模拟
  const nodePositions = calculateNodePositions(nodes, links)
  
  // 绘制连接线
  links.forEach(link => {
    const sourcePos = nodePositions.get(link.source) || { x: 0, y: 0 }
    const targetPos = nodePositions.get(link.target) || { x: 0, y: 0 }
    
    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line')
    line.setAttribute('x1', sourcePos.x.toString())
    line.setAttribute('y1', sourcePos.y.toString())
    line.setAttribute('x2', targetPos.x.toString())
    line.setAttribute('y2', targetPos.y.toString())
    line.setAttribute('stroke', '#999')
    line.setAttribute('stroke-width', (link.weight || 1).toString())
    svg.appendChild(line)
    
    // 添加连接线标签
    const midX = (sourcePos.x + targetPos.x) / 2
    const midY = (sourcePos.y + targetPos.y) / 2
    const text = document.createElementNS('http://www.w3.org/2000/svg', 'text')
    text.setAttribute('x', midX.toString())
    text.setAttribute('y', midY.toString())
    text.setAttribute('text-anchor', 'middle')
    text.setAttribute('fill', '#666')
    text.setAttribute('font-size', '12px')
    text.textContent = link.name || ''
    svg.appendChild(text)
  })
  
  // 绘制节点
  nodes.forEach(node => {
    const pos = nodePositions.get(node.id) || { x: 0, y: 0 }
    
    // 节点圆圈
    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle')
    circle.setAttribute('cx', pos.x.toString())
    circle.setAttribute('cy', pos.y.toString())
    circle.setAttribute('r', node.depth === 0 ? '20' : '15')
    circle.setAttribute('fill', getCategoryColor(node.category))
    circle.setAttribute('stroke', '#fff')
    circle.setAttribute('stroke-width', '2')
    svg.appendChild(circle)
    
    // 节点文本
    const text = document.createElementNS('http://www.w3.org/2000/svg', 'text')
    text.setAttribute('x', pos.x.toString())
    text.setAttribute('y', pos.y.toString())
    text.setAttribute('text-anchor', 'middle')
    text.setAttribute('dominant-baseline', 'middle')
    text.setAttribute('fill', '#fff')
    text.setAttribute('font-size', node.depth === 0 ? '12px' : '10px')
    text.textContent = node.title || ''
    svg.appendChild(text)
    
    // 添加点击事件
    circle.addEventListener('click', () => {
      console.log('点击节点:', node)
      // 可以跳转到知识详情页
      window.location.href = `/knowledge/${node.id}`
    })
  })
}

// 计算节点位置
const calculateNodePositions = (nodes: any[], links: any[]) => {
  const positions = new Map<number, { x: number, y: number }>()
  const width = 800
  const height = 600
  
  // 按深度分组
  const nodesByDepth = new Map<number, any[]>()
  nodes.forEach(node => {
    const depth = node.depth || 0
    if (!nodesByDepth.has(depth)) {
      nodesByDepth.set(depth, [])
    }
    nodesByDepth.get(depth)?.push(node)
  })
  
  // 计算每层的位置
  const maxDepth = Math.max(...Array.from(nodesByDepth.keys()))
  const layerWidth = width / (maxDepth + 2)
  
  nodesByDepth.forEach((layerNodes, depth) => {
    const layerY = (height / (layerNodes.length + 1))
    layerNodes.forEach((node, index) => {
      positions.set(node.id, {
        x: (depth + 1) * layerWidth,
        y: (index + 1) * layerY
      })
    })
  })
  
  return positions
}

// 获取分类颜色
const getCategoryColor = (category: string) => {
  const colorMap: Record<string, string> = {
    '电信诈骗': '#409EFF',
    '网络诈骗': '#67C23A',
    '金融诈骗': '#E6A23C',
    '其他': '#909399'
  }
  return colorMap[category] || '#909399'
}

// 重置图谱
const resetGraph = () => {
  loadGraph()
}

// 监听knowledgeId变化
watch(() => props.knowledgeId, () => {
  loadGraph()
})

onMounted(() => {
  loadGraph()
})
</script>

<style scoped>
.knowledge-graph {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.graph-controls {
  display: flex;
  gap: 8px;
}

.loading-container,
.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  gap: 12px;
  color: var(--color-text-secondary);
}

.error-container {
  color: var(--color-danger);
}

.graph-container {
  position: relative;
  width: 100%;
  height: 600px;
  overflow: auto;
}

.graph {
  position: relative;
}

@media (max-width: 768px) {
  .graph-container {
    height: 400px;
  }
}
</style>