package com.minlia.cross.listener;

import com.minlia.cross.runner.CrossRunner;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by will on 9/21/17.
 */
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

  @Autowired
  CrossRunner crossRunner;

  @Autowired
  Executor executor;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    executor.execute(crossRunner);
  }
}
