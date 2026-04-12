# 组件命名规范

## 1. 命名原则

- **一致性**：所有组件命名遵循统一的规范
- **可读性**：命名应清晰、直观，便于理解和维护
- **语义化**：命名应反映组件的功能和用途
- **大小写规范**：使用 PascalCase（帕斯卡命名法）

## 2. 文件名规范

### 2.1 页面组件（views目录）

- **格式**：`[功能名称].vue` 或 `[功能名称]Pro.vue`（增强版）
- **示例**：
  - `Home.vue` - 首页
  - `Login.vue` - 登录页
  - `DataAnalysis.vue` - 数据分析页
  - `UserCenterPro.vue` - 用户中心增强版

### 2.2 通用组件（components目录）

- **格式**：`[功能描述].vue`
- **示例**：
  - `LoadingButton.vue` - 加载按钮
  - `GlassCard.vue` - 玻璃卡片
  - `EnhancedTable.vue` - 增强表格

### 2.3 目录组织

- **按功能分类**：将相关组件放在同一目录下
- **示例**：
  - `components/common/` - 通用组件
  - `components/home/` - 首页相关组件
  - `components/charts/` - 图表组件

## 3. 组件名称规范

- **使用 PascalCase**：组件名称首字母大写，单词之间大写
- **避免使用下划线**：使用驼峰命名法
- **避免使用数字**：除非必要，否则不使用数字
- **避免使用缩写**：尽量使用完整单词

## 4. 组件内部规范

### 4.1 组件选项

- **name属性**：与文件名保持一致
- **示例**：
  ```vue
  <script setup lang="ts">
  // 组件逻辑
  </script>
  
  <template>
    <!-- 组件模板 -->
  </template>
  
  <style scoped>
  /* 组件样式 */
  </style>
  ```

### 4.2 Props 命名

- **使用 camelCase**：props 名称使用小驼峰命名法
- **示例**：
  ```typescript
  defineProps({
    userId: {
      type: String,
      required: true
    },
    showLoading: {
      type: Boolean,
      default: false
    }
  })
  ```

### 4.3 事件命名

- **使用 kebab-case**：事件名称使用短横线分隔
- **示例**：
  ```typescript
  const emit = defineEmits([
    'user-clicked',
    'data-loaded',
    'error-occurred'
  ])
  ```

## 5. 命名示例

| 组件类型 | 推荐命名 | 不推荐命名 |
|---------|---------|-----------|
| 页面组件 | `UserProfile.vue` | `user-profile.vue` |
| 通用组件 | `LoadingSpinner.vue` | `loading_spinner.vue` |
| Props | `maxItems` | `max_items` |
| 事件 | `item-selected` | `itemSelected` |

## 6. 特殊情况处理

- **复合功能组件**：使用描述性名称，如 `SocialInteraction.vue`
- **增强版组件**：使用 `Pro` 后缀，如 `DataAnalysisPro.vue`
- **特定页面组件**：使用页面功能作为名称，如 `Dashboard.vue`

## 7. 检查清单

- [ ] 所有组件文件名使用 PascalCase
- [ ] 组件名称与文件名一致
- [ ] Props 使用 camelCase
- [ ] 事件使用 kebab-case
- [ ] 命名语义化，反映组件功能
- [ ] 目录结构清晰，按功能分类

遵循这些规范将有助于提高代码的可读性、可维护性和一致性。
