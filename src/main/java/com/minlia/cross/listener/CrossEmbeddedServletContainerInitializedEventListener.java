package com.minlia.cross.listener;

import com.minlia.cross.holder.ServerPortHolder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CrossEmbeddedServletContainerInitializedEventListener implements
    ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        ServerPortHolder.setPort(event.getSource().getPort());
    }
}
