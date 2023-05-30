# stream应用样例

## 1. 搭建stream应用需要做哪些事儿？

 - [x] 选择资源，没有满足需求的资源需要自己构建
 - [x] 选择数据接收器，没有满足需求的数据接收器需要自己构建
 - [x] 根据业务需求选择和构建插件
 - [x] 构建application.json，编排数据接收器和插件，组成stream应用流程
 - [x] 新建stream应用程序入口Application，main函数中调用StreamRunner的run方法

## 2. stream应用样例：fake_stream

 - [x] 应用程序入口：[Application](https://github.com/frankcl/stream/blob/main/stream-test/src/main/java/xin/manong/stream/test/Application.java)
 - [x] 应用配置文件：[application.json](https://github.com/frankcl/stream/blob/main/stream-test/src/main/resources/application.json)

![fake_stream](https://github.com/frankcl/stream/blob/main/image/fake_stream.png)

 * fake_receiver周期性生产数据并分发下游插件record_id_builder
 * record_id_builder为数据生成ID，并根据ID分发下游插件
   * 如果ID为偶数分发给processor1
   * 如果ID为奇数分发给processor2
 * processor1和processor2工作：打印数据ID
 * 组件如何引用资源请参考[RecordIDBuilder](https://github.com/frankcl/stream/blob/main/stream-test/src/main/java/xin/manong/stream/test/plugin/RecordIDBuilder.java)
   * 如果应用中相同类型资源唯一，Resource注解可以省略name参数，框架会根据资源类型进行注入
   * 资源名参数可以使用占位符${}进行填充，框架会根据占位符从插件configMap中获取真实的资源名称，然后进行注入

```java
public class RecordIDBuilder extends Plugin {

   @Resource(name = "${idBuilder}")
   private AutoIncreasedIDBuilder builder;
}
```