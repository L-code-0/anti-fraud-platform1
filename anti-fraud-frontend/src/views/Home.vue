<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="header-container">
        <div class="logo-section">
          <div class="logo-icon">
            <el-icon><Lock /></el-icon>
          </div>
          <div class="logo-text">
            <span class="logo-title">反诈安全平台</span>
            <span class="logo-subtitle">守护校园安全</span>
          </div>
        </div>

        <nav class="nav-menu">
          <a href="#knowledge" class="nav-item active">知识学习</a>
          <a href="#test" class="nav-item">在线测试</a>
          <a href="#simulation" class="nav-item">情景演练</a>
          <a href="#activity" class="nav-item">活动中心</a>
        </nav>

        <div class="header-actions">
          <el-button type="primary" size="default" @click="router.push('/login')">
            登录/注册
          </el-button>
        </div>

        <button class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen">
          <el-icon><Menu /></el-icon>
        </button>
      </div>

      <!-- 移动端菜单 -->
      <div class="mobile-menu" v-show="mobileMenuOpen">
        <a href="#knowledge" class="mobile-nav-item">知识学习</a>
        <a href="#test" class="mobile-nav-item">在线测试</a>
        <a href="#simulation" class="mobile-nav-item">情景演练</a>
        <a href="#activity" class="mobile-nav-item">活动中心</a>
      </div>
    </header>

    <!-- Banner区域 -->
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-particles">
          <span v-for="i in 15" :key="i" class="particle" :style="getParticleStyle(i)"></span>
        </div>
      </div>

      <div class="hero-content">
        <div class="hero-badge">
          <el-icon><Star /></el-icon>
          <span>2026年反诈先锋平台</span>
        </div>

        <h1 class="hero-title">
          筑牢反诈防线<br />
          <span class="gradient-text">守护美好校园</span>
        </h1>

        <p class="hero-desc">
          汇聚海量反诈知识，通过智能学习、在线测试、情景演练等方式，
          全面提升高校师生的防骗意识和能力，让诈骗无处遁形
        </p>

        <div class="hero-actions">
          <el-button type="primary" size="large" class="btn-primary-gradient" @click="router.push('/knowledge')">
            <el-icon><Reading /></el-icon>
            <span>开始学习</span>
          </el-button>
          <el-button size="large" @click="router.push('/simulation')">
            <el-icon><VideoPlay /></el-icon>
            <span>情景演练</span>
          </el-button>
        </div>

        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-num">10,000+</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">500+</span>
            <span class="stat-label">知识文章</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">95%</span>
            <span class="stat-label">防护效果</span>
          </div>
        </div>
      </div>

      <div class="hero-visual">
        <div class="shield-visual">
          <div class="shield-main">
            <el-icon><Lock /></el-icon>
          </div>
          <div class="shield-ring shield-ring-1"></div>
          <div class="shield-ring shield-ring-2"></div>
          <div class="shield-ring shield-ring-3"></div>
          <div class="floating-icon fi-1"><el-icon><Lock /></el-icon></div>
          <div class="floating-icon fi-2"><el-icon><Key /></el-icon></div>
          <div class="floating-icon fi-3"><el-icon><WarnTriangleFilled /></el-icon></div>
        </div>
      </div>
    </section>

    <!-- 功能模块 -->
    <section class="features-section" id="features">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">核心功能</span>
          <h2 class="section-title">四位一体 · 全方位防护</h2>
          <p class="section-desc">学习、测试、演练、举报，构建完整反诈体系</p>
        </div>

        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in features" :key="feature.title" :style="{ animationDelay: index * 0.1 + 's' }">
            <div class="feature-icon" :style="{ background: feature.gradient }">
              <el-icon><component :is="feature.icon" /></el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
            <div class="feature-tags">
              <span class="feature-tag" v-for="tag in feature.tags" :key="tag">{{ tag }}</span>
            </div>
            <el-button type="primary" text @click="router.push(feature.path)">
              立即体验
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新预警 -->
    <section class="warnings-section" id="warnings">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">实时预警</span>
          <h2 class="section-title">最新诈骗手法预警</h2>
          <p class="section-desc">及时了解最新诈骗手法，提高警惕</p>
        </div>

        <div class="warnings-carousel">
          <div class="warning-card" v-for="warning in warnings" :key="warning.id">
            <div class="warning-header">
              <span class="warning-type" :class="'type-' + warning.type">
                {{ warning.typeName }}
              </span>
              <span class="warning-time">{{ warning.time }}</span>
            </div>
            <h4>{{ warning.title }}</h4>
            <p>{{ warning.desc }}</p>
            <div class="warning-footer">
              <span class="warning-case">
                <el-icon><Document /></el-icon>
                {{ warning.cases }}例
              </span>
              <el-button type="primary" text size="small">
                了解详情
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门知识 -->
    <section class="knowledge-section" id="knowledge">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">知识中心</span>
          <h2 class="section-title">热门反诈知识</h2>
          <p class="section-desc">深入了解各类诈骗手法，防患于未然</p>
        </div>

        <div class="knowledge-tabs">
          <button
            v-for="tab in knowledgeTabs"
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeKnowledgeTab === tab.id }"
            @click="activeKnowledgeTab = tab.id"
          >
            {{ tab.name }}
          </button>
        </div>

        <div class="knowledge-grid">
          <div class="knowledge-card" v-for="item in knowledgeList" :key="item.id">
            <div class="knowledge-image">
              <img :src="item.cover" :alt="item.title" />
              <div class="knowledge-overlay">
                <el-button type="primary" size="small">
                  <el-icon><View /></el-icon>
                  阅读全文
                </el-button>
              </div>
            </div>
            <div class="knowledge-content">
              <div class="knowledge-meta">
                <span class="knowledge-tag">{{ item.category }}</span>
                <span class="knowledge-time">{{ item.time }}</span>
              </div>
              <h4>{{ item.title }}</h4>
              <p class="knowledge-desc">{{ item.desc }}</p>
              <div class="knowledge-footer">
                <div class="knowledge-stats">
                  <span><el-icon><View /></el-icon> {{ item.views }}</span>
                  <span><el-icon><Star /></el-icon> {{ item.likes }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="section-more">
          <el-button size="large" @click="router.push('/knowledge')">
            查看更多知识
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </section>

    <!-- 学习路径 -->
    <section class="learning-path-section">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">成长路线</span>
          <h2 class="section-title">反诈学习路径</h2>
          <p class="section-desc">从入门到精通，系统学习反诈知识</p>
        </div>

        <div class="path-timeline">
          <div class="path-item" v-for="(step, index) in learningPath" :key="step.title">
            <div class="path-node">
              <div class="node-icon">
                <el-icon><component :is="step.icon" /></el-icon>
              </div>
              <div class="node-line" v-if="index < learningPath.length - 1"></div>
            </div>
            <div class="path-content">
              <span class="path-step">阶段 {{ step.step }}</span>
              <h4>{{ step.title }}</h4>
              <p>{{ step.desc }}</p>
              <div class="path-tags">
                <span v-for="tag in step.tags" :key="tag" class="path-tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区域 -->
    <section class="cta-section">
      <div class="cta-container">
        <div class="cta-bg"></div>
        <div class="cta-content">
          <h2>立即加入我们</h2>
          <p>注册账号，开始您的反诈学习之旅，与我们一起守护校园安全</p>
          <div class="cta-actions">
            <el-button type="primary" size="large" class="btn-primary-gradient" @click="router.push('/register')">
              立即注册
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" @click="router.push('/about')">
              了解更多
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部 -->
    <footer class="home-footer">
      <div class="footer-container">
        <div class="footer-links">
          <div class="footer-col">
            <h4>关于我们</h4>
            <a href="#">平台介绍</a>
            <a href="#">联系我们</a>
            <a href="#">加入团队</a>
          </div>
          <div class="footer-col">
            <h4>帮助中心</h4>
            <a href="#">常见问题</a>
            <a href="#">用户指南</a>
            <a href="#">意见反馈</a>
          </div>
          <div class="footer-col">
            <h4>法律声明</h4>
            <a href="#">用户协议</a>
            <a href="#">隐私政策</a>
            <a href="#">版权声明</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 高校反诈安全知识普及平台. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Lock, Key, Star, Reading, VideoPlay, ArrowRight,
  Menu, View, Document, WarnTriangleFilled, EditPen, Coordinate
} from '@element-plus/icons-vue'

