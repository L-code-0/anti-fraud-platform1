/**
 * 国际化配置
 */

// 语言类型
export type Locale = 'zh-CN' | 'en-US'

// 翻译资源类型
export interface LocaleMessages {
  [key: string]: string | LocaleMessages
}

// 翻译资源
const messages: Record<Locale, LocaleMessages> = {
  'zh-CN': {
    common: {
      ok: '确定',
      cancel: '取消',
      save: '保存',
      delete: '删除',
      edit: '编辑',
      add: '添加',
      search: '搜索',
      reset: '重置',
      refresh: '刷新',
      export: '导出',
      import: '导入',
      view: '查看',
      submit: '提交',
      back: '返回',
      confirm: '确认',
      cancel: '取消',
      success: '成功',
      error: '失败',
      warning: '警告',
      info: '信息'
    },
    menu: {
      dashboard: '仪表盘',
      knowledge: '反诈知识',
      question: '题库管理',
      exam: '模拟考试',
      drill: '演练管理',
      report: '举报管理',
      user: '用户管理',
      admin: '后台管理',
      data: '数据中心',
      log: '操作日志',
      notification: '通知管理',
      setting: '系统设置'
    },
    login: {
      title: '登录系统',
      username: '用户名',
      password: '密码',
      remember: '记住我',
      forget: '忘记密码',
      login: '登录',
      register: '注册',
      welcome: '欢迎回来',
      error: '用户名或密码错误'
    },
    dashboard: {
      title: '系统概览',
      userCount: '用户数量',
      knowledgeCount: '知识数量',
      questionCount: '题目数量',
      reportCount: '举报数量',
      recentReports: '最近举报',
      recentKnowledge: '最新知识',
      userGrowth: '用户增长',
      reportTrend: '举报趋势'
    },
    knowledge: {
      title: '反诈知识管理',
      list: '知识列表',
      add: '添加知识',
      edit: '编辑知识',
      delete: '删除知识',
      titleField: '标题',
      content: '内容',
      category: '分类',
      tags: '标签',
      viewCount: '浏览量',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmDelete: '确定删除这条知识吗？',
      createSuccess: '知识创建成功',
      updateSuccess: '知识更新成功',
      deleteSuccess: '知识删除成功'
    },
    question: {
      title: '题库管理',
      list: '题目列表',
      add: '添加题目',
      edit: '编辑题目',
      delete: '删除题目',
      type: '题型',
      content: '题目内容',
      options: '选项',
      answer: '答案',
      analysis: '解析',
      difficulty: '难度',
      category: '分类',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmDelete: '确定删除这道题目吗？',
      createSuccess: '题目创建成功',
      updateSuccess: '题目更新成功',
      deleteSuccess: '题目删除成功'
    },
    exam: {
      title: '模拟考试',
      start: '开始考试',
      history: '考试历史',
      result: '考试结果',
      score: '得分',
      correctRate: '正确率',
      time: '用时',
      questions: '题目',
      correct: '正确',
      incorrect: '错误',
      review: '查看详情',
      retake: '重新考试',
      back: '返回列表'
    },
    drill: {
      title: '演练管理',
      list: '演练列表',
      add: '添加演练',
      edit: '编辑演练',
      delete: '删除演练',
      name: '演练名称',
      description: '演练描述',
      scenario: '场景',
      steps: '步骤',
      difficulty: '难度',
      duration: '时长',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmDelete: '确定删除这个演练吗？',
      createSuccess: '演练创建成功',
      updateSuccess: '演练更新成功',
      deleteSuccess: '演练删除成功'
    },
    report: {
      title: '举报管理',
      list: '举报列表',
      detail: '举报详情',
      status: '状态',
      type: '类型',
      content: '举报内容',
      contact: '联系方式',
      submitTime: '提交时间',
      processTime: '处理时间',
      processor: '处理人',
      result: '处理结果',
      confirmProcess: '确定处理这条举报吗？',
      processSuccess: '举报处理成功',
      statusOptions: {
        pending: '待处理',
        processing: '处理中',
        completed: '已完成',
        rejected: '已驳回'
      }
    },
    user: {
      title: '用户管理',
      list: '用户列表',
      add: '添加用户',
      edit: '编辑用户',
      delete: '删除用户',
      username: '用户名',
      nickname: '昵称',
      email: '邮箱',
      phone: '电话',
      role: '角色',
      status: '状态',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmDelete: '确定删除这个用户吗？',
      createSuccess: '用户创建成功',
      updateSuccess: '用户更新成功',
      deleteSuccess: '用户删除成功',
      roleOptions: {
        admin: '管理员',
        user: '普通用户',
        teacher: '教师'
      },
      statusOptions: {
        active: '激活',
        inactive: '未激活',
        banned: '禁用'
      }
    },
    data: {
      title: '数据中心',
      export: '数据导出',
      import: '数据导入',
      type: '数据类型',
      selectType: '请选择数据类型',
      downloadTemplate: '下载模板',
      uploadFile: '上传文件',
      importSuccess: '数据导入成功',
      importFailed: '数据导入失败',
      exportSuccess: '数据导出成功',
      exportFailed: '数据导出失败'
    },
    log: {
      title: '操作日志',
      list: '日志列表',
      detail: '日志详情',
      username: '操作用户',
      actionType: '操作类型',
      module: '操作模块',
      description: '操作描述',
      ipAddress: 'IP地址',
      userAgent: '设备信息',
      createTime: '操作时间',
      params: '请求参数',
      result: '操作结果',
      success: '是否成功',
      export: '导出日志',
      clean: '清理旧日志',
      actionTypeOptions: {
        create: '创建',
        update: '更新',
        delete: '删除',
        login: '登录',
        logout: '登出',
        export: '导出',
        execute: '执行',
        error: '异常'
      }
    },
    notification: {
      title: '通知管理',
      list: '通知列表',
      type: '通知类型',
      content: '通知内容',
      status: '状态',
      createTime: '创建时间',
      markRead: '标记为已读',
      markAllRead: '全部标记为已读',
      delete: '删除通知',
      deleteAll: '清空通知',
      typeOptions: {
        system: '系统通知',
        study: '学习提醒',
        interact: '互动消息',
        activity: '活动通知'
      },
      statusOptions: {
        unread: '未读',
        read: '已读'
      }
    },
    community: {
      title: '社区互助',
      list: '帖子列表',
      add: '发布帖子',
      edit: '编辑帖子',
      delete: '删除帖子',
      titleField: '标题',
      content: '内容',
      type: '类型',
      viewCount: '浏览量',
      likeCount: '点赞数',
      commentCount: '评论数',
      shareCount: '分享数',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmDelete: '确定删除这条帖子吗？',
      createSuccess: '帖子创建成功',
      updateSuccess: '帖子更新成功',
      deleteSuccess: '帖子删除成功',
      typeOptions: {
        knowledge: '反诈知识',
        suspicious: '可疑信息',
        help: '求助问答',
        experience: '经验分享',
        other: '其他'
      }
    },
    collaboration: {
      title: '多人协作演练',
      list: '演练会话列表',
      create: '创建会话',
      join: '加入会话',
      leave: '离开会话',
      end: '结束会话',
      name: '会话名称',
      description: '会话描述',
      scenario: '演练场景',
      status: '状态',
      participants: '参与者',
      startTime: '开始时间',
      endTime: '结束时间',
      createTime: '创建时间',
      updateTime: '更新时间',
      confirmEnd: '确定结束这个会话吗？',
      createSuccess: '会话创建成功',
      joinSuccess: '加入会话成功',
      leaveSuccess: '离开会话成功',
      endSuccess: '会话结束成功',
      statusOptions: {
        preparing: '准备中',
        ongoing: '进行中',
        ended: '已结束',
        cancelled: '已取消'
      }
    },
    risk: {
      title: '智能风险画像',
      list: '风险画像列表',
      detail: '风险画像详情',
      userId: '用户ID',
      username: '用户名',
      riskScore: '风险评分',
      riskLevel: '风险等级',
      riskTags: '风险标签',
      behaviorFeatures: '行为特征',
      riskAnalysis: '风险分析',
      suggestions: '建议措施',
      lastUpdateTime: '最后更新时间',
      status: '状态',
      createTime: '创建时间',
      updateTime: '更新时间',
      generate: '生成风险画像',
      generateSuccess: '风险画像生成成功',
      levelOptions: {
        low: '低风险',
        medium: '中风险',
        high: '高风险'
      },
      statusOptions: {
        normal: '正常',
        abnormal: '异常'
      }
    },
    setting: {
      title: '系统设置',
      basic: '基础设置',
      security: '安全设置',
      notification: '通知设置',
      language: '语言设置',
      saveSuccess: '设置保存成功',
      languageOptions: {
        'zh-CN': '简体中文',
        'en-US': 'English'
      }
    }
  },
  'en-US': {
    common: {
      ok: 'OK',
      cancel: 'Cancel',
      save: 'Save',
      delete: 'Delete',
      edit: 'Edit',
      add: 'Add',
      search: 'Search',
      reset: 'Reset',
      refresh: 'Refresh',
      export: 'Export',
      import: 'Import',
      view: 'View',
      submit: 'Submit',
      back: 'Back',
      confirm: 'Confirm',
      cancel: 'Cancel',
      success: 'Success',
      error: 'Error',
      warning: 'Warning',
      info: 'Info'
    },
    menu: {
      dashboard: 'Dashboard',
      knowledge: 'Knowledge',
      question: 'Question Bank',
      exam: 'Mock Exam',
      drill: 'Drill',
      report: 'Report',
      user: 'User',
      admin: 'Admin',
      data: 'Data',
      log: 'Operation Log',
      notification: 'Notification',
      setting: 'Setting'
    },
    login: {
      title: 'Login',
      username: 'Username',
      password: 'Password',
      remember: 'Remember me',
      forget: 'Forgot password',
      login: 'Login',
      register: 'Register',
      welcome: 'Welcome back',
      error: 'Invalid username or password'
    },
    dashboard: {
      title: 'System Overview',
      userCount: 'User Count',
      knowledgeCount: 'Knowledge Count',
      questionCount: 'Question Count',
      reportCount: 'Report Count',
      recentReports: 'Recent Reports',
      recentKnowledge: 'Recent Knowledge',
      userGrowth: 'User Growth',
      reportTrend: 'Report Trend'
    },
    knowledge: {
      title: 'Knowledge Management',
      list: 'Knowledge List',
      add: 'Add Knowledge',
      edit: 'Edit Knowledge',
      delete: 'Delete Knowledge',
      titleField: 'Title',
      content: 'Content',
      category: 'Category',
      tags: 'Tags',
      viewCount: 'View Count',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmDelete: 'Are you sure to delete this knowledge?',
      createSuccess: 'Knowledge created successfully',
      updateSuccess: 'Knowledge updated successfully',
      deleteSuccess: 'Knowledge deleted successfully'
    },
    question: {
      title: 'Question Bank Management',
      list: 'Question List',
      add: 'Add Question',
      edit: 'Edit Question',
      delete: 'Delete Question',
      type: 'Type',
      content: 'Content',
      options: 'Options',
      answer: 'Answer',
      analysis: 'Analysis',
      difficulty: 'Difficulty',
      category: 'Category',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmDelete: 'Are you sure to delete this question?',
      createSuccess: 'Question created successfully',
      updateSuccess: 'Question updated successfully',
      deleteSuccess: 'Question deleted successfully'
    },
    exam: {
      title: 'Mock Exam',
      start: 'Start Exam',
      history: 'Exam History',
      result: 'Exam Result',
      score: 'Score',
      correctRate: 'Correct Rate',
      time: 'Time',
      questions: 'Questions',
      correct: 'Correct',
      incorrect: 'Incorrect',
      review: 'View Details',
      retake: 'Retake Exam',
      back: 'Back to List'
    },
    drill: {
      title: 'Drill Management',
      list: 'Drill List',
      add: 'Add Drill',
      edit: 'Edit Drill',
      delete: 'Delete Drill',
      name: 'Drill Name',
      description: 'Description',
      scenario: 'Scenario',
      steps: 'Steps',
      difficulty: 'Difficulty',
      duration: 'Duration',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmDelete: 'Are you sure to delete this drill?',
      createSuccess: 'Drill created successfully',
      updateSuccess: 'Drill updated successfully',
      deleteSuccess: 'Drill deleted successfully'
    },
    report: {
      title: 'Report Management',
      list: 'Report List',
      detail: 'Report Details',
      status: 'Status',
      type: 'Type',
      content: 'Content',
      contact: 'Contact',
      submitTime: 'Submit Time',
      processTime: 'Process Time',
      processor: 'Processor',
      result: 'Result',
      confirmProcess: 'Are you sure to process this report?',
      processSuccess: 'Report processed successfully',
      statusOptions: {
        pending: 'Pending',
        processing: 'Processing',
        completed: 'Completed',
        rejected: 'Rejected'
      }
    },
    user: {
      title: 'User Management',
      list: 'User List',
      add: 'Add User',
      edit: 'Edit User',
      delete: 'Delete User',
      username: 'Username',
      nickname: 'Nickname',
      email: 'Email',
      phone: 'Phone',
      role: 'Role',
      status: 'Status',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmDelete: 'Are you sure to delete this user?',
      createSuccess: 'User created successfully',
      updateSuccess: 'User updated successfully',
      deleteSuccess: 'User deleted successfully',
      roleOptions: {
        admin: 'Admin',
        user: 'User',
        teacher: 'Teacher'
      },
      statusOptions: {
        active: 'Active',
        inactive: 'Inactive',
        banned: 'Banned'
      }
    },
    data: {
      title: 'Data Center',
      export: 'Data Export',
      import: 'Data Import',
      type: 'Data Type',
      selectType: 'Please select data type',
      downloadTemplate: 'Download Template',
      uploadFile: 'Upload File',
      importSuccess: 'Data imported successfully',
      importFailed: 'Data import failed',
      exportSuccess: 'Data exported successfully',
      exportFailed: 'Data export failed'
    },
    log: {
      title: 'Operation Log',
      list: 'Log List',
      detail: 'Log Details',
      username: 'Username',
      actionType: 'Action Type',
      module: 'Module',
      description: 'Description',
      ipAddress: 'IP Address',
      userAgent: 'User Agent',
      createTime: 'Create Time',
      params: 'Params',
      result: 'Result',
      success: 'Success',
      export: 'Export Logs',
      clean: 'Clean Old Logs',
      actionTypeOptions: {
        create: 'Create',
        update: 'Update',
        delete: 'Delete',
        login: 'Login',
        logout: 'Logout',
        export: 'Export',
        execute: 'Execute',
        error: 'Error'
      }
    },
    notification: {
      title: 'Notification Management',
      list: 'Notification List',
      type: 'Type',
      content: 'Content',
      status: 'Status',
      createTime: 'Create Time',
      markRead: 'Mark as Read',
      markAllRead: 'Mark All as Read',
      delete: 'Delete Notification',
      deleteAll: 'Clear All Notifications',
      typeOptions: {
        system: 'System',
        study: 'Study',
        interact: 'Interaction',
        activity: 'Activity'
      },
      statusOptions: {
        unread: 'Unread',
        read: 'Read'
      }
    },
    community: {
      title: 'Community',
      list: 'Post List',
      add: 'Add Post',
      edit: 'Edit Post',
      delete: 'Delete Post',
      titleField: 'Title',
      content: 'Content',
      type: 'Type',
      viewCount: 'View Count',
      likeCount: 'Like Count',
      commentCount: 'Comment Count',
      shareCount: 'Share Count',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmDelete: 'Are you sure to delete this post?',
      createSuccess: 'Post created successfully',
      updateSuccess: 'Post updated successfully',
      deleteSuccess: 'Post deleted successfully',
      typeOptions: {
        knowledge: 'Anti-fraud Knowledge',
        suspicious: 'Suspicious Information',
        help: 'Help & Q&A',
        experience: 'Experience Sharing',
        other: 'Other'
      }
    },
    collaboration: {
      title: 'Multi-person Collaboration',
      list: 'Session List',
      create: 'Create Session',
      join: 'Join Session',
      leave: 'Leave Session',
      end: 'End Session',
      name: 'Session Name',
      description: 'Description',
      scenario: 'Scenario',
      status: 'Status',
      participants: 'Participants',
      startTime: 'Start Time',
      endTime: 'End Time',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      confirmEnd: 'Are you sure to end this session?',
      createSuccess: 'Session created successfully',
      joinSuccess: 'Joined session successfully',
      leaveSuccess: 'Left session successfully',
      endSuccess: 'Session ended successfully',
      statusOptions: {
        preparing: 'Preparing',
        ongoing: 'Ongoing',
        ended: 'Ended',
        cancelled: 'Cancelled'
      }
    },
    risk: {
      title: 'Intelligent Risk Profile',
      list: 'Risk Profile List',
      detail: 'Risk Profile Details',
      userId: 'User ID',
      username: 'Username',
      riskScore: 'Risk Score',
      riskLevel: 'Risk Level',
      riskTags: 'Risk Tags',
      behaviorFeatures: 'Behavior Features',
      riskAnalysis: 'Risk Analysis',
      suggestions: 'Suggestions',
      lastUpdateTime: 'Last Update Time',
      status: 'Status',
      createTime: 'Create Time',
      updateTime: 'Update Time',
      generate: 'Generate Risk Profile',
      generateSuccess: 'Risk profile generated successfully',
      levelOptions: {
        low: 'Low Risk',
        medium: 'Medium Risk',
        high: 'High Risk'
      },
      statusOptions: {
        normal: 'Normal',
        abnormal: 'Abnormal'
      }
    },
    setting: {
      title: 'System Setting',
      basic: 'Basic Setting',
      security: 'Security Setting',
      notification: 'Notification Setting',
      language: 'Language Setting',
      saveSuccess: 'Setting saved successfully',
      languageOptions: {
        'zh-CN': 'Chinese',
        'en-US': 'English'
      }
    }
  }
}

// 当前语言
let currentLocale: Locale = 'zh-CN'

// 初始化语言
function initLocale() {
  const savedLocale = localStorage.getItem('locale') as Locale
  if (savedLocale && Object.keys(messages).includes(savedLocale)) {
    currentLocale = savedLocale
  } else {
    // 检测浏览器语言
    const browserLocale = navigator.language as Locale
    if (Object.keys(messages).includes(browserLocale)) {
      currentLocale = browserLocale
    }
  }
}

// 切换语言
function setLocale(locale: Locale) {
  if (Object.keys(messages).includes(locale)) {
    currentLocale = locale
    localStorage.setItem('locale', locale)
    return true
  }
  return false
}

// 获取当前语言
function getLocale(): Locale {
  return currentLocale
}

// 翻译函数
function t(key: string, fallback?: string): string {
  const keys = key.split('.')
  let result: any = messages[currentLocale]
  
  for (const k of keys) {
    if (result && typeof result === 'object' && k in result) {
      result = result[k]
    } else {
      return fallback || key
    }
  }
  
  return typeof result === 'string' ? result : fallback || key
}

// 初始化
initLocale()

export {
  messages,
  setLocale,
  getLocale,
  t
}
