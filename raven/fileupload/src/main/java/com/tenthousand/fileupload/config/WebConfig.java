package com.tenthousand.fileupload.config;

import com.tenthousand.fileupload.consts.Consts;
import com.tenthousand.fileupload.utils.SystemUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/html/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Consts.mapping + "/**").addResourceLocations("file:" + SystemUtil.getRootPath());
//        super.addResourceHandlers(registry);
    }
}
