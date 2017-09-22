package com.minlia.cross.runner;

import static com.minlia.cross.constant.Constant.DOMAIN;

import com.minlia.cross.client.NgrokClient;
import com.minlia.cross.holder.ServerPortHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.DisposableBean;

@Slf4j
public class CrossRunner implements DisposableBean, Runnable {

//  @Autowired
//  private CrossProperties crossProperties;


  private Thread thread;
  private volatile boolean someCondition;

  public CrossRunner() {
    this.thread = new Thread(this);
  }

  NgrokClient ngclient;

  @Override
  public void run() {

    Integer port=0;

    Integer localApplicationPort=8080;

    //先从服务器上下文中取得端口

    //再从配置文件中取端口
//    port=crossProperties.getPort();
//    if(null!=port && port> 80 && port < 65535){
//      localApplicationPort=port;
//
//      log.debug("Starting on port: {} from properties",localApplicationPort);
//    }

    port= ServerPortHolder.getPort();
    if(null!=port && port> 80 && port < 65535){
      localApplicationPort=port;
      log.debug("Starting on port: {} from ServerPortHolder",localApplicationPort);
    }



//    System.setProperty("https.protocols", "TLSv1.1");
    NgrokClient ngclient = new NgrokClient();
    //addtunnel
    ngclient.addTun("127.0.0.1", localApplicationPort, "http",
        RandomStringUtils.randomAlphabetic(16).toLowerCase() + "."+DOMAIN, "", 4443, "");
//		ngclient.addTun("127.0.0.1",80,"http","","",0,"");
    //start
    ngclient.start();

  }

  @Override
  public void destroy() {
    someCondition = false;
    ngclient.stop();
  }

}
  