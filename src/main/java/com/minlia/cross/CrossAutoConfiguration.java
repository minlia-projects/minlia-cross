package com.minlia.cross;

import com.minlia.cross.listener.ApplicationReadyEventListener;
import com.minlia.cross.listener.CrossEmbeddedServletContainerInitializedEventListener;
import com.minlia.cross.runner.CrossRunner;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by will on 9/21/17.
 */
//@EnableAspectJAutoProxy
//@EnableAsync(mode = AdviceMode.ASPECTJ, proxyTargetClass = true)
@Configuration
//@EnableConfigurationProperties(value = {CrossProperties.class })
public class CrossAutoConfiguration {

  @Bean
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("Minlia-Cross-X-");
    executor.initialize();
    return executor;
  }


  @Bean
  public CrossRunner crossRunner() {
    return new CrossRunner();
  }

  @Bean
  @Lazy
  public ApplicationReadyEventListener appListener() {
    return new ApplicationReadyEventListener();
  }

  @Bean
  @Lazy
  public CrossEmbeddedServletContainerInitializedEventListener crossEmbeddedServletContainerInitializedEventListener() {
    return new CrossEmbeddedServletContainerInitializedEventListener();
  }

}
