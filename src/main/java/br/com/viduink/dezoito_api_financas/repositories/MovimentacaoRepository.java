package br.com.viduink.dezoito_api_financas.repositories;

import br.com.viduink.dezoito_api_financas.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    /* Consulta para trazer uma lista de movimentações beaseado em uma data de início e de fim
    usando linguagem JPQL (Java Persistence Query Language)
     */

    @Query("""
            SELECT m
            FROM Movimentacao m
            WHERE m.data BETWEEN :pDataInicio AND :pDataFim
            """)
    Page<Movimentacao> findByData(
            @Param("pDataInicio") LocalDate dataInicio,
            @Param("pDataFim") LocalDate dataFim,
            Pageable paginacao
    );

    //Consulta para retornar as movimentações dentro de um período de datas selecionado

    @Query("""
            SELECT m
            FROM Movimentacao m
            WHERE m.data BETWEEN :pDataInicio AND :pDataFim
            ORDER BY m.data DESC
            """)
    List<Movimentacao> findByData(
            @Param("pDataInicio") LocalDate dataInicio,
            @Param("pDataFim") LocalDate dataFim
    );
}
