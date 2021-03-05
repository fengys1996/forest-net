#  ForestNet
Intranet penetration tool（内网穿透工具）

###  ForestNet能干什么
- 当你需要在本地调试需要回调的程序，比如微信接口本地开发等等。**Choose it！**
- 当你想要暴露自己本地服务到公网上时，**Choose it！**
- 当你想学习Netty时，苦于没有上手项目，**Choose it！**
- 当你想调试复杂的多线程项目，**Choose it！**

### ForestNet特性
- 基于JDK8 + Netty + Spring构建。
- 支持传输数据的zero copy。
- 构建了自定义协议，简单且高效。
- 对 linux 系统进行了一些针对性的优化，例如，linux kernel >= 3.9，支持so_resueport。
- 设置了高低水位线 + 流量控制 +  check channel writable befroe write，避免OOM，极大程度保证系统的安全性。
- 通过解析 Host 字段，进行用户区分，从而达到支持单服务器部署多用户使用的功能。

### 架构图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210119134050795.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4MjU1Nzcy,size_16,color_FFFFFF,t_70)



### 架构说明
- 当启动outer server时，默认监听9091端口和8081端口。9091端口用于连接inner server。8081端口用来连接浏览器。
- 当启动inner server时，发起对outer server 9091端口的连接。当连接成功时，inner server会发出注册信息（密码）给outer server。
- outer server接收到注册信息，会验证密码是否正确，如果正确，则为inner server颁发域名，并将域名信息发送给 inner server。
- 当 outer server 和 inner server 都正确启动时，浏览器发出请求，outer server接收到请求后，要解析出host请求头，并根据host 转发到相应的 inner server中。
- 当 inner server 接收到来自 outer server 的数据时，会将数据写入一个block queue中。Inner server会单独起一个线程，执行转发 block queue 中的数据到tomcat。
- tomcat做出处理后，会顺着之前建立的一系列channel将数据返回给浏览器。
>上述的端口是默认的，在启动程序的时候可以通过参数进行修改。
 
### 快速开始
- 下载地址（outer Server）：[https://gitee.com/leek-code-god/forest-net/attach_files/627412/download/fNet-outer-server.jar](https://gitee.com/leek-code-god/forest-net/attach_files/627412/download/fNet-outer-server.jar)
- 下载地址（inner Server）：[https://gitee.com/leek-code-god/forest-net/attach_files/627413/download/fNet-inner-server.jar](https://gitee.com/leek-code-god/forest-net/attach_files/627413/download/fNet-inner-server.jar)

- 本地快速搭建体验
	1. 简述：假设本地启动一个web服务，监听端口8080。我们需要通过127.0.0.1:8081端口访问该web服务。
	2.  启动 web 服务。
	3. 启动 outer server 服务。
	java -jar fNet-outer-server.jar -dnl 127.0.0.1
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210305113447421.png)
		> -dnl：表示可分配给inner server的域名列表。

	4. 启动 inner server服务
java -jar fNet-inner-server.jar
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210305113656490.png)
		> [message] Inner server register success!Domain name = [127.0.0.1:8081]
		> 这就是outer server分配给inner server的域名，我们可以通过该域名对web服务进行访问。

- 服务器搭建类似，但需要设置一些配置项，并且开放服务器中相应的端口。更多的配置项请参考[https://gitee.com/leek-code-god/forest-net/blob/master/docs/config.md](https://gitee.com/leek-code-god/forest-net/blob/master/docs/config.md)

### Forest Net展望
现在已大致完成了单服务器部署多用户使用的版本的编写。接下来的工作除了维护现有版本，还可以构思集群版本。Fighting！！！
