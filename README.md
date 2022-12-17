# 流式数据处理框架stream

## 模块

> * stream-sdk: stream基础开发工具及接口，stream应用开发者基于此实现应用插件、资源和接收器
> * stream-framework: stream框架，实现流程编排、资源管理及应用生命周期管理
> * stream-receiver: 通用stream接收器实现，首发版本实现了阿里云RocketMQ接收器，阿里云OTS通道接收器，以及内存队列接收器
> * stream-plugin: 通用stream插件实现，首发版本仅实现了速度控制插件
> * stream-resource: 通用stream资源实现，首发版本实现了阿里云OTS相关资源、阿里云ONS相关资源及阿里云OSS客户端资源等
> * stream-test: stream测试相关套件

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

