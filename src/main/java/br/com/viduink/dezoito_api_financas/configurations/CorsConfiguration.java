package br.com.viduink.dezoito_api_financas.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${cors.frontend}")
    private String corsFrontend;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        //Permissão para o projeto Angular
        registry
                .addMapping("/**") //permissão para todos os ENDPOINTS
                .allowedOrigins(corsFrontend) //projeto Angular
                .allowedMethods("POST", "PUT", "DELETE", "GET")
                .allowedHeaders("*");
    }
}