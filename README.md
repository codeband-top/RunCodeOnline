# 介绍

该项目是一个在线运行代码项目的服务端应用，使用Java语言开发，提供WebSocket服务和HTTP服务，支持前后端分离，因此你可以自己写前端。运行代码需要Docker环境，因此你需要安装Docker，并且开放远程连接，详见下方。

[成品效果](https://run.codetool.top/)

# 支持功能

## 在线运行代码（Websocket服务）

用户前端通过Websocket和后端服务器连接，将编程语言类型、源代码（目前仅支持单文件）传到服务器，服务器交给Docker运行，并将执行过程中实时产生的输出回显给客户端。

![](https://api.codetool.top/img/15918839477417.png)

### 保存代码（HTTP服务）

保存代码的目的是记录一段代码到数据库、并返回一个插入数据的id，客户端可以根据这个ID来生成一个页面，当访问这个页面的时候查询数据库，将保存的代码显示出来。

**该功能以RestAPI的形式实现，用Netty同时实现了一个HTTP服务器，目前仅支持持有固定密码的人员提交保存请求，尚未做用户身份鉴别相关功能**

![](https://api.codetool.top/img/15918841779849.png)

对应的，当其他用户访问这个页面，就能从数据库取出代码，并显示在网页上：

例如：

https://run.codetool.top/?id=34

![](https://api.codetool.top/img/15918843411006.png)

# 支持在线运行的编程语言

目前支持语言：

- [x] Java
- [x] C++/C（共用G++）
- [x] Golang
- [x] Python3

有望加入语言

- [ ] NodeJS
- [ ] PHP
- [ ] ... 

# 想直接用成品的请看

有意使用该项目的，我就默认你是个站长了，搭建服务端应用会比较麻烦，因此对于这部分有疑问的可以直接来联系作者或发Issue。

运行该项目建议持有两台服务器，一台用于运行Netty服务和数据库，一台用于提供Docker服务。（放到同一台上也可以）

在[下载页面](https://github.com/codeband-top/RunCodeOnline/releases)下载`release*.zip`，解压到本地。

## 建立数据库

使用根目录下的`code_segment.sql`文件在Netty服务器上创建一个数据库，用于存代码。
    
*TODO:*
有时间了把这个也搞成Docker镜像

## 开启Docker远程连接

Docker必须开启远程连接，按下面这篇博客进行操作：

https://blog.csdn.net/yaofeng_hyy/article/details/80923941

**得到的证书文件下载到本地，替换掉`certs/`下面的文件**

你还需要提前拉取用于运行代码的镜像，例如对于目前支持的编程语言：

```shell
docker pull openjdk:11
docker pull gcc:7.3
docker pull python:3
docker pull golang:1.14
```

## 修改配置文件

修改`runcodeNettyWithJava_jar/config`中的以下文件：

`user.properties`:

```properties
# 前端保存代码到数据库用于验证的密码
password = 123456
# websocket服务器的端口
ws_server_port = 7000
# http服务器的端口
api_server_port = 7001
```

`db.properties`:

```properties
driver=com.mysql.cj.jdbc.Driver

# 修改为自己的数据库连接信息
url=jdbc:mysql://localhost:3306/runcode?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
username=root
password=123456
```

`docker.properties`:

```properties
# docker连接信息
dockerHost = tcp://localhost:2375
# 不用改，我写死在Dockerfile中了
dockerCertPath = /data/certs
# 填写自己的信息（意义不大）
registryUsername = user
registryPassword = 123456
registryEmail =
```

可选修改`logback.xml`配置，用于输出日志。

将该文件夹上传到用于运行Netty服务的服务器，通过下面命令生成Docker镜像：

```shell
docker build -t runcode:1.0 .
```

通过下面命令生成Docker容器并后台开始运行：

```shell
docker run -d --name=runcode -p 7000:7000 -p 7001:7001 runcode:1.0
```

服务器必须开放7000和7001端口（如果使用反向代理可以不需要，并且强烈建议使用反向代理将这两个服务代理到80或443端口）

Nginx反向代理配置：（可选）

```conf
# 代理HTTP服务
location /
{
    proxy_pass http://localhost:7001;
    proxy_set_header Host localhost;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header REMOTE-HOST $remote_addr;

    add_header Cache-Control no-cache;
}

# 代理WebSocket服务
location /runcode {
	proxy_pass http://localhost:7000;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

尝试访问你搭建的HTTP服务（例如：http://localhost:7001/ ），若有输出，说明HTTP服务运行成功。否则可以通过`docker logs runcode`查看程序日志输出。

# API使用说明

## Websocket服务

客户端发送的数据格式：

```json
{
    "langType": "cpp/java/python3/golang",
    "content": "需要运行的源码"
}
```

服务端响应的数据：

程序的实时运行输出，纯文本

## HTTP服务

### `GET /codeSegment/{id}`

根据id获取一段保存的代码，服务端返回数据格式：

```json
{
    "success": true/false,
    "message": "提示信息",
    "data":{
        "lang": "cpp/java/python3/golang",
        "content": "保存的源码"
    }
}
```

若根据id未查到信息，则`success`为false，`data`内不包含数据。

### `POST /codeSegment`

保存一段代码，请求体格式：

```json
{
    "langType": "cpp/java/python3/golang",
    "content": "需要保存的源码",
    "password": "配置的密码"
}
```

响应格式：

```json
{
    "success": true/false,
    "message": "提示信息",
    "data":{
        "id": Number
    }
}
```

# 有意参与源码编写的请看

## 技术点

前端技术点（有点拉跨，应该会重写）：

+ CodeMirror代码编辑器
+ BootStrap
+ JQuery

后端技术点：

+ Docker
+ Netty（实现WebSocket服务器和HTTP服务器）
+ [docker-java](https://github.com/docker-java/docker-java)

## 后端项目结构

![](https://api.codetool.top/img/15919296312266.png)

