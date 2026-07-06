package br.com.viduink.dezoito_api_financas.dtos;

import java.util.UUID;

public record CategoriaResponse(
        UUID id, //ID da categoria
        String nome //Nome da categoria
) {
}
