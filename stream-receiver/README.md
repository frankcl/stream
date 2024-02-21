# 通用数据接收器实现

## 快速导航
* [阿里云ONS数据接收器](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md#%E9%98%BF%E9%87%8C%E4%BA%91ons%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%99%A8)
* [阿里云OTS通道数据接收器](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md#%E9%98%BF%E9%87%8C%E4%BA%91ots%E9%80%9A%E9%81%93%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%99%A8)
* [Kafka数据接收器](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md#kafka%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%99%A8)
* [内存中转数据接收器](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md#%E5%86%85%E5%AD%98%E4%B8%AD%E8%BD%AC%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%99%A8)
* [fake数据接收器](https://github.com/frankcl/stream/blob/main/stream-receiver/README.md#fake%E6%95%B0%E6%8D%AE%E6%8E%A5%E6%94%B6%E5%99%A8)

## 阿里云ONS数据接收器
阿里云开放消息通知服务数据拉取封装，配置定义示例如下
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.ons.ONSReceiver",                //ONSReceiver全限定类名
      "converterClass": "xin.manong.stream.boost.receiver.ons.JSONMessageConverter",      //JSON消息转换器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "consumeThreadNum": 16,                                                           //消费线程数，默认为1
        "maxCachedMessageNum": 1000,                                                      //最大缓存记录数，默认1000
        "consumeId": "GID_TOPIC_XXX",                                                     //consumer id
        "serverURL": "http://onsaddr.cn-hangzhou.mq-internal.aliyuncs.com:8080",          //endpoint
        "subscribes": [                                                                   //订阅列表
          {
            "topic": "TOPIC_XXX",                                                         //订阅topic
            "tags": "*"                                                                   //订阅tags
          }
        ]
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```

## 阿里云OTS通道数据接收器
阿里云OTS数据通道拉取数据封装，配置定义示例如下
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.ots.OTSTunnelReceiver",          //OTS通道数据接收器全限定类名
      "converterClass": "xin.manong.stream.boost.receiver.ots.StreamRecordConverter",     //OTS数据转化器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "endpoint": "https://xxx.cn-hangzhou.vpc.tablestore.aliyuncs.com",                //OTS endpoint
        "instance": "xxx",                                                                //OTS实例
        "workerConfigs": [                                                                //OTS数据通道配置信息
          {
            "consumeThreadNum": 4,                                                        //拉取线程数，默认为1
            "maxConsumeDelayMs": 60000,                                                   //最大消费延迟，达到最大延迟触发报警
            "table": "table_xxx",                                                         //OTS数据表名
            "tunnel": "tunnel_xxx"                                                        //OTS数据通道名
          }
        ]
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```

## 阿里云MNS数据接收器
阿里云MNS消息服务数据拉取封装，配置定义示例如下（需引用资源MNSClient [详细参考](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91sls%E6%97%A5%E5%BF%97%E5%AE%A2%E6%88%B7%E7%AB%AF)）
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.mns.MNSReceiver",                //MNSReceiver全限定类名
      "converterClass": "xin.manong.stream.boost.receiver.mns.JSONMessageConverter",      //JSON消息转换器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "threadNum": 16,                                                                  //消费线程数，默认为1
        "queueName": "xxx",                                                               //MNS队列名称
        "clientName": "mns_client",                                                       //mns客户端名称
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```

## Kafka数据接收器
Kafka消息拉取封装，配置定义示例如下
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.kafka.KafkaReceiver",            //kafka数据接收器全限定类名
      "converterClass": "xin.manong.stream.boost.receiver.kafka.JSONMessageConverter",    //JSON数据转化器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "servers": "endpoint1,endpoint2",                                                 //kafka endpoint
        "groupId": "GID_XXX",                                                             //消费group id
        "topics": ["TOPIC_XXX"],                                                          //订阅topic列表
        "consumeThreadNum": 1,                                                            //消费线程数，默认为1
        "authConfig": {                                                                   //kafka认证配置
          "securityProtocol": "xxx",                                                      //安全协议
          "saslMechanism": "xxx",                                                         //SASL机制
          "saslJaasConfig": "xxx"                                                         //JAAS配置
        }
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```

## 内存中转数据接收器
用于应用流程内部数据中转及异步处理，例如数据需要重新执行部分逻辑可以通过该数据接收器进行数据扭转 

注意：依赖数据队列资源，详见[链接](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E5%86%85%E5%AD%98%E6%95%B0%E6%8D%AE%E9%98%9F%E5%88%97)
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.memory.MemoryReceiver",          //内存数据接收器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "recordQueue": "queue_xxx",                                                       //内存队列resource
        "threadNum": 1                                                                    //处理线程数，默认为1
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```

## fake数据接收器
不对接任何数据源，周期性生产1条空数据向下游分发，触发数据流程
```json
{
  "receivers": [
    {
      "name": "xxx_receiver",                                                             //数据接收器名称
      "receiverClass": "xin.manong.stream.boost.receiver.fake.FakeReceiver",              //fake数据接收器全限定类名
      "receiverConfigMap": {                                                              //数据接收器配置信息
        "timeIntervalMs": 1000,                                                           //数据生产周期，单位毫秒
        "threadNum": 1                                                                    //处理线程数，默认为1
      },
      "processors": ["xxx_processor"]                                                     //分发插件列表
    }
  ]
}
```
