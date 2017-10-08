package com.minlia.cross;

import com.minlia.cross.config.CrossProperties;
import com.minlia.cross.listener.ApplicationReadyEventListener;
import com.minlia.cross.listener.CrossEmbeddedServletContainerInitializedEventListener;
import com.minlia.cross.runner.CrossRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Created by will on 9/21/17.
 */
//@EnableAsync(mode = AdviceMode.ASPECTJ, proxyTargetClass = true)
@Configuration
@EnableConfigurationProperties(CrossProperties.class)
public class CrossAutoConfiguration {


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