const router = useRouter()
const mobileMenuOpen = ref(false)
const activeKnowledgeTab = ref('all')

const getParticleStyle = (index: number) => {
  const size = Math.random() * 6 + 2
  const delay = Math.random() * 5
  const duration = Math.random() * 15 + 10
  const left = Math.random() * 100

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}

const features = [
  {
    icon: 'Reading',
    title: '知识学习',
    desc: '丰富的反诈知识库，涵盖各类诈骗手法详解',
    tags: ['防骗指南', '案例分析', '最新预警'],
    path: '/knowledge',
    gradient: 'var(--gradient-primary)'
  },
  {
    icon: 'EditPen',
    title: '在线测试',
    desc: '检验学习成果，智能错题分析',
    tags: ['章节测试', '模拟考试', '能力评估'],
    path: '/test',
    gradient: 'var(--gradient-success)'
  },
  {
    icon: 'Coordinate',
    title: '情景演练',
    desc: '模拟真实诈骗场景，实战演练',
    tags: ['电话诈骗', '网络诈骗', '杀猪盘'],
    path: '/simulation',
    gradient: 'var(--gradient-warning)'
  },
  {
    icon: 'WarnTriangleFilled',
    title: '举报预警',
    desc: '一键举报诈骗信息，保护他人',
    tags: ['快速举报', '线索奖励', '预警推送'],
    path: '/report',
    gradient: 'var(--gradient-danger)'
  }
]

