<template>
  <div class="help-page">
    <div class="page-header">
      <h1><el-icon><QuestionFilled /></el-icon> 帮助中心</h1>
      <p>在这里您可以找到常见问题的解答和使用指南</p>
    </div>

    <!-- 搜索框 -->
    <el-card class="search-card">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索问题..."
        size="large"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <!-- 快速入口 -->
    <el-row :gutter="20" class="quick-entry">
      <el-col :span="6">
        <div class="entry-card" @click="activeTab = 'guide'">
          <div class="entry-icon blue">
            <el-icon><Reading /></el-icon>
          </div>
          <h4>新手指南</h4>
          <p>快速了解平台功能</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="entry-card" @click="activeTab = 'faq'">
          <div class="entry-icon green">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <h4>常见问题</h4>
          <p>FAQ 常见问题解答</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="entry-card" @click="activeTab = 'tips'">
          <div class="entry-icon orange">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <h4>防骗技巧</h4>
          <p>识别和防范诈骗</p>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="entry-card" @click="activeTab = 'contact'">
          <div class="entry-icon purple">
            <el-icon><Service /></el-icon>
          </div>
          <h4>联系我们</h4>
          <p>获取人工帮助</p>
        </div>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-card class="content-card">
      <el-tabs v-model="activeTab" class="help-tabs">
        <!-- 新手指南 -->
        <el-tab-pane label="新手指南" name="guide">
          <div class="guide-content">
            <el-collapse v-model="activeGuide" accordion>
              <el-collapse-item title="1. 如何注册和登录？" name="guide-1">
                <div class="guide-detail">
                  <p>访问平台首页，点击右上角的"注册"按钮，填写用户名、密码、手机号等信息即可完成注册。注册完成后，使用账号密码登录即可开始使用平台功能。</p>
                  <p class="tip"><el-icon><InfoFilled /></el-icon> 提示：建议使用真实信息注册，以便享受更多个性化服务。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="2. 如何学习反诈知识？" name="guide-2">
                <div class="guide-detail">
                  <p>登录后，点击顶部导航栏的"知识学习"进入知识库页面。您可以：</p>
                  <ul>
                    <li>浏览各类反诈知识文章</li>
                    <li>根据分类筛选感兴趣的内容</li>
                    <li>收藏喜欢的文章以便日后复习</li>
                    <li>参与知识文章的评论讨论</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="3. 如何参加在线测试？" name="guide-3">
                <div class="guide-detail">
                  <p>点击"测试考试"进入考试中心，您可以：</p>
                  <ul>
                    <li>选择感兴趣的试卷进行测试</li>
                    <li>查看历史考试记录和成绩</li>
                    <li>利用错题本复习薄弱知识点</li>
                    <li>查看考试结果分析报告</li>
                  </ul>
                  <p class="tip"><el-icon><InfoFilled /></el-icon> 提示：认真阅读考试说明，部分考试有时间和次数限制。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="4. 如何进行情景演练？" name="guide-4">
                <div class="guide-detail">
                  <p>点击"演练模拟"进入演练中心，选择感兴趣的演练场景。系统会模拟真实的诈骗场景，您需要在对话中选择正确的应对方式。完成演练后可查看详细的表现分析。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="5. 如何举报诈骗信息？" name="guide-5">
                <div class="guide-detail">
                  <p>点击"举报预警"进入举报中心，点击"提交举报"填写相关信息：</p>
                  <ul>
                    <li>诈骗类型和详细描述</li>
                    <li>可疑电话号码或链接</li>
                    <li>您的联系方式（可选匿名）</li>
                  </ul>
                  <p class="tip"><el-icon><InfoFilled /></el-icon> 提示：提供详细信息有助于我们更快处理您的举报。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="6. 积分和等级有什么用？" name="guide-6">
                <div class="guide-detail">
                  <p>平台采用积分和等级系统：</p>
                  <ul>
                    <li><strong>积分获取</strong>：学习知识、完成测试、参与演练、提交举报等都可以获得积分</li>
                    <li><strong>等级提升</strong>：积累一定积分可提升等级，解锁更多功能</li>
                    <li><strong>勋章奖励</strong>：完成特定任务可获得精美勋章</li>
                  </ul>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-tab-pane>

        <!-- 常见问题 -->
        <el-tab-pane label="常见问题" name="faq">
          <div class="faq-content">
            <el-collapse v-model="activeFAQ" accordion>
              <el-collapse-item title="忘记密码怎么办？" name="faq-1">
                <div class="faq-detail">
                  <p>在登录页面点击"忘记密码"，输入您注册时使用的手机号或邮箱，系统会发送重置链接或验证码给您，按照提示操作即可重置密码。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="如何修改个人资料？" name="faq-2">
                <div class="faq-detail">
                  <p>登录后，点击右上角的头像进入"个人中心"，可以修改头像、昵称、手机号、邮箱等个人信息。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="考试可以重复参加吗？" name="faq-3">
                <div class="faq-detail">
                  <p>这取决于试卷的设置。部分试卷允许无限次练习，部分试卷有限制次数或时间间隔。请在开始考试前查看考试说明。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="错题会自动保存吗？" name="faq-4">
                <div class="faq-detail">
                  <p>是的，当您在测试中答错题目时，系统会自动将错题记录到您的错题本中。您可以在"错题本"页面查看和复习这些题目。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="举报信息是否保密？" name="faq-5">
                <div class="faq-detail">
                  <p>平台非常重视用户隐私。您可以选择匿名提交举报，您的个人信息将被严格保密。如果需要后续反馈，建议留下联系方式。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="如何联系客服？" name="faq-6">
                <div class="faq-detail">
                  <p>您可以通过以下方式联系我们：</p>
                  <ul>
                    <li>在线客服：点击右下角"在线客服"图标</li>
                    <li>客服电话：400-xxx-xxxx（工作日 9:00-18:00）</li>
                    <li>邮箱：support@example.com</li>
                  </ul>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-tab-pane>

        <!-- 防骗技巧 -->
        <el-tab-pane label="防骗技巧" name="tips">
          <div class="tips-content">
            <div class="tips-grid">
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><Bell /></el-icon>
                    <span>警惕陌生链接</span>
                  </div>
                </template>
                <p>不要轻易点击陌生人发送的链接，尤其是声称中奖、退款、退货等内容的链接。正规机构不会通过短信链接要求您输入个人信息。</p>
              </el-card>
              
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><Lock /></el-icon>
                    <span>保护账户安全</span>
                  </div>
                </template>
                <p>定期更换密码，使用强密码（包含大小写字母、数字和特殊字符）。不要在多个网站使用相同的密码，尤其是银行和支付账户。</p>
              </el-card>
              
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><User /></el-icon>
                    <span>核实身份信息</span>
                  </div>
                </template>
                <p>遇到自称公检法、银行、快递等机构的电话，一定要通过官方渠道核实。真正的机构不会通过电话要求转账或提供验证码。</p>
              </el-card>
              
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><Money /></el-icon>
                    <span>谨慎金钱交易</span>
                  </div>
                </template>
                <p>任何涉及转账、汇款的要求都要谨慎对待。特别是对方要求向陌生人转账、使用非官方支付渠道、或催促您立即操作时。</p>
              </el-card>
              
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><Warning /></el-icon>
                    <span>警惕高回报诱惑</span>
                  </div>
                </template>
                <p>"低投入高回报"、"快速赚钱"等宣传通常是诈骗的典型特征。保持理性思维，警惕任何看似过于优惠的投资机会。</p>
              </el-card>
              
              <el-card class="tip-card" shadow="hover">
                <template #header>
                  <div class="tip-header">
                    <el-icon class="tip-icon"><ChatLineSquare /></el-icon>
                    <span>多方求证信息</span>
                  </div>
                </template>
                <p>遇到可疑情况时，多与家人朋友商量，或向官方渠道求证。不要独自做出重大决定，诈骗者往往会制造紧迫感让您无法冷静思考。</p>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 联系我们 -->
        <el-tab-pane label="联系我们" name="contact">
          <div class="contact-content">
            <el-row :gutter="40">
              <el-col :span="12">
                <div class="contact-info">
                  <h3>联系方式</h3>
                  <div class="contact-item">
                    <el-icon><Phone /></el-icon>
                    <div>
                      <h4>客服热线</h4>
                      <p>400-xxx-xxxx</p>
                      <span>工作日 9:00-18:00</span>
                    </div>
                  </div>
                  <div class="contact-item">
                    <el-icon><Message /></el-icon>
                    <div>
                      <h4>电子邮箱</h4>
                      <p>support@example.com</p>
                      <span>24小时内回复</span>
                    </div>
                  </div>
                  <div class="contact-item">
                    <el-icon><Location /></el-icon>
                    <div>
                      <h4>办公地址</h4>
                      <p>某某市某某区某某路xxx号</p>
                      <span>仅限预约拜访</span>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="feedback-form">
                  <h3>意见反馈</h3>
                  <el-form :model="feedbackForm" label-position="top">
                    <el-form-item label="反馈类型">
                      <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型" style="width: 100%">
                        <el-option label="功能建议" value="suggestion" />
                        <el-option label="问题反馈" value="problem" />
                        <el-option label="内容纠错" value="correction" />
                        <el-option label="其他" value="other" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="反馈内容">
                      <el-input
                        v-model="feedbackForm.content"
                        type="textarea"
                        :rows="4"
                        placeholder="请详细描述您的问题或建议..."
                      />
                    </el-form-item>
                    <el-form-item label="联系方式（选填）">
                      <el-input v-model="feedbackForm.contact" placeholder="手机号或邮箱" />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="handleSubmitFeedback" style="width: 100%">
                        提交反馈
                      </el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 快捷链接 -->
    <el-card class="links-card">
      <template #header>
        <span>快捷链接</span>
      </template>
      <div class="quick-links">
        <el-button @click="$router.push('/knowledge')">
          <el-icon><Reading /></el-icon> 知识学习
        </el-button>
        <el-button @click="$router.push('/test')">
          <el-icon><EditPen /></el-icon> 测试考试
        </el-button>
        <el-button @click="$router.push('/simulation')">
          <el-icon><Monitor /></el-icon> 演练模拟
        </el-button>
        <el-button @click="$router.push('/report')">
          <el-icon><Warning /></el-icon> 举报预警
        </el-button>
        <el-button @click="$router.push('/activity')">
          <el-icon><Calendar /></el-icon> 活动中心
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { QuestionFilled, Search, Reading, ChatDotRound, Service, InfoFilled, Bell, Lock, User, Money, Warning, ChatLineSquare, Phone, Message, Location, EditPen, Monitor, Calendar } from '@element-plus/icons-vue'

