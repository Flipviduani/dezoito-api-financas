package br.com.viduink.dezoito_api_financas.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    Queue queue() {
        //Criando fila no RabbitMQ
        return new Queue("relatorios-movimentacoes");
    }
}