const warnings = [
  {
    id: 1,
    type: 'phone',
    typeName: '电话诈骗',
    title: '警惕冒充客服注销账户诈骗',
    desc: '近期有不法分子冒充电商客服，以注销账户需要转账验证为由实施诈骗',
    time: '2小时前',
    cases: 128
  },
  {
    id: 2,
    type: 'online',
    typeName: '网络诈骗',
    title: '警惕虚假投资理财平台',
    desc: '近期出现多个虚假投资理财APP，以高回报率为诱饵骗取钱财',
    time: '5小时前',
    cases: 256
  },
  {
    id: 3,
    type: 'social',
    typeName: '社交诈骗',
    title: '防范杀猪盘情感诈骗',
    desc: '网络交友需谨慎，警惕先培养感情后诱导投资的诈骗手法',
    time: '1天前',
    cases: 89
  }
]

const knowledgeTabs = [
  { id: 'all', name: '全部' },
  { id: 'phone', name: '电话诈骗' },
  { id: 'online', name: '网络诈骗' },
  { id: 'social', name: '社交诈骗' },
  { id: 'finance', name: '金融诈骗' }
]

const knowledgeList = [
  {
    id: 1,
    title: '揭秘冒充公检法诈骗套路',
    desc: '详细解析冒充公检法诈骗的常见套路和识别方法',
    category: '电话诈骗',
    time: '2026-01-15',
    views: '2.3k',
    likes: 328,
    cover: '/src/assets/images/hero.png'
  },
  {
    id: 2,
    title: '刷单返利诈骗防范指南',
    desc: '网络刷单本身就是违法行为，警惕各种返利诱惑',
    category: '网络诈骗',
    time: '2026-01-14',
    views: '1.8k',
    likes: 256,
    cover: '/src/assets/images/hero.png'
  },
  {
    id: 3,
    title: '杀猪盘诈骗完整解析',
    desc: '深度剖析杀猪盘诈骗的全过程，提高防范意识',
    category: '社交诈骗',
    time: '2026-01-13',
    views: '3.1k',
    likes: 456,
    cover: '/src/assets/images/hero.png'
  },
  {
    id: 4,
    title: '虚假贷款诈骗识别技巧',
    desc: '远离非法网贷，警惕各种贷款诈骗陷阱',
    category: '金融诈骗',
    time: '2026-01-12',
    views: '1.5k',
    likes: 198,
    cover: '/src/assets/images/hero.png'
  }
]

const learningPath = [
  {
    step: 1,
    icon: 'Reading',
    title: '基础认知',
    desc: '了解常见诈骗类型和基本特征',
    tags: ['诈骗类型', '基础概念']
  },
  {
    step: 2,
    icon: 'EditPen',
    title: '深度学习',
    desc: '学习各类诈骗的详细套路和案例',
    tags: ['案例分析', '手法揭秘']
  },
  {
    step: 3,
    icon: 'Coordinate',
    title: '实战演练',
    desc: '通过模拟演练提升应对能力',
    tags: ['情景模拟', '应对技巧']
  },
  {
    step: 4,
    icon: 'Trophy',
    title: '能力认证',
    desc: '通过考试获得防护能力证书',
    tags: ['在线测试', '能力认证']
  }
]
</script>

<style scoped>
/* ==================== 基础布局 ========== */
.home-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* ==================== 头部导航 ========== */
.home-header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--border-primary);
}

.header-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: var(--spacing-4) var(--spacing-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: var(--shadow-primary);
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.logo-subtitle {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.nav-menu {
  display: flex;
  gap: var(--spacing-6);
}

.nav-item {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-2) 0;
  position: relative;
  transition: color var(--transition-fast);
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--gradient-primary);
  transition: width var(--transition-fast);
}

.nav-item:hover,
.nav-item.active {
  color: var(--primary-color);
}

.nav-item:hover::after,
.nav-item.active::after {
  width: 100%;
}

