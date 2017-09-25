package com.minlia.cross.runner;

import static com.minlia.cross.constant.Constant.DOMAIN;

import com.minlia.cross.client.NgrokClient;
import com.minlia.cross.config.CrossProperties;
import com.minlia.cross.holder.ServerPortHolder;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

@Slf4j
public class CrossRunner implements DisposableBean, Runnable {

  @Autowired
  private CrossProperties crossProperties;



  @Autowired
  TaskExecutor taskExecutor;

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


    String subdomain=crossProperties.getSubdomain();
    if(StringUtils.isEmpty(subdomain)){
      subdomain= RandomStringUtils.randomAlphabetic(16);
    }

//    System.setProperty("https.protocols", "TLSv1.1");
     ngclient = new NgrokClient();
    //addtunnel
    ngclient.setTaskExecutor(taskExecutor);
    ngclient.addTun("127.0.0.1", localApplicationPort, "http",
         DOMAIN, subdomain, 4443, "");
//		ngclient.addTun("127.0.0.1",80,"http","","",0,"");
    //start
    taskExecutor.execute(ngclient);
//    ngclient.start();

    while (true) {
      if (ngclient.lasttime + 30 < (System.currentTimeMillis() / 1000) && ngclient.lasttime > 0) {
        log.debug("Check status with error");

        ngclient.trfalg = false;
        ngclient.stopNgrokClient();
//        ngclient.tunnelinfos.clear();//
//        try {
//          Thread.sleep(10000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
        //reconnct
        ngclient.trfalg = true;
//        ngclient.start();
        taskExecutor.execute(ngclient);
      } else {
        log.debug("Check status with OK");
      }
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
//        e.printStackTrace();
      }
    }

  }

  @Override
  public void destroy() {
    someCondition = false;
    ngclient.stopNgrokClient();
  }

}
  