# smartrpc project
smartrpc是基于netty构建的rpc框架

----------
## 前言

写这个小项目的目的，一是检验平时的积累，二是学点新东西。从准备写到开始写，中间看了很多类似的开源代码，其中最最最大牌的就是dubbo了，借鉴项目的架构，以及一些具体实现的方案，收获颇多，我想，代码能力的增长，要多看，多写，在实际中更加深刻的理解一些技术。

----------

## smartrpc项目架构
![rpc-architure-detail.png](image%2Frpc-architure-detail.png)
什么是rpc？A系统调用B系统的方法，像是调本系统的一样。服务提供方把启动后，把接口注册到注册中心，包含服务地址，开放的方法。服务消费方启动后把要调用的服务，从注册中心找到后，动态代理。
----------


## 技术要点
* 单例模式:double check和静态内部类
* guava，线程池
* spi，拓展机制
* netty，通信基础
* spring boot:自动配置
* ......

----------
## 使用说明
1、定义公共接口
