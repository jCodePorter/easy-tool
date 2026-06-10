# 自适应多层时间轮算法 

## 概述
这是一个基于Java实现的自适应多层时间轮算法，支持**一次性任务**和**重复任务**的调度。采用动态层级管理策略，根据任务延迟自动创建和扩展时间轮层级，并提供丰富的条件终止机制。

## 核心特性

### 🚀 动态层级管理
- **无需预定义**: 不再需要固定创建秒轮、分轮、时轮
- **按需扩展**: 根据任务延迟自动创建合适的时间轮层级
- **智能分配**: 任务自动分配到最合适的时间轮层级
- **无限扩展**: 理论上支持任意长度的延迟时间

### ⚡ 高性能设计
- **1秒精度**: 基础时间轮提供1秒级精度
- **并发安全**: 读写锁机制保证线程安全
- **任务级联**: 时间轮层级间智能任务级联
- **内存优化**: 动态层级避免不必要的内存占用

### 🎯 智能调度
- **自动扩容**: 遇到超长延迟任务自动创建高层级时间轮
- **任务降级**: 时间轮推进时任务自动降级到低层级
- **延迟计算**: 智能计算任务剩余延迟时间
- **优雅降级**: 系统关闭时优雅处理所有任务

### 🔄 重复任务支持
- **智能调度**: 支持按固定间隔重复执行的任务
- **条件终止**: 基于时间、次数、性能等条件的智能终止
- **任务统计**: 提供详细的执行统计信息
- **动态管理**: 支持运行时启动、停止和监控重复任务

## 核心类

### TimeWheelTask
- 基础任务类，实现Comparable接口
- 支持任务取消和延迟执行
- 提供任务ID和执行时间管理

### TimeWheel
- 时间轮实现，支持任意层级
- 父子关系管理，形成时间轮链
- 智能任务分配和级联

### AdaptiveTimeWheel
- 自适应时间轮管理器
- 动态创建和管理时间轮层级
- 读写锁保证线程安全
- 内置重复任务支持

### RepeatingTimeWheelTask
- 重复任务实现，继承自TimeWheelTask
- 支持条件终止、执行统计、性能监控
- 线程安全的任务状态管理

### TaskStopConditions
- 丰富的条件终止策略集合
- 支持时间、次数、性能、自定义等多种终止条件
- 支持条件的逻辑组合（AND/OR）

## 架构设计

### 时间轮层级结构
```
Level 0 (基础轮): 10 slots × 1000ms = 最大延迟10秒
Level 1:          10 slots × 10000ms = 最大延迟100秒
Level 2:          10 slots × 100000ms = 最大延迟1000秒
Level 3:          10 slots × 1000000ms = 最大延迟10000秒
... (按需扩展，最多 10 层)
```

### 动态扩容机制
1. **任务添加**: 根据延迟时间自动选择合适层级
2. **容量检查**: 当前层级无法容纳时自动创建新层级
3. **层级关联**: 新层级自动建立父子关系
4. **任务迁移**: 超出当前层级的任务自动上浮

## 使用方法

### 基本使用
```java
// 创建自适应时间轮
AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();

// 添加一次性任务 - 系统自动选择合适的时间轮层级
String taskId1 = timeWheel.addTask(() -> {
    System.out.println("5秒后执行");
}, 5);

String taskId2 = timeWheel.addTask(() -> {
    System.out.println("1小时后执行");
}, 3600);

// 添加重复任务
String repeatingTaskId = timeWheel.addRepeatingTask(() -> {
    System.out.println("每2秒执行一次 - " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
}, 2);

// 添加带次数限制的重复任务
String limitedTaskId = timeWheel.addRepeatingTask(() -> {
    System.out.println("每1秒执行，最多5次");
}, 1, 5);

// 关闭时间轮
timeWheel.shutdown();
```

### 条件终止示例

```java


// 基于时间终止 (10秒后停止)
String timeTask = timeWheel.addRepeatingTask(() -> {
    System.out.println("10秒后停止的任务");
}, 1, TimeBasedStopCondition.afterSeconds(10));

        // 基于执行次数终止 (执行15次后停止)
        String countTask = timeWheel.addRepeatingTask(() -> {
            System.out.println("15次后停止的任务");
        }, 1, new ExecutionCountStopCondition(15));

        // 复合条件 (10秒内执行超过5次 OR 平均执行时间超过100ms)
        String complexTask = timeWheel.addRepeatingTask(() -> {
            System.out.println("复合条件任务");
        }, 1, any(
                all(TimeBasedStopCondition.afterSeconds(10), new ExecutionCountStopCondition(5)),
                new PerformanceStopCondition(100.0, 3)
        ));
```

### 监控和调试
```java
// 获取当前层级数
int levels = timeWheel.getWheelLevels();

// 获取待处理任务数
int pendingTasks = timeWheel.getTotalPendingTasks();

// 获取运行状态
boolean running = timeWheel.isRunning();

// 打印时间轮整体状态
System.out.println(timeWheel.toString());
```

## 性能指标

### 时间精度
- **基础精度**: 1秒
- **高层级精度**: 按层级递增 (Level N: 10^N × 10 秒)
- **重复任务精度**: 按指定间隔精确执行

### 容量支持
- **理论最大延迟**: 受限于整数范围和最大层级数
- **实际最大延迟**: 10^10 秒 (约 317 年)
- **最大层级数**: 10 (可配置)
- **重复任务数**: 理论上无限制

### 并发性能
- **线程安全**: 读写锁机制
- **任务并发**: 线程池异步执行
- **内存消耗**: O(任务数量)

## 核心算法

### 层级选择算法
```java
// canHandleDelay 判断当前轮是否能容纳指定延迟
// maxDelay = slotSize * tickMs / 1000
// 基础轮: 10 * 1000 / 1000 = 10s
// Level 1: 10 * 10000 / 1000 = 100s, 以此类推
```

### 动态扩容算法
```java
void ensureSufficientCapacity(int taskDelay) {
    // 快速检查（无锁）
    if (topLevelWheel.canHandleDelay(taskDelay)) return;
    // 写锁下双重检查 + 扩容
    wheelLock.writeLock().lock();
    try {
        while (!topLevelWheel.canHandleDelay(taskDelay)) {
            TimeWheel newWheel = createHigherLevelWheel(topLevelWheel);
            topLevelWheel.setParent(newWheel);
            newWheel.setChild(topLevelWheel);
            topLevelWheel = newWheel;
        }
    } finally {
        wheelLock.writeLock().unlock();
    }
}
```

### 任务级联算法
```java
// 基础轮归零时触发父轮推进 + 级联
// advance() 中:
if (nextSlot == 0 && parent != null) {
    parent.advance();
    cascadeTasksFromParent();  // 将父轮当前槽位任务降级到当前轮
}
```

## 优化点

### 相比固定层级方案
1. **内存效率**: 只创建需要的时间轮层级
2. **扩展性**: 支持任意长度的延迟时间
3. **灵活性**: 不受限于固定的秒分时结构
4. **智能化**: 自动选择最优的时间轮层级

### 性能优化
1. **读写分离**: 读写锁提高并发性能
2. **任务缓存**: 避免频繁的任务迁移
3. **懒加载**: 时间轮按需创建
4. **批量处理**: 任务批量级联和执行

## 注意事项

### 使用限制
- 最小延迟时间: 1秒
- 最大层级数: 10 (可配置)
- 线程安全: 需要正确处理并发访问

### 最佳实践
- 合理设置任务延迟时间
- 及时取消不需要的任务
- 监控时间轮状态和性能
- 优雅关闭时间轮