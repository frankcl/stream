# 通用资源实现

## 快速导航

* [阿里云ONS消息发送](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91ons%E6%B6%88%E6%81%AF%E5%8F%91%E9%80%81)
* [阿里云OTS客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91ots%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [阿里云OSS客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91oss%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [阿里云DataHub客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91datahub%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [阿里云SLS日志客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91sls%E6%97%A5%E5%BF%97%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [阿里云MNS客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E9%98%BF%E9%87%8C%E4%BA%91mns%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [kafka消息生产客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#kafka%E6%B6%88%E6%81%AF%E7%94%9F%E4%BA%A7%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [redis客户端](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#redis%E5%AE%A2%E6%88%B7%E7%AB%AF)
* [内存数据队列](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E5%86%85%E5%AD%98%E6%95%B0%E6%8D%AE%E9%98%9F%E5%88%97)
* [进程级速度控制器](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E8%BF%9B%E7%A8%8B%E7%BA%A7%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)
* [全局速度控制器](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#%E5%85%A8%E5%B1%80%E9%80%9F%E5%BA%A6%E6%8E%A7%E5%88%B6%E5%99%A8)

## 阿里云ONS消息发送
阿里云开放消息通知服务数据发送客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_producer",                                                             //消息生产客户端名称
      "className": "xin.manong.stream.boost.resource.ons.ONSProducerResource",            //消息生产客户端资源全限定类名
      "configMap": {                                                                      //消息生产客户端配置信息
        "serverURL": "http://onsaddr.cn-hangzhou.mq-internal.aliyuncs.com:8080",          //ONS endpoint
        "requestTimeoutMs": 3000,                                                         //请求超时，单位毫秒，默认3秒
        "retryCnt": 3                                                                     //发送重试次数，默认3次
      }
    }
  ]
}
```

## 阿里云OTS客户端
阿里云OTS客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_ots_client",                                                           //OTS客户端名称
      "className": "xin.manong.stream.boost.resource.ots.OTSClientResource",              //OTS客户端资源全限定类名
      "configMap": {                                                                      //OTS客户端配置信息
        "endpoint": "https://xxx.cn-hangzhou.vpc.tablestore.aliyuncs.com",                //OTS endpoint
        "instance": "xxx",                                                                //OTS实例
        "socketTimeoutMs": 5000,                                                          //socket超时，单位毫秒，默认5秒
        "connectionRequestTimeoutMs": 5000,                                               //连接请求超时，单位毫秒，默认5秒
        "connectionTimeoutMs": 5000,                                                      //连接超时，单位毫秒，默认5秒
        "retryCnt": 3                                                                     //发送重试次数，默认3次
      }
    }
  ]
}
```

## 阿里云OSS客户端
阿里云OSS客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_oss_client",                                                           //OSS客户端名称
      "className": "xin.manong.stream.boost.resource.oss.OSSClientResource",              //OSS客户端资源全限定类名
      "configMap": {                                                                      //OSS客户端配置信息
        "endpoint": "https://oss-cn-hangzhou-internal.aliyuncs.com",                      //OSS endpoint
        "socketTimeoutMs": 10000,                                                         //socket超时，单位毫秒，默认10秒
        "connectionTimeoutMs": 5000,                                                      //连接超时，单位毫秒，默认5秒
        "retryCnt": 3                                                                     //发送重试次数，默认3次
      }
    }
  ]
}
```

## 阿里云DataHub客户端
阿里云DataHub客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_data_hub_client",                                                      //DataHub客户端名称
      "className": "xin.manong.stream.boost.resource.datahub.DataHubClientResource",      //DataHub客户端资源全限定类名
      "configMap": {                                                                      //DataHub客户端配置信息
        "endpoint": "http://dh-cn-hangzhou-int-vpc.aliyuncs.com",                         //DataHub endpoint
        "retryCnt": 3                                                                     //发送重试次数，默认3次
      }
    }
  ]
}
```

## 阿里云MNS客户端
阿里云MNS客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_mns_client",                                                           //MNS客户端名称
      "className": "xin.manong.stream.boost.resource.mns.MNSClientResource",              //MNS客户端资源全限定类名
      "configMap": {                                                                      //MNS客户端配置信息
        "endpoint": "http://<yourAccountId>.mns.cn-hangzhou.aliyuncs.com",                //MNS endpoint
        "maxConnections": 200,                                                            //最大连接数，默认200
        "maxConnectionsPerRoute": 200,                                                    //每个路由最大连接数，默认200
        "socketTimeoutMs": 20000,                                                         //socket超时（单位：毫秒），默认20秒
        "connectTimeoutMs": 20000                                                         //连接超时（单位：毫秒），默认20秒
      }
    }
  ]
}
```

## 阿里云SLS日志客户端
阿里云SLS日志客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_log_client",                                                           //log客户端名称
      "className": "xin.manong.stream.boost.resource.log.LogClientResource",              //log客户端资源全限定类名
      "configMap": {                                                                      //log客户端配置信息
        "endpoint": "http://cn-hangzhou.log.aliyuncs.com"                                 //log client endpoint
      }
    }
  ]
}
```

