package kr.cseungjoo.chome_be.shared.adapter.web.config;

import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/v1", c ->
                c.isAnnotationPresent(ApiV1.class)
        );
    }
}
