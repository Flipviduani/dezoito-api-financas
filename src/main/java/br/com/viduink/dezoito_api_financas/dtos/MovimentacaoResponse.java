package br.com.viduink.dezoito_api_financas.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoResponse(
        UUID id,
        String nome,
        LocalDate data,
        Double valor,
        String tipo,
        CategoriaResponse categoria
) {
}
