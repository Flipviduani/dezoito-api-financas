package br.com.viduink.dezoito_api_financas.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkerService {

    @Autowired
    private RestClient restClient;

    /*
        Metodo para ler e processar cada registro contido na fila
        Ele deverá transmitir os dados para a API do agente de IA
        @Payload -> dados gravados na fila
     */
    @RabbitListener(queues = "relatorios-movimentacoes")
    public void listener(@Payload String payload) throws Exception {

        var result = restClient.post()
                .uri("http://localhost:8084/api/relatorios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        System.out.println("\nTRANSMISSÂO REALIZADA COM SUCESSO!");
        System.out.println("Status HTTP: " + result.getStatusCode());
    }
}
