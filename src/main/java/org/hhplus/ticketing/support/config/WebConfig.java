package org.hhplus.ticketing.support.config;

import org.hhplus.ticketing.support.filter.LoggingFilter;
import org.hhplus.ticketing.support.interceptor.TokenValidationInterceptor;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private TokenValidationInterceptor tokenValidationInterceptor;

    public WebConfig(TokenValidationInterceptor tokenValidationInterceptor) {
        this.tokenValidationInterceptor = tokenValidationInterceptor;
    }

    @Bean
    public HttpMessageConverters customConverters() {
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return new HttpMessageConverters(false, Collections.singletonList(jacksonMessageConverter));
    }

    // filter
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 토큰 검증 인터셉터
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns("/api/**")                 // 인터셉터 적용할 경로
                .excludePathPatterns("/api/queues/**")      // 제외할 경로 (토큰발급/조회)
                .excludePathPatterns("/api/users/**");      // 제외할 경로 (잔액충전/조회)
    }
}