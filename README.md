# EasyLib

一个基于 TabooLib 的 Minecraft 服务器插件开发库，提供 GUI 构建、物品生成、条件匹配等核心功能。

## 设计思路

### 核心架构

EasyLib 采用模块化设计，主要包括以下几个核心模块：

1. **GUI 构建系统** - 提供灵活的界面构建框架，支持普通界面和分页界面
2. **物品生成器** - 统一接口支持多种物品插件（SXItem、NeigeItems、MMOItems、EasySaver）
3. **动作系统** - 基于泛型的动作接口，支持不同类型的数据处理
4. **条件匹配器** - 策略模式实现的条件匹配系统，可扩展性强

### 工作流程

```kotlin
// 条件匹配流程
玩家输入条件列表 -> MatcherManager.checkMatcher() -> 遍历匹配器 -> 返回动作列表 -> 执行动作

// GUI 构建流程
创建配置文件 -> 选择构建器类型 -> 实现抽象方法 -> 调用buildAndOpen()
```


## 项目优点

### 1. 高度模块化
- 各功能模块独立，依赖关系清晰
- 易于维护和扩展

### 2. 策略模式应用
- [MatcherStrategy](file://B:\IdeaProjects\EasyLib\src\main\kotlin\EasyLib\function\MatcherStrategy.kt#L5-L7) 接口便于添加新的条件匹配器
- [Actions](file://B:\IdeaProjects\EasyLib\src\main\kotlin\EasyLib\function\Actions.kt#L4-L10) 接口支持不同类型的动作实现

### 3. 多插件兼容
- 物品生成器自动检测可用的物品插件
- 无需手动配置，自动适配环境

### 4. 完善的异常处理
- 自定义异常类提供详细错误信息
- 全面的 try-catch 保护避免崩溃

### 5. 调试支持
- 完整的调试日志系统
- 性能监控和构建步骤跟踪

## 快速开始

### 扩展条件匹配器

创建新的条件匹配器需要实现 [MatcherStrategy](file://B:\IdeaProjects\EasyLib\src\main\kotlin\EasyLib\function\MatcherStrategy.kt#L5-L7) 接口：

```kotlin
class CustomMatcher : MatcherStrategy {
    override fun matches(text: String, thisPlayer: Player): Actions<*>? {
        // 解析文本条件
        // 验证玩家是否满足条件
        // 返回相应的动作对象或null
        return if (conditionMet) {
            CustomAction().apply { this.data = actionData }
        } else {
            null
        }
    }
}

// 注册匹配器
FunctionManager.getMatcherManager().addMatcher("@custom", CustomMatcher())
```


### 扩展动作行为

创建新的动作需要实现 `Actions<T>` 接口：

```kotlin
class CustomAction : Actions<String> {
    override var data: String = ""
    
    override fun doAction(thisPlayer: Player) {
        // 使用预设的data执行动作
    }
    
    override fun doAction(thisPlayer: Player, text: String) {
        // 解析text参数执行动作
    }
}

// 注册动作
FunctionManager.getActionManager().addAction("@custom", CustomAction())
```


### 快速创建GUI

1. 创建配置文件 `gui.yml`：
```yaml
Title: "&a示例界面"
GuiPlain:
  - "#########"
  - "#       #"
  - "#########"
GuiKey:
  '#':
    Material: BLACK_STAINED_GLASS_PANE
    Name: " "
    IconFunction: "baned"
```


2. 创建构建器类：
```kotlin
class CustomGuiBuilder(override val config: GuiConfig, thisPlayer: Player) 
    : INormalGuiBuilder(config, thisPlayer) {
    
    override fun open() {
        buildAndOpen { }
    }
    
    override fun mapIconsToFunctions() {
        mapIconsToFunctionWay { key, function ->
            when(function) {
                "baned" -> setDefaultIcon(key)
                else -> setDefaultIcon(key)
            }
        }
    }
}
```


3. 使用GUI：
```kotlin
val config = GuiConfig(configFile)
val builder = CustomGuiBuilder(config, player)
builder.open()
```


## 依赖说明

项目中存在一些特定的依赖需要区分清楚：

### 条件判断依赖
- [RemoveItemAction](file://B:\IdeaProjects\EasyLib\src\main\kotlin\EasyLib\function\actions\RemoveItemAction.kt#L9-L59) 直接依赖 `easySaver.arim.Arim`
    - 用于物品匹配判断
    - 属于条件验证模块

### 物品生成依赖
- [GiveItemAction](file://B:\IdeaProjects\EasyLib\src\main\kotlin\EasyLib\function\actions\GiveItemAction.kt#L5-L56) 依赖 `EasyCrate.Factory.ItemGenerator.ItemGenerator`
    - 用于生成和给予物品
    - 属于物品生成模块

这两个依赖分别服务于不同的功能，需要分清楚其用途：
- 条件判断：验证玩家是否拥有满足条件的物品
- 物品生成：从物品库中生成并给予玩家物品

## 包结构说明

```
EasyLib/
├── Configs/           # 配置管理
├── EasyGui/           # GUI构建系统
├── ItemGenerator/     # 物品生成器
├── Managers/          # 管理器模块
├── Utils/             # 工具类
├── function/          # 动作和匹配器系统
└── EasyLib.kt         # 主插件文件
```
