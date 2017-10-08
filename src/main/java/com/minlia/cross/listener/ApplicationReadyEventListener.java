package com.minlia.cross.listener;

import com.minlia.cross.runner.CrossRunner;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by will on 9/21/17.
 */
@Slf4j
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

  @Autowired
  CrossRunner crossRunner;

//  @Autowired
//  Executor executor;


  private ScheduledExecutorService scheduledExecutorService;

  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  public void setScheduledExecutorService(
      ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
  }



  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {

    scheduledExecutorService.execute(crossRunner);
  }
}
