package com.buberdinner.dinnerservice.domain.event.EventListner;

import com.buberdinner.dinnerservice.domain.entity.Dinner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Bean Spring qui récupère le ApplicationEventPublisher
 * et le transmet à l'entité Dinner via la méthode statique setPublisher(...).
 */
@Component
public class DinnerEntityListener implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Dinner.setEventPublisher(ctx);
    }
}
