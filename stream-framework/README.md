# stream framework

## stream整体架构

![stream_architecture](https://github.com/frankcl/stream/blob/main/image/stream_architecture.png)

* 资源管理：管理stream应用所有Resource生命周期
  * ResourceConfig：资源定义，通过配置文件中资源描述进行抽象
  * ResourceFactory：资源工厂，根据ResourceConfig定义构建和销毁Resource
  * ResourcePool：对Resource的池化封装，保证Resource实例可控和复用，ResourcePool通过ResourceFactory进行Resource的构建和销毁
  * ResourceInjector：负责将Resource注入组件，组件包含Receiver、Plugin和Resource
  * ResourceManager：管理stream应用中所有Resource的生命周期，包含创建、销毁、租借和归还
* 流程编排：负责将stream应用定义的数据接收器和插件进行编排，组建成数据链路拓扑流程，并启动数据接收器接收数据，数据在链路图谱中处理流转，完成业务需求
  * 插件编排：根据配置构建插件，并根据插件之间上下游关系编排插件拓扑
    * ProcessorConfig：插件定义，定义插件的实现类、配置参数及下游插件关系
    * ProcessorGraph：插件拓扑图定义，负责构建和销毁插件，并根据插件依赖关系构建插件拓扑图；检测拓扑图的有效性，例如插件唯一性检测、DAG检测等
    * ProcessorGraphFactory：负责根据配置构建和销毁ProcessorGraph，保证每个线程有独占的ProcessorGraph
  * 数据接收器构建与对接：根据配置构建数据接收器，并根据下游插件配置构建ProcessorGraph完成对接，形成完整数据链路拓扑图
    * ReceiveControllerConfig：数据接收器定义，包含数据接收器实现类定义，数据转化器实现类定义，以及接收器和转换器配置参数定义
    * ReceiveController：根据ReceiveControllerConfig完成数据接收器生命周期管理，包含：初始化、启动、停止和销毁
    * ReceiveManager：管理stream应用定义的所有Receiver
* 流程启动和停止：通过StreamRunner完成，步骤如下
  * 解析main入口类注解，检测是否定义预处理逻辑，如果有执行预处理逻辑
  * 读取stream应用配置文件，默认classpath下application.json，根据配置文件完成以下操作
    * 资源解析和构建
    * 数据处理器解析和构建
    * 插件拓扑检测：解析和构建插件拓扑图，检测拓扑图有效性
  * 启动数据接收器，数据处理线程开始接收数据，并向下游插件分发数据
  * 以数据驱动的方式按需创建插件拓扑图（保证每个线程一个插件拓扑图），进行数据处理和流转

## stream启动时序
![stream_bootstrap_timeline](https://github.com/frankcl/stream/blob/main/image/stream_bootstrap_timeline.png)

