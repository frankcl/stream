# 流式数据处理框架stream

## 特点
* 快速搭建数据处理流程，框架负责解决业务无关逻辑，例如切面日志记录，流程监控报警等
* 业务逻辑插件化：不同业务通过插件解耦，数据在插件中处理，在插件间传递和流转
* 配置化流程编排：用户通过配置文件编排数据接收器及插件，构建数据处理流程，支持多流程、多分支拓扑，灵活方便
* 重复利用：框架实现通用数据接收器、插件及资源，用户不用重复实现，利用已有实现配置即用

## 指南

 - [x] stream开发SDK：[链接](https://github.com/frankcl/stream/blob/main/stream-sdk/README.md)
 - [x] stream架构原理：[链接](https://github.com/frankcl/stream/blob/main/stream-framework/README.md)
 - [x] stream通用数据接收器：[链接](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md)
 - [x] stream通用资源
 - [x] stream通用插件
 - [x] stream测试应用样例：[链接](https://github.com/frankcl/stream/blob/main/stream-test/README.md)

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

