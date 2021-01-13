#  ForestNet
Intranet penetration tool（内网穿透工具）

###  ForestNet能干什么
1. 当你需要在本地调试需要回调的程序，比如微信接口本地开发等等。**Choose it！**
2. 当你想要暴露自己本地服务到公网上时，**Choose it！**
3. 当你想学习Netty时，苦于没有上手项目，**Choose it！**
4. 当你想调试复杂的多线程项目，**Choose it！**

### ForestNet特性
- 基于JDK8 + Netty + Spring构建。
- 一共分为两个版本，单服务器多客户端版本，集群版本（集群版本还在构思中）。

### 架构图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210113185821299.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4MjU1Nzcy,size_16,color_FFFFFF,t_70)


### 架构说明
- 当启动outer server时，默认监听9091端口和8081端口。9091端口用于连接inner server。8081端口用来连接浏览器。
- 当启动inner server时，发起对outer server 9091端口的连接，当连接成功时，inner server会发出注册信息给outer server。
- outer server接收到注册信号，会验证密码是否正确，如果正确，则为inner server颁发域名，并将域名信息发送给 inner server。
- 当 outer server 和 inner server 都正确启动时，浏览器发出请求，outer server接收到请求后，要解析出host请求头，并根据host 转发到相应的 inner server中。
- 当 inner server 接收到来自 outer server 的数据时，会将数据写入一个block queue中。Inner server会单独起一个线程，执行转发 block queue 中的数据到tomcat。
- tomcat做出处理后，会顺着之前建立的一系列channel将数据返回给浏览器。
>上述的端口是默认的，在启动程序的时候可以通过参数进行修改。
 
### 快速开始
- 下载地址（outer Server）：[https://gitee.com/leek-code-god/forest-net/attach_files/583551/download/fNet-outer-server.jar](https://gitee.com/leek-code-god/forest-net/attach_files/583551/download/fNet-outer-server.jar)
- 下载地址（inner Server）：[https://gitee.com/leek-code-god/forest-net/attach_files/583552/download/fNet-inner-server.jar](https://gitee.com/leek-code-god/forest-net/attach_files/583552/download/fNet-inner-server.jar)
- 如何启动
 1. 启动outer Server。命令如下。
		`java -jar fNet-outer-server.jar -port 9091 -password 12345678 -remotePort 8081 -dnl www.a.com:8080*www.b.com:8080 -wl 100 -rl 100`
		
		参数说明
		port：			供inner Server连接的端口。
		remotePort：	供浏览器连接的端口。
		password：      inner Server连接outer Server的凭证。
		dnl:		    绑定外网服务器的域名列表，用 * 分割。如果没有绑定域名，可以直接写服务器的 ip:端口。
		wl：			outer server 写流控。（单位 byte/s）
		rl：			outer server 读流控。（单位 byte/s）
 3.  启动inner Server。命令如下。
        `java -jar fNet-inner-server.jar -osa 127.0.0.1 -osp 9091 -rsa 127.0.0.1 -rsp 8080 -pwd 12345678`
        
         参数说明
         osa： （outer Server address）outer Server的公网地址。
         osp： （outer Server port）outer Server供inner Server连接的端口。
         rsa： （real Server address）真实服务器地址。
         rsp： （real Server port）真实服务器端口。
         pwd： （password）inner Server连接outer Server的凭证。