## kafka消息生产客户端
kafka消息生产客户端封装，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_producer",                                                             //kafka消息生产客户端名称
      "className": "xin.manong.stream.boost.resource.kafka.KafkaProducerResource",        //kafka消息生产客户端资源全限定类名
      "configMap": {                                                                      //kafka消息生产客户端配置信息
        "servers": "xxx,yyy,zzz",                                                         //kafka endpoint
        "requestTimeoutMs": 3000,                                                         //请求超时，单位毫秒，默认3秒
        "retryCnt": 3,                                                                    //发送重试次数，默认3次
        "authConfig": {                                                                   //kafka认证配置
          "securityProtocol": "xxx",                                                      //安全协议
          "saslMechanism": "xxx",                                                         //SASL机制
          "saslJaasConfig": "xxx"                                                         //JAAS配置
        }
      }
    }
  ]
}
```

## redis客户端
基于redisson的redis客户端封装，支持3中模式
 - [x] SINGLE：单点模式
 - [x] CLUSTER：集群模式
 - [x] MASTER_SLAVE：主从模式

配置定义示例如下
```json
{
  "resources": [
    {
      "name": "single_redis_client",                                                      //redis客户端名称
      "className": "xin.manong.stream.boost.resource.redis.RedisClientResource",          //redis客户端资源全限定类名
      "configMap": {                                                                      //redis客户端配置信息
        "mode": "SINGLE",                                                                 //SINGLE模式
        "address": "xxx",                                                                 //redis服务器地址
        "db": 0,                                                                          //redis数据库，默认0
        "password": "xxx",                                                                //密码，默认为空
        "connectionPoolSize": 64,                                                         //连接池大小，默认64
        "timeout": 3000                                                                   //连接超时，单位毫秒，默认3秒
      }
    },
    {
      "name": "cluster_redis_client",                                                     //redis客户端名称
      "className": "xin.manong.stream.boost.resource.redis.RedisClientResource",          //redis客户端资源全限定类名
      "configMap": {                                                                      //redis客户端配置信息
        "mode": "CLUSTER",                                                                //CLUSTER模式
        "nodeAddresses": ["xxx"],                                                         //redis服务器地址列表
        "password": "xxx",                                                                //密码，默认为空
        "connectionPoolSize": 64,                                                         //连接池大小，默认64
        "timeout": 3000                                                                   //连接超时，单位毫秒，默认3秒
      }
    },
    {
      "name": "master_slave_redis_client",                                                //redis客户端名称
      "className": "xin.manong.stream.boost.resource.redis.RedisClientResource",          //redis客户端资源全限定类名
      "configMap": {                                                                      //redis客户端配置信息
        "mode": "MASTER_SLAVE",                                                           //MASTER_SLAVE模式
        "masterAddress": "xxx",                                                           //master服务器地址
        "slaveAddresses": ["xxx"],                                                        //slave服务器地址列表
        "password": "xxx",                                                                //密码，默认为空
        "db": 0,                                                                          //redis数据库，默认0
        "connectionPoolSize": 64,                                                         //连接池大小，默认64
        "timeout": 3000                                                                   //连接超时，单位毫秒，默认3秒
      }
    }
  ]
}
```

## 内存数据队列
内存KVRecord数据队列封装，用于缓存中转数据，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_record_queue",                                                         //数据队列名称
      "className": "xin.manong.stream.boost.resource.common.RecordQueueResource",         //数据队列资源全限定类名
      "configMap": {                                                                      //数据队列配置信息
        "queueSize": 1000                                                                 //队列大小
      }
    }
  ]
}
```

## 全局速度控制器
控制应用级别数据处理速率，配置定义示例如下

注意：依赖redis客户端资源，详见[链接](https://github.com/frankcl/stream/blob/main/stream-resource/README.md#redis%E5%AE%A2%E6%88%B7%E7%AB%AF)
```json
{
  "resources": [
    {
      "name": "xxx_rate_limiter",                                                         //限速器名称
      "className": "xin.manong.stream.boost.resource.common.RateLimiterResource",         //限速器资源全限定类名
      "configMap": {                                                                      //数据队列配置信息
        "redisClient": "xxx",                                                             //redis客户端资源名称
        "rateLimiterKey": "xxx",                                                          //redis限速器key
        "timeSpanSeconds": 60,                                                            //时间跨度，单位秒
        "permits": 10                                                                     //限制数量
      }
    }
  ]
}
```