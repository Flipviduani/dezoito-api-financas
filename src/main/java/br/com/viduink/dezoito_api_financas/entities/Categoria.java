package br.com.viduink.dezoito_api_financas.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    //O mappedBy define o nome do atributo na classe Movimentacao que referencia a categoria.
    //Isso indica que a relação é bidirecional e que a tabela de movimentacoes possui uma
    //chave estrangeira para a tabela de categorias.

    @OneToMany(mappedBy = "categoria")
    private List<Movimentacao> movimentacoes;
}