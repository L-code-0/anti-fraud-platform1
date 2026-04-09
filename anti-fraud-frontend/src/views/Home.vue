<template>
  <div class="home-page">
    <!-- Banner - 升级版 -->
    <Banner />
    
    <!-- 功能模块 - 升级版 -->
    <Features />
    
    <!-- 最新预警 - 升级版 -->
    <Warnings />
    
    <!-- 热门知识 - 升级版 -->
    <HotKnowledge />
    
    <!-- 学习路径 - 升级版 -->
    <LearningPath />
    
    <!-- CTA区域 -->
    <CTASection />
  </div>
</template>

<script setup lang="ts">
import Banner from '@/components/home/Banner.vue'
import Features from '@/components/home/Features.vue'
import Warnings from '@/components/home/Warnings.vue'
import HotKnowledge from '@/components/home/HotKnowledge.vue'
import LearningPath from '@/components/home/LearningPath.vue'
import CTASection from '@/components/home/CTASection.vue'
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
