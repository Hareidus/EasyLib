# 📚 EasyLib - Minecraft 插件开发库

> **EasyLib** 是一个基于 [TabooLib](https://github.com/TabooLib) 的轻量级、模块化 Minecraft 插件开发辅助库，专为简化 GUI 构建、物品生成、条件匹配与动作执行而设计。支持多物品插件兼容（SXItem / NeigeItems / MMOItems / EasySaver），适用于 Bukkit/Spigot/Paper 服务器环境。

---

## ✨ 特性概览

| 特性 | 说明 |
|------|------|
| 🧩 **模块化架构** | 各功能解耦，易于维护与扩展 |
| 🎯 **策略模式匹配器** | 可自定义条件判断逻辑，灵活扩展 |
| 🔌 **多物品插件兼容** | 自动识别并适配主流物品系统 |
| 💬 **占位符支持** | 全面集成 PlaceholderAPI 进行动态判断 |
| 🛠️ **调试友好** | 内置日志、性能监控与构建追踪 |
| 📦 **GUI 快速构建** | YAML 配置驱动，轻松创建分页界面 |

---

## 🏗️ 核心架构

### 🗂️ 包结构

```
EasyLib/
├── Configs/           # 配置管理
├── EasyGui/           # GUI 构建系统
├── ItemGenerator/     # 多插件物品生成器
├── Managers/          # 功能管理器（Matcher / Action）
├── Utils/             # 工具类与扩展函数
├── function/          # 动作与匹配器核心逻辑
└── EasyLib.kt         # 主插件入口
```

### ⚙️ 工作流程

#### 🔍 条件匹配流程
```kotlin
玩家触发条件 -> MatcherManager.checkMatcher() 
→ 遍历注册的 MatcherStrategy 
→ 匹配成功返回 Actions<T> 
→ 执行对应动作
```

#### 🖼️ GUI 构建流程
```kotlin
编写 gui.yml 配置 → 加载 GuiConfig 
→ 创建 GUI 构建器（继承 INormalGuiBuilder / IPagingGuiBuilder）
→ 实现 open() 与 mapIconsToFunctions()
→ 调用 buildAndOpen() 显示界面
```

---

## ✅ 内置功能组件

### 🔎 条件匹配器（Matcher）

| 匹配器 | 格式 | 示例 | 说明 |
|-------|------|------|------|
| `PermissionMatcher` | `@permission\|<节点>` | `@permission|admin.command` | 检查玩家权限 |
| `VaultMatcher` | `@vault\|<金额>` | `@vault|100.0` | 检查金币余额 |
| `PlaceholderMatcher` | `@papi\|<左值> <操作符> <右值>` | `@papi|%player_level% >= 10` | 占位符数值比较 |
| `ItemMatcher` | `@item\|<表达式>-<数量>` | `@item|name:all(startswith(&c机械),c(靴))-1` | 检查玩家是否持有指定物品 |

> ✅ 所有匹配器均支持表达式计算（如 `{amount} * 2 + 1`），用于动态数量判断。

---

### 🧰 动作执行器（Action）

| 动作 | 格式 | 示例 | 说明 |
|------|------|------|------|
| `MessageAction` | `@message\|<消息>` | `@message|欢迎回来！` | 向玩家发送消息 |
| `CommandAction` | `@command\|<命令>` | `@command|give {player} diamond 1` | 执行控制台命令 |
| `GiveItemAction` | `@giveitem\|<ID>-<数量>` | `@giveitem|sword_legendary-1` | 给予玩家物品 |
| `RemoveItemAction` | `@takeitem\|<表达式>-<数量>` | `@takeitem|name:(&c机械)-1` | 移除玩家物品 |
| `VaultAction` | `@vault\|<金额>` | `@vault|-50.0` | 增减玩家金币（负数为扣除） |
| `VoidAction` | `@void` | `@void` | 空动作，仅用于条件满足标记 |

---

## 🧪 高级用法与案例详解

### 🧩 `ItemMatcher` 使用案例

> 用于判断玩家是否持有满足条件的物品，并可自动扣除。

#### ✅ 基础语法
```text
@item|<物品表达式>-<数量>
```

#### 🧪 示例 1：检查并移除一把名称以“机械”开头的靴子
```yaml
Condition: "@item|name:all(startswith(&c机械),c(靴))-1"
```
- **说明**：玩家必须持有至少 1 把名称以 `§c机械` 开头且类型为“靴”的物品。
- **结果**：若匹配成功，将自动触发 `RemoveItemAction` 移除该物品。

#### 🧪 示例 2：检查并移除 3 个特定自定义物品
```yaml
Condition: "@item|id:custom_gem-3"
```
- **说明**：使用 `id:` 匹配由 EasySaver/SXItem 定义的自定义物品 ID。
- **注意**：表达式支持 `and()` / `or()` / `not()` 逻辑组合。

#### 🧪 示例 3：结合表达式动态计算所需数量
```kotlin
// 配置项
Condition: "@item|name:(&c强化石)-{amount}"

// 在代码中传入 op = "{amount} * 2"
val actions = matcher.matches(text, "{amount} * 2", player, info)
```
- **效果**：原本需要 1 个强化石 → 实际判断是否拥有 2 个。

---

### 💰 `VaultMatcher` 使用案例

> 判断玩家经济余额，支持动态计算。

#### ✅ 基础语法
```text
@vault|<金额>
```

#### 🧪 示例 1：检查玩家是否有至少 100 金币
```yaml
Condition: "@vault|100.0"
```
- 若满足，则继续执行后续动作（如给予物品）。

#### 🧪 示例 2：动态价格（等级越高折扣越多）
```kotlin
val level = player.getLevel()
val basePrice = 200.0
val discount = "200 - (%player_level% * 5)" // 每级减5金币

// 计算实际价格
val finalPrice = Arim.fixedCalculator.evaluate(discount).toDouble()

// 匹配器调用
matcher.matches("@vault|$finalPrice", player, info)
```

#### 🧪 示例 3：结合表达式实现“花费一半金币”
```kotlin
val currentMoney = VaultHook.economy.getBalance(player)
val half = currentMoney / 2
matcher.matches("@vault|$half", player, info)
```

> 💡 提示：`VaultMatcher` 不会自动扣钱，需配合 `VaultAction` 使用：
```yaml
Actions:
  - "@vault|-50.0"  # 扣除50金币
```

---

## 🧱 扩展开发指南

### ➕ 自定义条件匹配器

实现 `MatcherStrategy` 接口：

```kotlin
class CustomMatcher : MatcherStrategy {
    override fun matches(text: String, thisPlayer: Player, info: infoType): Actions<*>? {
        return matches(text, "{amount}", thisPlayer, info)
    }

    override fun matches(text: String, op: String, thisPlayer: Player, info: infoType): Actions<*>? {
        // 解析 text
        // 执行条件判断
        return if (满足条件) {
            CustomAction().apply { data = "success" }
        } else {
            null
        }
    }
}

// 注册
FunctionManager.getMatcherManager().addMatcher("@custom", CustomMatcher())
```

### ➕ 自定义动作行为

实现 `Actions<T>` 接口：

```kotlin
class CustomAction : Actions<String> {
    override var data: String = ""

    override fun doAction(thisPlayer: Player) {
        thisPlayer.sendMessage("执行自定义动作: $data")
    }

    override fun doAction(thisPlayer: Player, text: String) {
        thisPlayer.sendMessage("接收到参数: $text")
    }
}

// 注册
FunctionManager.getActionManager().addAction("@custom", CustomAction())
```

---

## 🖼️ 快速创建 GUI

### 1️⃣ 创建 `gui.yml`

```yaml
Title: "&c物品库"
Name: "examplePool"

GuiPlain:
  - "#########"
  - "@@@@@@@@@"
  - "@@@@@@@@@"
  - "@@@@@@@@@"
  - "@@@@@@@@@"
  - "L#######N"

GuiKey:
  "@":
    IconFunction: "item"
    Material: "STAINED_GLASS_PANE"
    Data: 7
    Name: "&7装饰玻璃"
  "#":
    IconFunction: "any"
    Material: "WHITE_STAINED_GLASS_PANE"
    Name: "&f边框"
  "N":
    IconFunction: "next"
    normal:
      Material: "ENDER_PEARL"
      Name: "&7下一页"
      Lore: ["&8已经是最后一页"]
    has:
      Material: "ENDER_PEARL"
      Name: "&a下一页"
      Lore: ["&7点击切换到下一页"]
      Sound: "block.note.pling"
  "L":
    IconFunction: "last"
    normal:
      Material: "ENDER_PEARL"
      Name: "&7上一页"
      Lore: ["&8已经是第一页"]
    has:
      Material: "ENDER_PEARL"
      Name: "&a上一页"
      Lore: ["&7点击返回上一页"]
      Sound: "block.note.pling"
```

### 2️⃣ 创建构建器类

```kotlin
class CustomGuiBuilder(override val config: GuiConfig, player: Player) 
    : INormalGuiBuilder(config, player) {

    override fun open() {
        buildAndOpen {}
    }

    override fun mapIconsToFunctions() {
        mapIconsToFunctionWay { key, function ->
            when (function) {
                "item", "any" -> setDefaultIcon(key)
                else -> {}
            }
        }
    }
}
```

### 3️⃣ 打开 GUI

```kotlin
val file = File(dataFolder, "gui.yml")
val config = GuiConfig(file)
val builder = CustomGuiBuilder(config, player)
builder.open()
```

---

## 🔗 依赖说明

| 功能 | 依赖模块 | 用途 |
|------|----------|------|
| **物品匹配判断** | `easySaver.arim.Arim` | 用于 `ItemMatcher` 中的物品表达式解析与匹配 |
| **物品生成** | `EasyCrate.Factory.ItemGenerator.ItemGenerator` | 用于 `GiveItemAction` 生成自定义物品 |

> ⚠️ 注意：这两个依赖职责分离：
> - `Arim` 仅用于 **条件验证**
> - `ItemGenerator` 用于 **物品发放**

---

## 🛠️ 调试与日志

启用调试模式可在控制台查看：
- GUI 构建耗时
- 匹配器执行顺序
- 异常堆栈信息
- 表达式计算过程

```kotlin
info("调试信息: $msg")  // 使用 TabooLib 日志
```

---

## 📎 许可证

MIT License

---

> 📬 **贡献与反馈**  
> 欢迎提交 Issue 或 Pull Request！  
> 项目仓库：`https://github.com/yourname/EasyLib`  
> 开发者：@YourName  
> 支持版本：Minecraft 1.8+ | Java 8+

---

🎯 **EasyLib —— 让插件开发更简单、更高效。**
