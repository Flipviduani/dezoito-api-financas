package br.com.viduink.dezoito_api_financas;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class DezoitoApiFinancasApplication {

    public static void main(String[] args) {
        SpringApplication.run(DezoitoApiFinancasApplication.class, args);
    }

}