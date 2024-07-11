package org.hhplus.ticketing.config;

import org.hhplus.ticketing.application.queue.facade.QueueFacade;
import org.hhplus.ticketing.aspect.TokenValidationAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public TokenValidationAspect tokenValidationAspect(QueueFacade queueFacade) {
        return new TokenValidationAspect(queueFacade);
    }
}