.header-actions {
  display: flex;
  gap: var(--spacing-3);
}

.mobile-menu-btn {
  display: none;
  padding: var(--spacing-2);
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-primary);
  cursor: pointer;
}

.mobile-menu {
  padding: var(--spacing-4);
  background: var(--bg-primary);
  border-top: 1px solid var(--border-primary);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.mobile-nav-item {
  padding: var(--spacing-3);
  color: var(--text-primary);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);
}

.mobile-nav-item:hover {
  background: var(--bg-hover);
}

/* ==================== Hero区域 ========== */
.hero-section {
  position: relative;
  min-height: calc(100vh - 72px);
  display: flex;
  align-items: center;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #0f172a 50%, #1e293b 100%);
}

.hero-particles {
  position: absolute;
  inset: 0;
}

.hero-particles .particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particleFloat linear infinite;
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% {
    transform: translateY(-100px) rotate(720deg);
    opacity: 0;
  }
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 600px;
  color: white;
  animation: slideUp 0.8s ease forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  backdrop-filter: blur(10px);
  margin-bottom: var(--spacing-6);
}

.hero-title {
  font-size: var(--font-size-6xl);
  font-weight: var(--font-weight-bold);
  line-height: 1.2;
  margin-bottom: var(--spacing-6);
}

.gradient-text {
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--info-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: var(--font-size-lg);
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.7;
  margin-bottom: var(--spacing-8);
}

.hero-actions {
  display: flex;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-10);
}

.btn-primary-gradient {
  background: var(--gradient-primary) !important;
  border: none !important;
  box-shadow: var(--shadow-primary) !important;
}

.btn-primary-gradient:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow) !important;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: var(--spacing-8);
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--info-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.6);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

/* Hero视觉 */
.hero-visual {
  position: absolute;
  right: 5%;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.shield-visual {
  position: relative;
  width: 400px;
  height: 400px;
  animation: slideUp 0.8s ease forwards;
  animation-delay: 0.3s;
  opacity: 0;
}

.shield-main {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 150px;
  height: 150px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  color: white;
  box-shadow: var(--shadow-2xl), var(--shadow-glow);
  z-index: 3;
}

.shield-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  opacity: 0.3;
  animation: ringPulse 3s ease-in-out infinite;
}

.shield-ring-1 {
  width: 200px;
  height: 200px;
  animation-delay: 0s;
}

.shield-ring-2 {
  width: 280px;
  height: 280px;
  animation-delay: 0.5s;
}

.shield-ring-3 {
  width: 360px;
  height: 360px;
  animation-delay: 1s;
}

@keyframes ringPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
    opacity: 0.5;
  }
}

.floating-icon {
  position: absolute;
  width: 48px;
  height: 48px;
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  font-size: 20px;
  box-shadow: var(--shadow-lg);
  animation: floatIcon 4s ease-in-out infinite;
}

.fi-1 {
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.fi-2 {
  top: 30%;
  right: 10%;
  animation-delay: 1s;
}

.fi-3 {
  bottom: 25%;
  left: 20%;
  animation-delay: 2s;
}

@keyframes floatIcon {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

/* ==================== 通用区块 ========== */
.section-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6);
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-12);
}

.section-tag {
  display: inline-block;
  padding: var(--spacing-1) var(--spacing-3);
  background: var(--primary-bg);
  color: var(--primary-color);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-full);
  margin-bottom: var(--spacing-4);
}

.section-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-4);
}

.section-desc {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
}

/* ==================== 功能模块 ========== */
.features-section {
  padding: var(--spacing-20) 0;
  background: var(--bg-primary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-6);
}

.feature-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  text-align: center;
  transition: all var(--transition-normal);
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-color);
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin: 0 auto var(--spacing-4);
}

.feature-card h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.feature-card p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-4);
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-2);
  justify-content: center;
  margin-bottom: var(--spacing-4);
}

.feature-tag {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--bg-secondary);
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

/* ==================== 预警区块 ========== */
.warnings-section {
  padding: var(--spacing-20) 0;
  background: var(--bg-secondary);
}

.warnings-carousel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-6);
}

.warning-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  transition: all var(--transition-normal);
}

.warning-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
}

.warning-type {
  padding: var(--spacing-1) var(--spacing-2);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
  color: white;
}

.warning-type.type-phone { background: var(--danger-color); }
.warning-type.type-online { background: var(--warning-color); }
.warning-type.type-social { background: var(--success-color); }

.warning-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.warning-card h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.warning-card p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-4);
  line-height: 1.6;
}

