# 通用插件实现

## 快速导航

* [速度控制器](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91ons%E6%B6%88%E6%81%AF%E5%8F%91%E9%80%81)

## 速度控制器
限制流程数据处理速度，配置定义示例如下

注意：依赖速度限制资源，详见[链接](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E8%BF%9B%E7%A8%8B%E7%BA%A7%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)
```json
{
  "processors": [
    {
      "name": "xxx_speed_controller",                                       //速度控制器名称
      "className": "xin.manong.stream.boost.plugin.SpeedController",        //速度控制器全限定类名
      "pluginConfig": {
        "rateLimiter": "xxx"                                                //限速资源名称
      },
      "processors": {
        "next": "xxx"                                                       //下游插件名称
      }
    }
  ]
}
```