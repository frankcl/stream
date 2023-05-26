# 开发者SDK

## 1. stream数据抽象
### 1.1. 传输及处理数据KVRecord
* 本质为Map封装，将数据用keyValue对表示
* 定义数据类型RecordType：PUT新增、UPDATE更新、DELETE删除
* 每个KVRecord有唯一ID
* 每个KVRecord定义主键，主键为列表集合

```java
package xin.manong.weapon.base.record;

public class KVRecord implements Serializable {
    /**
     * 数据唯一ID
     */
    private String id;
    /**
     * 数据类型：新增PUT，更新UPDATE，删除DELETE
     */
    private RecordType recordType;
    /**
     * 主键集合
     */
    private final Set<String> keys;
    /**
     * 数据字段
     */
    private final Map<String, Object> fieldMap;
}
```
### 1.2. 分发数据ProcessResult
* 定义分发结果，本质为Map封装，Map key代表分支，Map value代表分支数据列表
* 分支数据使用KVRecords封装，KVRecords本质为KVRecord列表

```java
package xin.manong.stream.sdk.common;

public class ProcessResult implements Serializable {
    /**
     * 分支数据
     */
    private Map<String, KVRecords> forkMap;
}
```

## 2. 资源Resource
* 对独立功能组件进行封装，提高复用性，避免组件重复开发，例如数据库客户端，消息队列发送客户端等
* 采用对象池实现，避免资源实例野蛮生长，同时满足资源重复利用需求
* 用户定义资源，stream框架管理资源：创建、租借及销毁
* 用户通过注解标注资源，stream框架根据注解将资源注入其他组件，注入目标包含
  * 插件Plugin
  * 数据接收器Receiver
  * 资源Resource

### 2.1. 如何实现自己的Resource？
* 继承抽象资源类：xin.manong.stream.sdk.resource.Resource
* 实现create和destroy方法
  * create：定义资源如何创建
  * destroy：定义资源如何销毁
  * validate（可选）：定义资源可用性验证，默认返回true
* 可选覆盖validate方法：验证资源是否有效，stream会对无效资源进行销毁和重建。此方法默认返回true

```java
package xin.manong.stream.sdk.resource;

public abstract class Resource<T> {
    /**
     * 根据配置创建资源
     *
     * @param configMap 配置信息
     */
    public abstract T create(Map<String, Object> configMap);

    /**
     * 销毁资源
     */
    public abstract void destroy();

    /**
     * 验证资源有效性，默认实现返回true
     * 自定义资源可以复写该方法，无效资源可触发框架进行销毁
     *
     * @return 如果有效返回true，否则返回false
     */
    public boolean validate() {
        return true;
    }
}
```

### 2.2. 如何定义Resource？
* 在哪里定义stream应用使用的资源？
  * stream应用配置文件中定义：默认application.json
  * 配置文件中resources部分定义资源
* 定义资源需要做哪些事儿？
  * resource名称：name（必填）
  * resource全限定类名：className（必填）
  * resource实例数，默认为1：num（可选）
  * resource配置参数：configMap（必填）

```json
{
  "resources": [
    {
      "name": "producer",
      "className": "xin.manong.stream.boost.resource.ons.ONSProducerResource",
      "configMap": {
        "serverURL": "http://onsaddr.cn-hangzhou.mq-internal.aliyuncs.com:8080"
      }
    }
  ]
}
```

## 3. 数据接收器Receiver
* 对数据源进行封装，实现数据的接收及向下分发，例如ONS消息数据接收器、OTS通道数据接收器、Kafka消息数据接收器等
* 对接收数据进行转换，生成stream处理数据KVRecord并向下游分发

### 3.1. 如何实现自己的Receiver？
* 继承抽象数据接收器：xin.manong.stream.sdk.receiver.Receiver
* 实现start和stop方法
  * start：定义如何启动数据接收器，接收和分发数据
  * stop：定义如何停止数据接收器，销毁资源

```java
package xin.manong.stream.sdk.receiver;

public abstract class Receiver {
    /**
     * 启动接收器
     *
     * @return 成功返回true，否则返回false
     */
    public abstract boolean start();

    /**
     * 停止接收器
     *
     * @return 成功返回true，否则返回false
     */
    public abstract void stop();
}
```

### 3.2. 如何实现自己的数据转换器？
* 继承抽象数据转换器：xin.manong.stream.sdk.receiver.ReceiveConverter
* 实现数据转换方法
  * convert：定义数据源转换为KVRecord方法
  * init（可选）：初始化，自定义需要覆盖
  * destroy（可选）：销毁，自定义需要覆盖

