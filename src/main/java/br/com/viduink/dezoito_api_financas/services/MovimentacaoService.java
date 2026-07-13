package br.com.viduink.dezoito_api_financas.services;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaResponse;
import br.com.viduink.dezoito_api_financas.dtos.MovimentacaoRequest;
import br.com.viduink.dezoito_api_financas.dtos.MovimentacaoResponse;
import br.com.viduink.dezoito_api_financas.entities.Categoria;
import br.com.viduink.dezoito_api_financas.entities.Movimentacao;
import br.com.viduink.dezoito_api_financas.enums.TipoMovimentacao;
import br.com.viduink.dezoito_api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.viduink.dezoito_api_financas.exceptions.ValidacaoException;
import br.com.viduink.dezoito_api_financas.repositories.CategoriaRepository;
import br.com.viduink.dezoito_api_financas.repositories.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MovimentacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    ;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /* metodo para criar uma movimentação no banco de dados
     */
    public MovimentacaoResponse criar(MovimentacaoRequest request) {
        //Verificar se a categoria existe no banco de dados
        var categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada."));

        //Executar as validações
        validarMovimentacao(request);

        //Criando um objeto da classe Movimentacao
        var movimentacao = new Movimentacao();

        //Preenchendo os dados da Movimentacao
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);

        //Salvar a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornar os dados da movimentação cadastrada usando o DTO
        return toResponse(movimentacao);
    }

    //Metodo para validar os dados da movimentação:
    public void validarMovimentacao(MovimentacaoRequest request) {

        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da movimentação é obrigatório.");
        }
        if (request.nome().length() < 6) {
            throw new ValidacaoException("O nome da movimentação deve ter no minimo 6 caracteres.");
        }
        if (request.valor().doubleValue() <= 0) {
            throw new ValidacaoException("O valor da movimentação deve ser maior do que zero.");
        }
        if (!request.tipo().toString().equals("DESPESA") && !request.tipo().toString().equals("RECEITA")) {
            throw new ValidacaoException("O tipo da movimenação deve ter RECEITA ou DESPESA.");
        }
    }

    /* Metodo para retornar os dados do DTO de resposta da movimentação
     */
    public MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getNome(),
                movimentacao.getData(),
                movimentacao.getValor().doubleValue(),
                movimentacao.getTipo().toString(),
                new CategoriaResponse(
                        movimentacao.getCategoria().getId(),
                        movimentacao.getCategoria().getNome()
                )
        );
    }
}
