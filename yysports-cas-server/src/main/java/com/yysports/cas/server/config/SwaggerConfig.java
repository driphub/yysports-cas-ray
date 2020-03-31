package com.yysports.cas.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EnableSwagger2
@Configuration
@Profile("dev")
public class SwaggerConfig {
    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).apiInfo(apiInfo())
                .genericModelSubstitutes(ResponseEntity.class, CompletableFuture.class)
                .globalResponseMessage(RequestMethod.GET, globalResponseMessages())
                .globalResponseMessage(RequestMethod.POST, globalResponseMessages())
                .select().paths(PathSelectors.regex("/api/.*")).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("CAS Web REST API")
                .description("CAS Web REST API")
                .contact(new Contact("ray.huang", "", "ray.huang@pousheng.com")).version("1.0").build();
    }

    private List<ResponseMessage> globalResponseMessages() {
        List<ResponseMessage> globalResponseMessages = new ArrayList<>();
        globalResponseMessages.add(new ResponseMessageBuilder().code(401).message("连线逾时, 请重新登录!").build());
        globalResponseMessages.add(new ResponseMessageBuilder().code(403).message("你没有调用此接口的权限!").build());
        globalResponseMessages.add(new ResponseMessageBuilder().code(404).message("查无数据").build());
        globalResponseMessages.add(new ResponseMessageBuilder().code(500).message("内部系统错误!").build());

        return globalResponseMessages;
    }
}