```java
package xin.manong.stream.sdk.receiver;

public abstract class ReceiveConverter {
    /**
     * 处理数据
     *
     * @param context 上下文
     * @param object 待处理数据
     * @throws Exception 转换异常
     * @return KVRecords
     */
    public abstract KVRecords convert(Context context, Object object) throws Exception;

    /**
     * 初始化
     *
     * @return 成功返回true，否则返回false
     */
    public boolean init() {
      return true;
    }
  
    /**
     * 销毁
     */
    public void destroy() {
    }
}
```

### 3.3. 如何定义Receiver？
* 在哪里定义stream应用使用的数据接收器？
  * stream应用配置文件中定义：默认application.json
  * 配置文件中receivers部分定义数据接收器
* 定义数据接收器需要做哪些事儿？
  * receiver名称：name（必填）
  * 数据接收器全限定类名：receiverClass（必填）
  * 数据接收器配置参数：receiverConfigMap（必填）
  * 分发插件列表：processors（必填）
  * 数据转换器全限定类名，converterClass（可选，默认不进行转化）
  * 数据转换器配置参数：converterConfigMap（可选）

```json
{
  "receivers": [
    {
      "name": "dummy_receiver",
      "receiverClass": "xin.manong.stream.boost.receiver.ons.ONSReceiver",
      "converterClass": "xin.manong.stream.boost.receiver.ons.JSONMessageConverter",
      "receiverConfigMap": {
        "consumeThreadNum": 16,
        "consumeId": "GID_TEST_TOPIC",
        "serverURL": "http://onsaddr.cn-hangzhou.mq-internal.aliyuncs.com:8080",
        "subscribes": [
          {
            "topic": "TEST_TOPIC",
            "tags": "*"
          }
        ]
      },
      "processors": ["dummy_processor"]
    }
  ]
}
```

## 4. 插件Plugin
* 对应用流程处理逻辑进行拆分，独立业务逻辑使用插件进行封装
* 插件生命周期由stream框架管理，插件生命周期分为3个阶段
  * 初始化：构建插件运行所需环境和参数，为插件注入所需资源Resource
  * 处理和分发数据：接收上游Receiver或Plugin分发数据，进行处理，并将结果分发到下游Plugin
  * 销毁：清理销毁运行现场，并销毁插件运行时创建的资源
* 插件根据业务需求定义数据分发路径，例如类型1数据分发到下游分支1、类型2数据分发到下游分支2 ...

### 4.1. 如何实现自己的Plugin？
* 继承抽象类Plugin：xin.manong.stream.sdk.plugin.Plugin
* 实现和覆盖对应方法
  * handle：定义数据处理和分发逻辑
  * init（可选）：初始化插件所需资源，默认返回true
  * destroy（可选）：销毁插件现场和创建资源，默认实现为空
  * flush（可选）：定义插件刷新逻辑，保证插件hold资源可以落地，stream框架定期调用flush方法，默认实现为空

```java
package xin.manong.stream.sdk.plugin;

public abstract class Plugin {
    /**
     * 处理数据
     *
     * @param kvRecord 数据
     * @return 处理结果
     * @throws Exception
     */
    public abstract ProcessResult handle(KVRecord kvRecord) throws Exception;

    /**
     * flush插件内部数据，保证数据落地
     * 默认实现为空，用户可覆写此方法
     */
    public void flush() {
    }
  
    /**
     * 初始化插件
     *
     * @return 如果成功返回true，否则返回false
     */
    public boolean init() {
      return true;
    }
  
    /**
     * 销毁插件
     *
     * @return 销毁成功返回true，否则返回false
     */
    public void destroy() {
    }
}
```

### 4.2. 如何定义Plugin？
* 在哪里定义stream应用使用的插件？
  * stream应用配置文件中定义：默认application.json
  * 配置文件中processors部分定义插件
* 定义插件需要做哪些事儿？
  * 插件名称：name（必填）
  * 插件全限定类名：className（必填）
  * 插件配置参数：configMap（可选）
  * 分发插件：processors（可选）

```json
{
  "processors": [
    {
      "name": "data_checker",
      "className": "xin.manong.stream.plugin.DataChecker",
      "pluginConfig": {
      },
      "processors": {
        "next": "expired_filter"
      }
    }
  ]
}
```

## 5. 预处理器Preprocessor
* 调用时机：stream应用启动前被框架调用
* 依赖注解xin.manong.stream.sdk.annotation.Import进行导入

### 5.1. 如何实现自己的Preprocessor？
* 集成抽象类xin.manong.stream.sdk.prepare.Preprocessor
* 实现process方法：预处理逻辑

```java
package xin.manong.stream.sdk.prepare;
public abstract class Preprocessor {

  /**
   * 预处理
   */
  public abstract void process();
}
```

### 5.2. 如何使定义的预处理器生效
* 定义预处理注解，在预处理注解之上声明Import注解，导入预处理器实现
* 预处理器实现：可获取预处理注解信息，进行预处理逻辑定义
