package br.com.viduink.dezoito_api_financas.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String mensagem,
        LocalDateTime timestamp
) {
}
