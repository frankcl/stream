# 流式数据处理框架stream

## 特点
* 快速搭建数据处理流程，框架负责解决业务无关逻辑，例如切面日志记录，流程监控报警等
* 业务逻辑插件化：不同业务通过插件解耦，数据在插件中处理，在插件间传递和流转
* 配置化流程编排：用户通过配置文件编排数据接收器及插件，构建数据处理流程，支持多流程、多分支拓扑，灵活方便
* 重复利用：框架实现通用数据接收器、插件及资源，用户不用重复实现，利用已有实现配置即用

## 模块

### 1. stream-sdk 基础开发SDK [指南](https://github.com/frankcl/stream/blob/main/stream-sdk/README.md)
  - [x] 数据接收器SDK定义
  - [x] 数据处理插件SDK定义
  - [x] 资源SDK定义
  - [x] 预处理器SDK定义
  - [x] 开发注解定义
### 2. stream-framework 框架实现 [指南](https://github.com/frankcl/stream/blob/main/stream-framework/README.md)
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
### 6. stream-test 测试应用样例 [指南](https://github.com/frankcl/stream/blob/main/stream-test/README.md)

## stream流程拓扑示例

![stream_flow](https://github.com/frankcl/stream/blob/main/image/stream_flow.png)

## 如何搭建自己的stream应用？

* 依赖JAR包选择

 | 工件               | 分组         | 最新版本  | 是否必须 | 说明                  |
|:-----------------|:-----------|:------|:-----|:--------------------|
 | stream-sdk       | xin.manong | 0.0.8 | 是    | 开发SDK，定义数据接收器、插件和资源 |
 | stream-framework | xin.manong | 0.0.8 | 是    | stream运行框架及环境       |
 | stream-resource  | xin.manong | 0.0.8 | 否    | 通用资源组件实现，按需引入       |
 | stream-receiver  | xin.manong | 0.0.8 | 否    | 通用数据接收器实现，按需引入      |
 | stream-plugin    | xin.manong | 0.0.8 | 否    | 通用插件实现，按需引入         |
  
* stream应用搭建示例参见：[链接](https://github.com/frankcl/stream/blob/main/stream-test/src/main/resources/application.json)

## 依赖三方库信息

| 工件             | 分组                      |     版本      | optional |
|:---------------|:------------------------|:-----------:|:--------:|
| aliyun-sdk-oss | com.aliyun.oss          |   3.15.0    |   true   |
| commons-cli    | commons-cli             |     1.3     |  false   |
| commons-pool2  | org.apache.commons      |   2.11.1    |  false   |
| guava          | com.google.guava        |  31.1-jre   |   true   |
| kafka-clients  | org.apache.kafka        |    3.3.1    |   true   |
| ons-client     | com.aliyun.openservices | 1.8.0.Final |   true   |
| redisson       | org.redisson            |   3.19.0    |   true   | 
| slf4j-log4j12  | org.slf4j               |   1.7.25    |   true   |
| tablestore     | com.aliyun.openservices |   5.13.10   |   true   |
| weapon-alarm   | xin.manong              |    0.1.2    |  false   |
| weapon-aliyun  | xin.manong              |    0.1.2    |  false   |
| weapon-base    | xin.manong              |    0.1.2    |  false   |

