# fNet
Intranet penetration tool（内网穿透工具）

## 简介
- 基于netty的内外网穿透工具（V1.0版本）。
- fNet包含两部分服务，outer server 和 inner server。
- outer server需要部署在具有公网IP的服务器上。
- inner server服务需要部署在没有公网IP的设备上。

## 架构图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200718135231301.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4MjU1Nzcy,size_16,color_FFFFFF,t_70)

## 架构说明
- 当启动outer Server时，默认监听9090端口。9090端口用于连接inner server。
- 当启动inner Server时，发起对outer Server 9090端口的连接，当连接成功时，inner Server会发出注册信号给outer Server。
- outer Server接收到注册信号，会验证密码是否正确，如果正确，outer Server则会监听8080端口，用于连接浏览器，完成连接后，返回一个注册成功的信息给Inner Server。
- 当outer and inner Server正确启动时，浏览器发出请求，outer Server接收到请求，会将请求通过inner to outer的Channel，传输给inner Server。注意，需要按照指定的传输协议进行数据传输。
- 当inner Server接收到数据时，会建立一个与tomcat连接的channel（如果对应的channel已经存在，则无需重复建立与tomcat的channel，具体在开发者文档中详细描述），用于数据传输，并将数据传输给tomcat。
- tomcat做出处理后，会顺着之前建立的一系列channel将数据返回给浏览器。
>上述的端口是默认的，在启动程序的时候可以通过参数进行修改。

> 如果你想更深入的了解该项目，后序会补充开发者文档，敬请期待。

 
## 使用说明
- 下载地址（outer Server）：[https://github.com/Fengys123/fNet/releases/download/v1.0.0/fNet-outer-server.jar](https://github.com/Fengys123/fNet/releases/download/v1.0.0/fNet-outer-server.jar)
- 下载地址（inner Server）：[https://github.com/Fengys123/fNet/releases/download/v1.0.0/fNet-inner-server.jar](https://github.com/Fengys123/fNet/releases/download/v1.0.0/fNet-inner-server.jar)
- 如何启动
 1. 启动outer Server。命令如下。
		`java -jar fNet-outer-server.jar -port 9090 -password 12345678 -remotePort 8081`
		
		参数说明
		port：       供inner Server连接的端口。
		remotePort： 供浏览器连接的端口。
		password：   inner Server连接outer Server的凭证。
 3.  启动inner Server。命令如下。
        `java -jar fNet-inner-server.jar -osa 127.0.0.1 -osp 9090 -rsa 127.0.0.1 -rsp 8080 -pwd 12345678`
        
         参数说明
         osa： （outer Server address）outer Server的公网地址。
         osp： （outer Server port）outer Server供inner Server连接的端口。
         rsa： （real Server address）真实服务器地址。
         rsp： （real Server port）真实服务器端口。
         pwd： （password）inner Server连接outer Server的凭证。
## 参与贡献
 - Fork 本仓库
 - 新建 Feat_xxx 分支
 - 提交代码
 - 新建 Pull Request
