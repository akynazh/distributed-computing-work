# 分布式计算

西电分布式计算课程作业及实验代码参考。

## cs_thread_pool

将基于 TCP 协议的 Client-Server 通信程序示例的服务器端程序改造成线程池版。

## library

利用 RPC 技术实现一个基于 C/S（客户端+服务器端）架构的书籍信息管理系统，具体要求：

客户端实现用户交互，服务器端实现书籍信息存储和管理。客户端与服务器端利用 RPC 机制进行协作。中间件任选。

服务器端至少暴露如下RPC接口：

bool add(Book b)   添加一个书籍对象。

Book queryByID(int bookID) 查询指定ID号的书籍对象。

BookList queryByName(String name) 按书名查询符合条件的书籍对象列表，支持模糊查询。

bool delete(int bookID) 删除指定ID号的书籍对象。

## random_generator

利用基于消息队列中间件的通信技术实现一个分布式随机信号分析系统，具体要求：

1. 随机信号产生器节点每隔100毫秒左右就产生一个正态分布的随机数字，并作为一个消息发布。

2. 随机信号统计分析节点对信号进行如下分析：

   （1）计算过去N个随机信号的均值和方差（N为常量，可设置）；

   （2）计算所有历史数据中的最大值和最小值；

   （3）定时地将分析结果打包成一个新消息并通过MOM发布出去。

3. 实时数据显示节点实现如下功能：

   （1）实时绘制过去一段时间内随机信号的折线图；

   （2）实时显示随机信号统计分析结果。

4. 消息中间件任选。

## forum

实现一个分布式论坛系统，包含多个副本论坛服务器。具体要求为：

1. 客户端软件可以选择连接任意的副本论坛服务器：发帖子（Post）、读帖子、针对某个帖子进行回应（Reply）。
2. 客户端软件显示论坛中的发帖和回应信息时要符合因果关系。
3. 假设模型：点对点拓扑；可靠FIFO链路；节点不会失效。

## hadoop_spark

学习基于 MapReduce 框架的分布式计算程序设计方法。

p1: 输入文件为学生成绩信息，包含了必修课与选修课成绩，每行格式：班级1, 姓名1, 科目1, 必修, 成绩1

编写两个 Hadoop 平台上的 MapReduce 程序，分别实现如下功能：

1. 计算每个学生必修课的平均成绩。
2. 按科目统计每个班的平均成绩。

p2: 输入文件的每一行为具有父子/父女/母子/母女/关系的一对人名，例如：Tim, Andy，假定不会出现重名现象。

编写 Hadoop 平台上的 MapReduce 程序，找出所有具有 grandchild-grandparent 关系的人名组。
