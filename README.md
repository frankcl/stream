# 流式数据处理框架stream

## 特点

- **快速搭建**：框架负责日志记录、流程监控、报警等业务无关逻辑，开发者专注业务实现
- **插件化**：业务逻辑通过插件（Plugin）解耦，数据在插件间传递流转，提高可读性和可维护性
- **配置化编排**：通过 JSON 配置文件编排接收器（Receiver）与插件，支持多流程、多分支 DAG 拓扑
- **资源池化**：连接、客户端等高开销资源统一管理，支持对象池化和依赖注入
- **开箱即用**：内置 Kafka、RocketMQ、ElasticSearch、Redis、Milvus 等常用组件实现

## 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                        StreamRunner                             │
│                      (应用生命周期管理)                            │
├──────────┬───────────────────────────────────┬──────────────────┤
│          │                                   │                  │
│  ReceiveManager        ProcessorGraph       ResourceManager     │
│  (接收器管理)          (插件DAG拓扑)          (资源池管理)         │
│          │                                   │                  │
│  ┌───────┴───────┐   ┌──────────────────┐   ┌┴───────────────┐ │
│  │ReceiveController│  │    Processor     │   │  ResourcePool  │ │
│  │  ┌──────────┐  │  │  ┌──────────┐   │   │  ┌──────────┐  │ │
│  │  │ Receiver │  │──│  │  Plugin   │   │──│  │ Resource  │  │ │
│  │  └──────────┘  │  │  └──────────┘   │   │  └──────────┘  │ │
│  │  ┌──────────┐  │  │    fork路由      │   │   对象池化      │ │
│  │  │Converter │  │  │  ┌──┐ ┌──┐      │   │   @Resource    │ │
│  │  └──────────┘  │  │  │P1│→│P2│→...  │   │   依赖注入      │ │
│  └────────────────┘  │  └──┘ └──┘      │   └────────────────┘ │
│                      └──────────────────┘                      │
└─────────────────────────────────────────────────────────────────┘
```

### 数据流

```
外部数据源 (Kafka/RocketMQ/OTS/...)
    ↓
Receiver (接收原始数据)
    ↓
ReceiveConverter (转换为 KVRecords)
    ↓
ProcessorGraph (DAG 拓扑路由)
    ↓
Plugin.handle(KVRecord) → ProcessResult (fork 分支路由)
    ↓                          ↓
Plugin A (fork=success)    Plugin B (fork=fail)
    ↓
Plugin C (fork=next)
```

**核心数据结构**：框架统一使用 `KVRecord`（键值记录）作为数据载体，所有数据源通过 `ReceiveConverter` 转换为 KVRecords 后进入处理管道。

## 模块结构

```
stream/
├── stream-sdk           # 开发SDK：核心抽象和注解（Plugin, Receiver, Resource）
├── stream-framework     # 运行框架：生命周期管理、DAG拓扑、资源池化
├── stream-receiver      # 通用接收器：Kafka, RocketMQ, ONS, MNS, OTS 等
├── stream-resource      # 通用资源：Redis, ElasticSearch, Milvus, OSS 等
├── stream-plugin        # 通用插件：分布式限流等
├── stream-config-map    # 配置管理：基于 etcd 的动态配置
└── stream-test          # 测试样例：示例应用
```

## 指南

 - [x] stream开发SDK：[链接](https://github.com/frankcl/stream/blob/main/stream-sdk/README.md)
 - [x] stream架构原理：[链接](https://github.com/frankcl/stream/blob/main/stream-framework/README.md)
 - [x] stream通用数据接收器：[链接](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md)
 - [x] stream通用资源：[链接](https://github.com/frankcl/stream/blob/main/stream-resource/README.md)
 - [x] stream通用插件：[链接](https://github.com/frankcl/stream/blob/main/stream-plugin/README.md)
 - [x] stream测试应用样例：[链接](https://github.com/frankcl/stream/blob/main/stream-test/README.md)

## stream流程拓扑示例

![stream_flow](https://github.com/frankcl/stream/blob/main/image/stream_flow.png)

## 如何搭建自己的stream应用？

* stream应用搭建示例参见：[链接](https://github.com/frankcl/stream/blob/main/stream-test/README.md)

* 依赖JAR包选择

 | 工件               | 分组         | 最新版本  | 是否必须 | 说明                  |
|:-----------------|:-----------|:------|:-----|:--------------------|
 | stream-sdk       | xin.manong | 0.1.7 | 是    | 开发SDK，定义数据接收器、插件和资源 |
 | stream-framework | xin.manong | 0.1.7 | 是    | stream运行框架及环境       |
 | stream-resource  | xin.manong | 0.1.7 | 否    | 通用资源组件实现，按需引入       |
 | stream-receiver  | xin.manong | 0.1.7 | 否    | 通用数据接收器实现，按需引入      |
 | stream-plugin    | xin.manong | 0.1.7 | 否    | 通用插件实现，按需引入         |

## 依赖三方库信息

| 工件                  | 分组                      |      版本      | optional |
|:--------------------|:------------------------|:------------:|:--------:|
| aliyun-log          | com.aliyun.openservices |    0.6.82    |   true   |
| aliyun-log-producer | com.aliyun.openservices |    0.3.12    |   true   |
| aliyun-sdk-datahub  | com.aliyun.datahub      | 2.3.0-public |   true   |
| aliyun-sdk-oss      | com.aliyun.oss          |    3.15.0    |   true   |
| commons-cli         | commons-cli             |     1.3      |  false   |
| commons-pool2       | org.apache.commons      |    2.11.1    |  false   |
| elasticsearch-java  | co.elastic.clients      |    8.13.4    |   true   |
| guava               | com.google.guava        |  33.3.1-jre  |   true   |
| kafka-clients       | org.apache.kafka        |    3.9.0     |   true   |
| milvus-sdk-java     | io.milvus               |    2.6.12    |   true   |
| ons-client          | com.aliyun.openservices | 1.8.0.Final  |   true   |
| redisson            | org.redisson            |    3.38.1    |   true   | 
| rocketmq-client     | org.apache.rocketmq     |    5.3.3     |   true   | 
| slf4j-reload4j      | org.slf4j               |    2.0.16    |   true   |
| tablestore          | com.aliyun.openservices |   5.13.10    |   true   |
| weapon-alarm        | xin.manong              |    0.3.0     |  false   |
| weapon-aliyun       | xin.manong              |    0.3.0     |  false   |
| weapon-base         | xin.manong              |    0.3.0     |  false   |

