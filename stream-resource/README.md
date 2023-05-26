# 通用资源

## 快速导航

* 阿里云ONS消息发送
* 阿里云OTS客户端
* 阿里云OSS客户端
* kafka消息生产客户端
* redis客户端
* 内存数据队列
* 进程级速度控制器
* 全局速度控制器

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

## 进程级速度控制器
控制进程级别数据处理速率，配置定义示例如下
```json
{
  "resources": [
    {
      "name": "xxx_rate_limiter",                                                         //限速器名称
      "className": "xin.manong.stream.boost.resource.common.RateLimiterResource",         //限速器资源全限定类名
      "configMap": {                                                                      //数据队列配置信息
        "permitsPerSecond": 10                                                            //限制最大速率，单位：条/秒
      }
    }
  ]
}
```

## 全局速度控制器
控制应用级别数据处理速率，配置定义示例如下

注意：依赖redis客户端资源，详见
```json
{
  "resources": [
    {
      "name": "xxx_rate_limiter",                                                         //限速器名称
      "className": "xin.manong.stream.boost.resource.common.RRateLimiterResource",        //限速器资源全限定类名
      "configMap": {                                                                      //数据队列配置信息
        "redisClient": "xxx",                                                             //redis客户端资源名称
        "rateLimiterKey": "xxx",                                                          //redis限速器key
        "permitsPerSecond": 10                                                            //限制最大速率，单位：条/秒
      }
    }
  ]
}
```