.warning-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-case {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* ==================== 知识区块 ========== */
.knowledge-section {
  padding: var(--spacing-20) 0;
  background: var(--bg-primary);
}

.knowledge-tabs {
  display: flex;
  justify-content: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-8);
}

.tab-btn {
  padding: var(--spacing-2) var(--spacing-5);
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  background: var(--bg-hover);
}

.tab-btn.active {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: white;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-6);
}

.knowledge-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.knowledge-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.knowledge-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.knowledge-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.knowledge-card:hover .knowledge-image img {
  transform: scale(1.1);
}

.knowledge-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.knowledge-card:hover .knowledge-overlay {
  opacity: 1;
}

.knowledge-content {
  padding: var(--spacing-4);
}

.knowledge-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2);
}

.knowledge-tag {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--primary-bg);
  color: var(--primary-color);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

.knowledge-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.knowledge-card h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
  line-height: 1.4;
}

.knowledge-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: var(--spacing-3);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-stats {
  display: flex;
  gap: var(--spacing-4);
  color: var(--text-muted);
  font-size: var(--font-size-sm);
}

.knowledge-stats span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
}

.section-more {
  text-align: center;
  margin-top: var(--spacing-10);
}

/* ==================== 学习路径 ========== */
.learning-path-section {
  padding: var(--spacing-20) 0;
  background: var(--bg-secondary);
}

.path-timeline {
  max-width: 800px;
  margin: 0 auto;
}

.path-item {
  display: flex;
  gap: var(--spacing-6);
  margin-bottom: var(--spacing-8);
}

.path-item:last-child {
  margin-bottom: 0;
}

.path-node {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.node-icon {
  width: 56px;
  height: 56px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: var(--shadow-primary);
  z-index: 1;
}

.node-line {
  width: 2px;
  flex: 1;
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-light));
  margin-top: var(--spacing-2);
}

.path-content {
  flex: 1;
  padding-bottom: var(--spacing-6);
}

.path-step {
  display: inline-block;
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--primary-bg);
  color: var(--primary-color);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
  margin-bottom: var(--spacing-2);
}

.path-content h4 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.path-content p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
}

.path-tags {
  display: flex;
  gap: var(--spacing-2);
}

.path-tag {
  padding: var(--spacing-1) var(--spacing-3);
  background: var(--bg-tertiary);
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

/* ==================== CTA区块 ========== */
.cta-section {
  padding: var(--spacing-20) 0;
  background: var(--bg-primary);
}

.cta-container {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  overflow: hidden;
}

.cta-bg {
  position: absolute;
  inset: 0;
  background: var(--gradient-primary);
  border-radius: var(--radius-2xl);
  opacity: 0.95;
}

.cta-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: var(--spacing-16);
  color: white;
}

.cta-content h2 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.cta-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
  margin-bottom: var(--spacing-8);
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4);
}

.cta-actions .el-button {
  padding: var(--spacing-4) var(--spacing-8);
  font-size: var(--font-size-lg);
}

.cta-actions .el-button:first-child {
  background: white;
  color: var(--primary-color);
  border: none;
}

.cta-actions .el-button:first-child:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.cta-actions .el-button:not(:first-child) {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: white;
}

/* ==================== 底部 ========== */
.home-footer {
  background: var(--text-primary);
  color: white;
  padding: var(--spacing-12) 0 var(--spacing-6);
}

.footer-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6);
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-8);
  margin-bottom: var(--spacing-8);
}

.footer-col h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-4);
}

.footer-col a {
  display: block;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-2);
  transition: color var(--transition-fast);
}

.footer-col a:hover {
  color: white;
}

.footer-bottom {
  text-align: center;
  padding-top: var(--spacing-6);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.5);
}

/* ==================== 响应式 ========== */
@media (max-width: 1024px) {
  .features-grid,
  .knowledge-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .warnings-carousel {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-visual {
    display: none;
  }

  .hero-section {
    min-height: auto;
    padding: var(--spacing-10) var(--spacing-6);
  }

  .hero-title {
    font-size: var(--font-size-4xl);
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .header-actions {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .features-grid,
  .warnings-carousel,
  .knowledge-grid {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: var(--font-size-2xl);
  }

  .hero-title {
    font-size: var(--font-size-3xl);
  }

  .hero-actions {
    flex-direction: column;
  }

  .hero-stats {
    flex-wrap: wrap;
    justify-content: center;
    gap: var(--spacing-4);
  }

  .stat-divider {
    display: none;
  }

  .knowledge-tabs {
    flex-wrap: wrap;
  }

  .footer-links {
    grid-template-columns: 1fr;
    gap: var(--spacing-6);
  }

  .path-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-3);
  }

  .node-line {
    display: none;
  }
}
</style>
