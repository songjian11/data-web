package com.cs.mobile.api.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 * @author wells.wong
 * @date 2018年11月18日
 */
@Configuration
public class ApiInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public ApiTimeInterceptor getApiTimeInterceptor() {
        return new ApiTimeInterceptor();
    }

    @Bean
    public ApiTokenInterceptor getApiTokenInterceptor() {
        return new ApiTokenInterceptor();
    }

    @Bean
    public ApiIpInterceptor getApiIpInterceptor() {
        return new ApiIpInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getApiTimeInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(getApiTokenInterceptor()).addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/info").excludePathPatterns("/api/weixin/**")
                .excludePathPatterns("/api/qywx/**")
                .excludePathPatterns("/api/goods/**").excludePathPatterns("/api/towerTeam/**")
                .excludePathPatterns("/api/ds/**").excludePathPatterns("/api/bonusPool/createData")
                .excludePathPatterns("/api/po/doPoAsnDetail").excludePathPatterns("/api/test/isok");
        registry.addInterceptor(getApiIpInterceptor()).addPathPatterns("/api/weixin/sendMsg");
    }

}
