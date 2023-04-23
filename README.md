# 流式数据处理框架stream

## 特点
> * 快速搭建数据处理流程，框架负责解决业务无关逻辑，例如切面日志记录，流程监控报警等
> * 业务逻辑插件化：不同业务通过插件解耦，数据在插件中处理，在插件间传递和流转
> * 配置化流程编排：用户通过配置文件编排数据接收器及插件，构建数据处理流程，支持多流程、多分支拓扑，灵活方便
> * 重复利用：框架实现通用数据接收器、插件及资源，用户不用重复实现，利用已有实现配置即用

## 模块

### 1. stream-sdk 基础开发SDK
  - [x] 数据接收器SDK定义
  - [x] 数据处理插件SDK定义
  - [x] 资源SDK定义
  - [x] 预处理器SDK定义
  - [x] 开发注解定义
### 2. stream-framework 框架实现
  - [x] 流程编排及调度
  - [x] 资源管理：注册，获取，归还及销毁
### 3. stream-receiver 通用接收器实现
  - [x] 阿里云ONS数据消费实现
  - [x] 阿里云OTS数据通道消费实现
  - [x] Kafka数据消费实现
  - [x] 基于内存阻塞队列的数据消费实现：用于流程内部数据流转
  - [x] Fake数据消费：周期性触发伪数据消费
### 4. stream-plugin 通用插件实现
  - [x] 速度控制插件
### 5. stream-resource 通用资源实现
  - [x] 阿里云ONS数据生产实现
  - [x] 阿里云OSS客户端实现
  - [x] 阿里云OTS客户端实现
  - [x] Kafka数据生产实现
  - [x] Redis客户端实现
  - [x] 单进程速度控制器
  - [x] 分布式速度控制器
  - [x] 内存阻塞队列
### 6. stream-test 测试相关套件

## 使用

stream应用开发请引入sdk和framework artifacts

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>stream-sdk</artifactId>
    <version>按需参考release信息</version>
</dependency>
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>stream-framework</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

使用通用stream通用资源实现，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>stream-resource</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

使用通用stream通用接收器实现，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>stream-receiver</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

使用通用stream通用插件实现，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>stream-plugin</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

