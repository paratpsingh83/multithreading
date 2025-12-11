//package com.jobportal.config;
//
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////
////@Configuration
////public class OpenApiConfig {
////
////    @Bean
////    public OpenAPI customOpenAPI() {
////        return new OpenAPI()
////                .info(new Info()
////                        .title("Travel BnB API Documentation")
////                        .version("1.0")
////                        .description("API Documentation with JWT Authentication"))
////                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
////                .components(new io.swagger.v3.oas.models.Components()
////                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
////                                .type(SecurityScheme.Type.HTTP)
////                                .scheme("bearer")
////                                .bearerFormat("JWT")));
////    }
////
////    @Bean
////    public GroupedOpenApi publicApi() {
////        return GroupedOpenApi.builder()
////                .group("public")
////                .pathsToMatch("/**")  // Match all paths
////                .build();
////    }
////}
//
//
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////@Configuration
////public class OpenApiConfig {
////    @Bean
////    public OpenAPI customOpenAPI() {
////        return new OpenAPI()
////                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
////                .components(new Components().addSecuritySchemes(
////                        "bearerAuth",
////                        new SecurityScheme()
////                                .name("bearerAuth")
////                                .type(SecurityScheme.Type.HTTP)
////                                .scheme("bearer")
////                                .bearerFormat("JWT")
////                ));
////    }
////}
//
//
//
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                .components(new Components().addSecuritySchemes(
//                        "bearerAuth",
//                        new SecurityScheme()
//                                .name("bearerAuth")
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")
//                ));
//    }
//}




