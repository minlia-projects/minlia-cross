### 更新日志

1.0.6.RELEASE
添加可以自定义二级域名的功能

1.0.5.RELEASE
修复在客户端调试的情况下服务端发送至客户端请求由于超时的服务端强制关闭连接的问题.



### Minlia Cross 使用NGROK提供外部域名访问

### 使用方式,已发布到MAVEN中央库, 在当前项目中添加如下依赖即可

```
    <dependency>
      <groupId>com.minlia.cross</groupId>
      <artifactId>minlia-cross</artifactId>
      <version>1.0.5.RELEASE</version>
    </dependency>
```

### 演示项目(基于spring boot 官方项目 https://start.spring.io/starter.zip )
 
git clone https://github.com/minlia-projects/minlia-cross

cd docs/demo
mvn spring-boot:run




### 运行效果

![Startup](https://raw.githubusercontent.com/minlia-projects/minlia-cross/master/docs/images/startup.jpg)

![Result in Browser](https://raw.githubusercontent.com/minlia-projects/minlia-cross/master/docs/images/result.jpg)

### 特别感谢

ngrok为我们提供这么好用的软件, 大大节省外网服务器的部署成本

dosgo提供了java版本的底层实现


