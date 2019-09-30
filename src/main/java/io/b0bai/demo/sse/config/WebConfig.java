package io.b0bai.demo.sse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        //This timeout determines how long the connection is alive for. Use appropriately.
        configurer.setDefaultTimeout(Long.MAX_VALUE);
    }
}
