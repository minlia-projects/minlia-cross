package com.minlia.cross.runner;

import static com.minlia.cross.constant.Constant.DOMAIN;

import com.minlia.cross.client.NgrokClient;
import com.minlia.cross.config.CrossProperties;
import com.minlia.cross.holder.ServerPortHolder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CrossRunner implements DisposableBean, Runnable {

  @Autowired
  private CrossProperties crossProperties;



  private ScheduledExecutorService scheduledExecutorService;

  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  public void setScheduledExecutorService(
      ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
  }

  private Thread thread;
  private volatile boolean someCondition;

  public CrossRunner() {
    this.thread = new Thread(this);
  }

  NgrokClient ngclient;

  @Override
  public void run() {


    Integer localPort=8080;

    Integer port= ServerPortHolder.getPort();
    if(null!=port && 0!=port && port> 80 && port < 65535){
      localPort=port;
      log.debug("Starting on port: {} from ServerPortHolder",localPort);
    }


    String subdomain=crossProperties.getSubdomain();
    if(StringUtils.isEmpty(subdomain)){
      subdomain= RandomStringUtils.randomAlphabetic(16);
    }


    String remoteServer=crossProperties.getRemoteServer();
    if(StringUtils.isEmpty(remoteServer)){
      remoteServer= DOMAIN;
    }

    Integer remotePort=crossProperties.getRemotePort();
    if(null ==remotePort){
      remotePort= 4443;
    }


    String localhost=crossProperties.getLocalhost();
    if(StringUtils.isEmpty(localhost)){
      localhost= "127.0.0.1";
    }


    Integer tmpPort =crossProperties.getLocalPort();
    if(null != tmpPort){
      localPort=tmpPort;
    }


//    System.setProperty("https.protocols", "TLSv1.1");
     ngclient = new NgrokClient();
    //addtunnel
    ngclient.setTaskExecutor(scheduledExecutorService);
    ngclient.addTun(localhost, localPort, "http",
        remoteServer, subdomain, remotePort, "");
//		ngclient.addTun("127.0.0.1",80,"http","","",0,"");
    //start
    scheduledExecutorService.execute(ngclient);
//    taskExecutor.execute(ngclient);
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
        scheduledExecutorService.execute(ngclient);
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
  