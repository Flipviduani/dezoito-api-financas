package br.com.viduink.dezoito_api_financas.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {

    //Metodo para ler e processar cada registro contido na fila
    //Ele deve transmitir os dados para a API do agente de IA
    //@Payload -> dados gravados na fila
    @RabbitListener(queues = "relatorios-movimentacoes")
    public void listener(@Payload String payload) throws Exception {
        //TODO Enviar os dados da mensageria para a API do agente de IA
        System.out.println("\n\n**********");
        System.out.println("\n\nDADOS TRANSMITIDOS COM SUCESSO!");
        System.out.println("\n\nPAYLOAD: " + payload);
        System.out.println("\n\n**********");

    }
}
