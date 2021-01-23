## 详细配置

### outer server 配置：
你可以输入 java -jar fNet-outer-server.jar -h 查看可配置的选项。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ce2f125a655341b6a1727aee57412c94~tplv-k3u1fbpfcp-watermark.image)

现在来分别说明一下，每一个配置项的作用。

- -h：获取帮助。

- -dnl：domain name list 的缩写。表示当前公网服务器（outer server）绑定的域名列表。如果配置多个域名，则需要使用分隔符 “*” 进行分割。
	
    For example：假设我的公网服务器绑定了两个域名 “a.forest.name” 和 “b.forest.name”。配置为 `-dnl a.name.forest*b.name.forest`。如果我的公网服务器没绑定域名，怎么使用呢？ 可以直接配置你的公网服务器的公网ip。 配置为 `-dnl myip`。

	请注意：如果不配置该选项，项目则无法正常启动。具体说明：当一个 client 接入时，outer server 会分配其一个域名。也就是说，一个用户一个域名，配置几个域名，就支持几个用户。
    
- -osp_b：outer server port for monitor browser 的缩写。表示 outer server 监听的一个端口，该端口用来接收来自浏览器的请求。默认监听8081端口。For Example，`-osp_b 8081`;

- -osp_i：outer server port for inner server 的缩写。表示 outer server 监听的一个端口，该端口用来接收来自 inner server 的请求。默认监听9091端口。For Examle,`-osp_i 9091`;

- -pwd：表示客户端连接需要的密码。默认是12345678。For Example，`-pwd 12345678`。

- -srp：enable so_resueport 的缩写。如果要开启的话，那么你的环境必须是 linux，并且内核版本 >= 3.9。“1”表示开启，非“1”表示关闭。默认关闭。For Example，`-srp 0`。

- trl：total read limit 的缩写。表示 outer server 最大承受的读流量。单位是 byte/s。默认为0，表示不开启。For Example，`-trl 1024`。

- twl：total write limit 的缩写。表示 outer server 最大承受的写流量。单位是 byte/s。默认为0，表示不开启。For Example，`-twl 1024`。


### inner server 配置：
你可以输入 java -jar fNet-inner-server.jar -h 查看可配置的选项。

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/23921b8970bc445d927f9bd9f7d4e7b7~tplv-k3u1fbpfcp-watermark.image)

现在来分别说明一下，每一个配置项的作用。

- -h：获取帮助。

- -osa：outer server address 的缩写。表示要连接的 outer server 所在公网服务器的 ip。默认为 “127.0.0.1”，使用的时候需要根据自己的公网服务器ip进行设置。For Example，`-osa 47.**.**.**`。

- -osp_i：outer server port for inner server 的缩写。表示所要连接的 outer server 监听的端口（与上面 outer server 设置中的 osp_i 保持一致）。默认为9091。For Example，`-osp_i 9091`。

- -pwd：表示客户端连接需要的密码。默认是12345678。For Example，`-pwd 12345678`。

- -rsa：real server address 的缩写。默认为127.0.0.1。一般情况下，inner server 和 real server（比如 tomcat）会部署到同一台机器，无需设置。如果部署到不同的机器上，则根据自己机器的情况进行设置。For Example，`-rsa 127.0.0.1`。

- -rsp：real server port 的缩写。real server（比如 tomcat） 监听的端口，默认8080。For Example，`-rsp 8080`。


### On the end
- 如果大家有什么疑问，欢迎提 issue。
- 其实，配置这一块还有很多形式可以去做，比如说，以配置文件的方式等等，欢迎大家参与项目。
- Thank you！！！
