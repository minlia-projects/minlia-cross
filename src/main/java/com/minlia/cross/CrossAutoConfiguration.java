package com.minlia.cross;

import com.minlia.cross.config.CrossProperties;
import com.minlia.cross.listener.ApplicationReadyEventListener;
import com.minlia.cross.listener.CrossEmbeddedServletContainerInitializedEventListener;
import com.minlia.cross.runner.CrossRunner;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Created by will on 9/21/17.
 */
//@EnableAsync(mode = AdviceMode.ASPECTJ, proxyTargetClass = true)
@Configuration
@Slf4j
@EnableConfigurationProperties(CrossProperties.class)
public class CrossAutoConfiguration {

  public CrossAutoConfiguration(){
    initScheduledExecutorService();
  }

  private ScheduledExecutorService scheduledExecutorService;

  private Map<String, String> tokenMap = new LinkedHashMap<String, String>();
  private   Map<String, ScheduledFuture<?>> futureMap = new HashMap<String, ScheduledFuture<?>>();

  private   int poolSize = 2;

  private   boolean daemon = Boolean.TRUE;

  private   boolean initialized = Boolean.FALSE;

  private   void initScheduledExecutorService() {
    log.info("daemon:{},poolSize:{}", daemon, poolSize);
    scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        // 设置守护线程
        thread.setDaemon(daemon);
        return thread;
      }
    });
  }


  @Bean
  public CrossRunner crossRunner() {
    CrossRunner crossRunner= new CrossRunner();
    crossRunner.setScheduledExecutorService(scheduledExecutorService);
    return crossRunner;
  }

  @Bean
  @Lazy
  public ApplicationReadyEventListener appListener() {
    ApplicationReadyEventListener applicationReadyEventListener=new ApplicationReadyEventListener();
    applicationReadyEventListener.setScheduledExecutorService(scheduledExecutorService);
    return applicationReadyEventListener;
  }

  @Bean
  @Lazy
  public CrossEmbeddedServletContainerInitializedEventListener crossEmbeddedServletContainerInitializedEventListener() {
    return new CrossEmbeddedServletContainerInitializedEventListener();
  }

}
