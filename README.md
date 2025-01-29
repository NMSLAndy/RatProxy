
# Rat Proxy


Rat Proxy是一个由Java编写的为Minecraft服务器搭建加速ip的软件

## Features

- 多版本支持(1.7.2+)
- 比较容易上手
- 多平台支持
- 自定义Motd, 服务器图标等
- dev不玩原神(jk

## How 2 use?

0. 请确认已经安装 Java 21

1. 新建文件夹后将核心 (.jar) 文件丢进去然后进入文件夹

2. 新建脚本文件

- Windows

    新建一个 `start.bat` 后写入：
    
    ```bash
    java -jar 核心名字.jar
    ```

- Linux/MacOS

    新建一个 `start.sh` 后写入：
    
    ```bash
    #!/bin/bash
    java -jar 核心名字.jar
    ```

    然后，在这个文件夹打开终端输入：

    ```bash
    chmod +x start.sh
    ```

3. 先运行一次, 修改配置文件后重新运行即可

## Configuration

*  Config.json
```json
{
  "srcPort": 25565, //监听端口
  "targetHost": "hypixel.net", //目标IP
  "targetPort": 25565, //目标端口(通常是25565)
  "rewriteHost": true, //是否启用IP覆写(用于绕过Hypixel等服务器的IP检查)
  "rewrittenHost": "mc.hypixel.net", //覆写IP
  "rewrittenPort": 25565, //覆写端口
  "maxPlayers": 10, //连接这条加速ip的最大人数
  "whiteListEnabled": false, //是否启用白名单(还没写)
  "motd": "server description - 服务器描述", //服务器的motd
  "list": [  //下面的都是Player List中显示的信息
    "尼好, 这里是老鼠代理",
    "Hi, this is §cR§6a§et §aP§br§9o§dx§5y§r."
  ]
}
```

*  自定义Fav Icon
    
    将符合要求的图标放到Rat Proxy目录中的FavIcons文件夹
    *  图标要求
        1. 分辨率为64x64
        2. 格式为PNG (可以在这里转换: https://convertio.co/)
        3. 体积最好别太大
    不放入文件将使用默认的FavIcon, 放入多个文件将随机显示

## Authors

- [@An_Andy_dy](https://space.bilibili.com/315911809)

