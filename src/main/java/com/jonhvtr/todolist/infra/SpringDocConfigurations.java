package com.jonhvtr.todolist.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("TodoList API")
                .description("API Rest da aplicação TodoList, contendo funcionalidades de CRUD de Tasks e Reminders (Tarefas e Lembretes).")
                .contact(new Contact()
                        .name("João Victor (jonhvtr)")
                        .email("jonhvtr_dev@hotmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://api.todolist.com/licensa")));
    }

}
