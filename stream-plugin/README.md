# 通用插件实现

## 快速导航

* [进程级速度控制器](https://github.com/frankcl/stream/tree/main/stream-plugin#%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)
* [集群级速度控制器](https://github.com/frankcl/stream/tree/main/stream-plugin#%E9%9B%86%E7%BE%A4%E7%BA%A7%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)

## 集群级速度控制器
限制流程数据处理速度，配置定义示例如下

注意：依赖redisson速度限制资源，详见[链接](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E5%85%A8%E5%B1%80%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)
```json
{
  "processors": [
    {
      "name": "xxx_speed_controller",                                       //速度控制器名称
      "className": "xin.manong.stream.boost.plugin.ClusterSpeedController", //速度控制器全限定类名
      "pluginConfig": {
        "block": true,                                                      //是否阻塞等待
        "rateLimiter": "xxx"                                                //限速资源名称
      },
      "processors": {
        "next": "xxx"                                                       //下游插件名称
      }
    }
  ]
}
```