package com.consubanco.freemarker.config;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static freemarker.template.Configuration.VERSION_2_3_30;

@org.springframework.context.annotation.Configuration
public class FreemarkerConf {

    @Bean
    @Primary
    public Configuration configuration() {
        Configuration configuration = new Configuration(VERSION_2_3_30);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return configuration;
    }

    @Bean
    public TemplateHashModel templateHashModel() {
        return new BeansWrapperBuilder(Configuration.VERSION_2_3_30)
                .build()
                .getStaticModels();
    }
}
