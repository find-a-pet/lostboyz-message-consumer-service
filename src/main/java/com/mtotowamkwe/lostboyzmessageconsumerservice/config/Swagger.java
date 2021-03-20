package com.mtotowamkwe.lostboyzmessageconsumerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan({"com.mtotowamkwe.lostboyzmessageconsumerservice"})
public class Swagger {

    private final Environment environment;

    public Swagger(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api/v1/consume.*"))
                .build()
                .apiInfo(new ApiInfoBuilder().description(description()).title(title()).version(version()).build());
    }

    protected String description() {
        return this.environment.getProperty("description");
    }

    protected String title() {
        return this.environment.getProperty("title");
    }

    protected String version() {
        return this.environment.getProperty("version");
    }

}