# stream架构原理

## 1. stream整体架构

![stream_architecture](https://github.com/frankcl/stream/blob/main/image/stream_architecture.png)

### 1.1. 基础组件

* 资源管理：管理应用中所有资源生命周期，资源组件体系如下

| 组件               | 可见性 | 引用关系                     | 说明                                      |
|:-----------------|:---:|:-------------------------|:----------------------------------------|
| Resource         | 开发者 | 无                        | 资源定义，开发者通过继承此类实现自定义资源                   |
 | ResourceConfig   | 框架  | 无                        | 资源配置信息，对应stream应用配置文件中resource定义        |
| ResourceFactory  | 框架  | ResourceConfig、Resource  | 根据ResourceConfig构建、销毁、钝化、激活Resource     |
| ResourcePool     | 框架  | ResourceFactory          | 池化资源实例，保证Resource实例可控及复用                |
 | ResourceManager  | 框架  | ResourcePool、Resource    | 管理stream应用所有Resource的生命周期，包含创建、销毁、租借和归还 |
| ResourceInjector | 框架  | ResourceManager、Resource | 负责将Resource注入Receiver、Plugin和Resource   |

* 数据接收器管理：管理应用中所有数据接收器生命周期，数据接收器组件体系如下

| 组件                      | 可见性 | 包含关系                                                               | 说明                                           |
|:------------------------|:---:|:-------------------------------------------------------------------|:---------------------------------------------|
| Receiver                | 开发者 | ReceiveProcessor                                                   | 数据接收器，开发者通过继承此类实现自定义数据接收器                    |
| ReceiveConverter        | 开发者 | 无                                                                  | 数据转换器，开发者通过继承此类实现自定义数据转换器                    |
| ReceiveProcessor        | 框架  | ReceiveConverter、ProcessorGraph                                    | 接收数据处理器，负责调用ReceiveConverter转换数据并分发数据给下游插件处理 |
| ReceiveControllerConfig | 框架  | 无                                                                  | 数据接收控制器配置，接收器和转化器配置信息，下游插件配置信息               |
| ReceiveController       | 框架  | ReceiveControllerConfig、ReceiveProcessor、Receiver、ReceiveConverter | 控制用户定义Receiver及ReceiveConverter生命周期          |
| ReceiveManager          | 框架  | ReceiveController、ReceiveControllerConfig                          | 根据配置生成应用定义ReceiveController，并管理其生命周期         |

* 插件管理：管理应用中所有插件生命周期，插件组件体系如下

| 组件                    | 可见性 | 包含关系                      | 说明                                         |
|:----------------------|:---:|:--------------------------|:-------------------------------------------|
| Plugin                | 开发者 | 无                         | 插件定义，开发者通过继承此类实现自定义插件                      |
| ProcessorConfig       | 框架  | 无                         | Processor配置：定义Plugin配置信息及下游插件信息            |
| Processor             | 框架  | Plugin、Processor          | 用户Plugin封装，以及下游分支Processor定义               |
| ProcessorGraph        | 框架  | ProcessorConfig、Processor | Processor拓扑图定义，应用进程中每个线程独占一个ProcessorGraph |
| ProcessorGraphFactory | 框架  | ProcessorGraph            | 负责管理应用中所有ProcessorGraph生命周期                |

### 1.2. 流程编排
将零散的数据接收器和插件根据上下游依赖关系进行连接和编排，形成完整的stream链路流程
* 构建插件拓扑图ProcessorGraph
  * 根据配置构建独立插件
  * 根据上下游依赖关系连接组装插件，生成ProcessorGraph
  * ProcessorGraph检测：保证ProcessorGraph连通性和有效性
* 构建完整stream链路流程
  * 根据配置构建数据接收器
  * 根据数据接收器的下游配置完成与ProcessorGraph中对应插件的对接

## 2. stream如何运行

### 2.1. 启动流程

![stream_bootstrap_timeline](https://github.com/frankcl/stream/blob/main/image/stream_bootstrap_timeline.png)

* 解析main入口类注解
  * StreamApplication注解解析：获取应用名和配置文件信息（默认classpath下application.json）
  * Import注解解析：解析Import注解执行预处理逻辑
* 解析stream应用配置文件，生成内存配置对象StreamRunnerConfig
* 构建stream切面日志记录，构建和启动报警发送器
* 资源Resource解析、构建和注册
* 插件拓扑图ProcessorGraph检测
  * 解析和构建ProcessorGraph
  * 检测ProcessorGraph连通性和有效性：同名插件检测，DAG有效性检测等
* 数据接收器Receiver解析和构建，生成完整stream链路流程
  * 解析和构建接收数据转换器
  * 解析和构建数据接收器
  * 对接ProcessorGraph组成完整stream链路流程
* 启动数据接收器：数据处理线程开始接收数据，并向下游插件分发数据

特殊说明
* 以数据驱动的方式按需创建ProcessorGraph，ProcessorGraph进行数据处理、流转和落地（为保证并发安全性，每个线程独占一个ProcessorGraph）
* 框架在资源、数据接收器及插件初始化之前，完成依赖资源的注入

### 2.2. 停止流程

![stream_stop_timeline](https://github.com/frankcl/stream/blob/main/image/stream_stop_timeline.png)

停止流程为启动流程的逆向工程，主要完成现场清理和资源销毁工作
* 切断数据源：停止所有数据接收器
  * 停止和销毁数据接收器
  * 销毁接收数据转换器
* 清理ProcessorGraph：清理所有线程相关ProcessorGraph
  * 找到ProcessorGraph入口插件
  * 以入口插件为起点开始销毁插件
* 资源清理：注销所有资源实例
* 停止报警发送器

## 3. stream框架还做了哪些工作？

* 资源注入：针对数据接收器、插件和资源本身，完成引用资源的自动注入
  * 按资源名进行注入
  * 按资源类型进行注入，限制条件：同一类型资源保证全局唯一
* 切面日志记录：记录stream处理的每一条记录，可对接SLS，进一步完成统计、报表和问题定位
* 数据跟踪：每一条处理路径有唯一TraceID，每一条数据有唯一ID，可通过TraceID对相同路径处理数据进行跟踪，通过数据ID定位数据