const searchKeyword = ref('')
const activeTab = ref('guide')
const activeGuide = ref('')
const activeFAQ = ref('')

const feedbackForm = reactive({
  type: '',
  content: '',
  contact: ''
})

const handleSearch = () => {
  if (searchKeyword.value) {
    activeTab.value = 'faq'
    ElMessage.info(`搜索：${searchKeyword.value}`)
  }
}

const handleSubmitFeedback = () => {
  if (!feedbackForm.content) {
    ElMessage.warning('请填写反馈内容')
    return
  }
  ElMessage.success('反馈已提交，感谢您的建议！')
  feedbackForm.type = ''
  feedbackForm.content = ''
  feedbackForm.contact = ''
}
</script>

<style scoped>
.help-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.page-header h1 {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
}

.page-header p {
  color: var(--color-text-secondary);
}

.search-card {
  margin-bottom: var(--spacing-lg);
}

.quick-entry {
  margin-bottom: var(--spacing-lg);
}

.entry-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.entry-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-primary);
}

.entry-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-md);
  font-size: 28px;
  color: white;
}

.entry-icon.blue { background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%); }
.entry-icon.green { background: linear-gradient(135deg, #48bb78 0%, #38a169 100%); }
.entry-icon.orange { background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%); }
.entry-icon.purple { background: linear-gradient(135deg, #9f7aea 0%, #805ad5 100%); }

.entry-card h4 {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-xs);
}

.entry-card p {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.content-card {
  margin-bottom: var(--spacing-lg);
}

.guide-content,
.faq-content {
  padding: var(--spacing-md);
}

.guide-detail,
.faq-detail {
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.guide-detail ul,
.faq-detail ul {
  padding-left: var(--spacing-xl);
  margin: var(--spacing-sm) 0;
}

.guide-detail li,
.faq-detail li {
  margin-bottom: var(--spacing-xs);
}

.guide-detail .tip,
.faq-detail .tip {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background: rgba(64, 158, 255, 0.1);
  border-radius: var(--radius-md);
  color: var(--color-primary);
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.tip-card {
  height: 100%;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.tip-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.contact-content {
  padding: var(--spacing-lg);
}

.contact-info h3,
.feedback-form h3 {
  font-size: var(--font-size-xl);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border-light);
}

.contact-item {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.contact-item .el-icon {
  font-size: 24px;
  color: var(--color-primary);
  margin-top: var(--spacing-xs);
}

.contact-item h4 {
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-xs);
}

.contact-item p {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-xs);
}

.contact-item span {
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
}

.links-card .quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  justify-content: center;
}

@media (max-width: 768px) {
  .tips-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-content .el-col {
    margin-bottom: var(--spacing-lg);
  }
}
</style>
