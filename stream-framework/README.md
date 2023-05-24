# stream framework

## stream整体架构

![stream_architecture](https://github.com/frankcl/stream/blob/main/image/stream_architecture.png)

* 资源管理：管理应用中所有资源生命周期，包含但不限于Resource

| 组件               | 可见性 | 引用关系                     | 说明                                      |
|:-----------------|:---:|:-------------------------|:----------------------------------------|
| Resource         | 开发者 | 无                        | 资源定义，开发者通过继承此类实现自定义资源                   |
 | ResourceConfig   | 框架  | 无                        | 资源配置信息，对应stream应用配置文件中resource定义        |
| ResourceFactory  | 框架  | ResourceConfig、Resource  | 根据ResourceConfig构建、销毁、钝化、激活Resource     |
| ResourcePool     | 框架  | ResourceFactory          | 池化资源实例，保证Resource实例可控及复用                |
 | ResourceManager  | 框架  | ResourcePool、Resource    | 管理stream应用所有Resource的生命周期，包含创建、销毁、租借和归还 |
| ResourceInjector | 框架  | ResourceManager、Resource | 负责将Resource注入Receiver、Plugin和Resource   |

* 数据接收器管理：管理应用中所有数据接收器生命周期，包含但不限于Receiver

| 组件                      | 可见性 | 包含关系                                                               | 说明                                           |
|:------------------------|:---:|:-------------------------------------------------------------------|:---------------------------------------------|
| Receiver                | 开发者 | ReceiveProcessor                                                   | 数据接收器，开发者通过继承此类实现自定义数据接收器                    |
| ReceiveConverter        | 开发者 | 无                                                                  | 数据转换器，开发者通过继承此类实现自定义数据转换器                    |
| ReceiveProcessor        | 框架  | ReceiveConverter、ProcessorGraph                                    | 接收数据处理器，负责调用ReceiveConverter转换数据并分发数据给下游插件处理 |
| ReceiveControllerConfig | 框架  | 无                                                                  | 数据接收控制器配置，接收器和转化器配置信息，下游插件配置信息               |
| ReceiveController       | 框架  | ReceiveControllerConfig、ReceiveProcessor、Receiver、ReceiveConverter | 控制用户定义Receiver及ReceiveConverter生命周期          |
| ReceiveManager          | 框架  | ReceiveController、ReceiveControllerConfig                          | 根据配置生成应用定义ReceiveController，并管理其生命周期         |

* 插件管理：管理应用中所有插件生命周期，包含但不限于Plugin

| 组件                    | 可见性 | 包含关系                      | 说明                                         |
|:----------------------|:---:|:--------------------------|:-------------------------------------------|
| Plugin                | 开发者 | 无                         | 插件定义，开发者通过继承此类实现自定义插件                      |
| ProcessorConfig       | 框架  | 无                         | Processor配置：定义Plugin配置信息及下游插件信息            |
| Processor             | 框架  | Plugin、Processor          | 用户Plugin封装，以及下游分支Processor定义               |
| ProcessorGraph        | 框架  | ProcessorConfig、Processor | Processor拓扑图定义，应用进程中每个线程独占一个ProcessorGraph |
| ProcessorGraphFactory | 框架  | ProcessorGraph            | 负责管理应用中所有ProcessorGraph生命周期                |

* 流程编排：负责将stream应用定义的数据接收器和插件进行编排，组建成数据链路拓扑，并启动数据接收器接收数据，数据在链路图谱中处理流转，完成业务需求
  * 插件拓扑图构建：根据配置构建插件，根据配置描述上下游关键组装生产ProcessorGraph
  * 数据接收器构建与对接：根据配置构建数据接收器，根据配置描述的下游插件信息完成ProcessorGraph中的插件对接

* 流程启动和停止：StreamRunner负责完成应用的启动和停止，步骤如下

## stream启动时序
![stream_bootstrap_timeline](https://github.com/frankcl/stream/blob/main/image/stream_bootstrap_timeline.png)

* 解析main入口类注解，检测是否定义预处理逻辑，如果有执行预处理逻辑
* 读取stream应用配置文件，默认classpath下application.json，生成StreamRunnerConfig
* 根据配置构建和启动报警发送器
* 资源解析、构建和注册
* 插件拓扑图检测：解析和构建插件拓扑图，检测拓扑图有效性（是否存在同名插件，是否存在环等）
* 数据接收器解析和构建，与插件拓扑图对接组成完整链路流程
* 启动数据接收器：数据处理线程开始接收数据，并向下游插件分发数据
* 以数据驱动的方式按需创建插件拓扑图，进行数据处理、流转和落地（保证并发安全性，框架保证每个线程独占一个插件拓扑图）

