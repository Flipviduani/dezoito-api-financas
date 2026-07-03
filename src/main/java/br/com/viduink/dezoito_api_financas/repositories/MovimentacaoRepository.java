package br.com.viduink.dezoito_api_financas.repositories;

import br.com.viduink.dezoito_api_financas.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
}
