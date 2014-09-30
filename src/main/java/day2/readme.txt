一个简单的 Zookeeper Watch 客户端
 
为了介绍 Zookeeper Java API 的基本用法，本文将带你如何一步一步实现一个功能简单的  Zookeeper 客户端。该 Zookeeper 客户端会监视一个你指定 Zookeeper 节点 Znode， 当被监视的节点发生变化时，客户端会启动或者停止某一程序。
 
基本要求
 
该客户端具备四个基本要求：
 •客户端所带参数： •Zookeeper 服务地址。
 •被监视的 Znode 节点名称。
 •可执行程序及其所带的参数
 
•客户端会获取被监视 Znode 节点的数据并启动你所指定的可执行程序。
 •如果被监视的 Znode 节点发生改变，客户端重新获取其内容并再次启动你所指定的可执行程序。
 •如果被监视的 Znode 节点消失，客户端会杀死可执行程序。
 
程序设计
 
一般而言，Zookeeper 应用程序分为两部分，其中一部分维护与服务器端的连接，另外一部分监视 Znode 节点的数据。在本程序中，Executor 类负责维护 Zookeeper 连接，DataMonitor 类监视 Zookeeper 目录树中的数据， 同时，Executor 包含了主线程和程序主要的执行逻辑，它负责少量的用户交互，以及与可执行程序的交互，该可执行程序接受你向它传入的参数，并且会根据被监视的 Znode 节点的状态变化停止或重启。
 
Executor类
 
Executor 对象是本例程最基本的“容器”，它包括Zookeeper 对象和DataMonitor对象。
