# DistributedCrawler
字节青训营大数据项目-项目四 分布式爬虫系统   

本项目为基于流式计算框架flink开发的分布式爬虫系统，能够实现多线程并发爬取京东网站上商品信息，将爬取到的网页信息清洗解析并存储到数据库、且能够对数据进行检索的完整的爬虫功能。
### 1. 项目部署

#### 1.1 环境配置

```
macOS 15.3
Java：JDK 1.8.0 
大数据框架：
Flink：1.13.0（POM 配置中定义的版本） 
Scala：2.12（Flink 依赖使用的版本） 
数据库及相关工具：
MySQL：8.0.X 连接驱动：mysql-connector-java 8.0.28 
数据库连接池： commons-dbcp 1.4 
commons-pool 1.6 
commons-dbutils 1.6 
日志管理：
slf4j-api 1.7.10 
slf4j-log4j12 1.7.10 
Zookeeper 相关：
Zookeeper：3.4.6 
Curator Framework：2.7.1 
定时任务调度：
Quartz：1.8.4 
Web 及 HTTP 相关：
HTML 解析： htmlcleaner 2.10 
jsoup 1.10.2 
HTTP 客户端： httpclient 4.5.13 
大数据存储：
HBase 相关： hbase-hadoop2-compat 1.1.5 
hbase-client 1.1.5 
hbase-common 1.1.5 
hbase-server 1.1.5 
单元测试：
JUnit：4.12 

```

#### 1.2 启动步骤

1. 根据当前电脑Hbase和mysql配置
2. 终端启动mysql
3. 根据\scripts\db.sql建立相应的mysql数据库和表。
4. 运行FlinkSpider.java，运行成功后结果如下图所示

```
-----flink url    https://list.jd.com/list.html?cat=9987,653,655&page=1
2025-03-05 10:02:58,846 [Flat Map -> Sink: Unnamed (1/4)#0] [org.apache.flink.runtime.state.heap.HeapKeyedStateBackendBuilder] [INFO] - Finished to build heap keyed state-backend.
2025-03-05 10:02:58,846 [Flat Map -> Sink: Unnamed (3/4)#0] [org.apache.flink.runtime.state.heap.HeapKeyedStateBackendBuilder] [INFO] - Finished to build heap keyed state-backend.
2025-03-05 10:02:58,846 [Flat Map -> Sink: Unnamed (3/4)#0] [org.apache.flink.runtime.state.heap.HeapKeyedStateBackend] [INFO] - Initializing heap keyed state backend with stream factory.
2025-03-05 10:02:58,846 [Flat Map -> Sink: Unnamed (1/4)#0] [org.apache.flink.runtime.state.heap.HeapKeyedStateBackend] [INFO] - Initializing heap keyed state backend with stream factory.
2025-03-05 10:02:58,847 [Flat Map -> Sink: Unnamed (3/4)#0] [org.apache.flink.runtime.taskmanager.Task] [INFO] - Flat Map -> Sink: Unnamed (3/4)#0 (4a1fbd9ad8f45bfed93535a5df30abf4) switched from INITIALIZING to RUNNING.
2025-03-05 10:02:58,847 [Flat Map -> Sink: Unnamed (1/4)#0] [org.apache.flink.runtime.taskmanager.Task] [INFO] - Flat Map -> Sink: Unnamed (1/4)#0 (f21ac67095db2f5da232b96de051fb1f) switched from INITIALIZING to RUNNING.
2025-03-05 10:02:58,847 [flink-akka.actor.default-dispatcher-5] [org.apache.flink.runtime.executiongraph.ExecutionGraph] [INFO] - Flat Map -> Sink: Unnamed (3/4) (4a1fbd9ad8f45bfed93535a5df30abf4) switched from INITIALIZING to RUNNING.
2025-03-05 10:02:58,847 [flink-akka.actor.default-dispatcher-5] [org.apache.flink.runtime.executiongraph.ExecutionGraph] [INFO] - Flat Map -> Sink: Unnamed (1/4) (f21ac67095db2f5da232b96de051fb1f) switched from INITIALIZING to RUNNING.
```
5. 运行Query.java,输入查询条目和关键词，据此构建 SQL 查询语句，统计符合条件的记录数，最后根据用户选择的显示条目，执行预编译 SQL 查询并输出结果。



- 代理ip使用说明

在\resources\IPProxyRepository.txt中添加代理ip地址，格式如下：

```
47.106.105.236:80
222.66.202.6:80
122.226.57.70:8888
...
```

- 数据检索模块使用说明

运行Query.java，按照提示进行操作。





