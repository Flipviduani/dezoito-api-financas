package br.com.viduink.dezoito_api_financas.services;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaResponse;
import br.com.viduink.dezoito_api_financas.dtos.MovimentacaoRequest;
import br.com.viduink.dezoito_api_financas.dtos.MovimentacaoResponse;
import br.com.viduink.dezoito_api_financas.dtos.RelatorioMovimentacaoRequest;
import br.com.viduink.dezoito_api_financas.entities.Movimentacao;
import br.com.viduink.dezoito_api_financas.enums.TipoMovimentacao;
import br.com.viduink.dezoito_api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.viduink.dezoito_api_financas.exceptions.ValidacaoException;
import br.com.viduink.dezoito_api_financas.repositories.CategoriaRepository;
import br.com.viduink.dezoito_api_financas.repositories.MovimentacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MovimentacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    ;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Queue queue;

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

    /*
        Método para alterar uma movimentaçao no banco de dados
     */
    public MovimentacaoResponse alterar(UUID id, MovimentacaoRequest request) {

        //Consultar a movimentação no banco de dados pelo id
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada."));

        //Verificar se a categoria existe no banco de dados
        var categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada."));

        //Executar as validações
        validarMovimentacao(request);

        //Preenchendo os dados da movimentação
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

    /*
        Método para excluir uma movimentaçao no banco de dados
     */
    public MovimentacaoResponse excluir(UUID id) {

        //Consultar a movimentação no banco de dados pelo id
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada."));

        //Excluir a movimentação no banco de dados
        movimentacaoRepository.delete(movimentacao);

        //Retornar os dados da movimentação cadastrada usando o DTO
        return toResponse(movimentacao);
    }

    //Metodo para consultar as movimentações por período de datas e com paginação
    public Page<MovimentacaoResponse> consultar(LocalDate dataInicio, LocalDate dataFim, int pageIndex, int pageSize) {

        //Validando as datas:
        if (dataInicio.isAfter(dataFim)) {
            throw new ValidacaoException("A data do início não pode ser maior que a data do fim.");
        }
        //Configurando a paginação
        if (pageSize > 25) pageSize = 25;
        var pageable = PageRequest.of(pageIndex, pageSize);

        //Consultar as movimentações no banco de dados
        var movimentacoes = movimentacaoRepository.findByData(dataInicio, dataFim, pageable);

        //Retornar os dados da movimentação cadastrada usando DTO
        return movimentacoes.map(this::toResponse);
    }

    //Metodo para consultar uma movimentação pelo ID
    public MovimentacaoResponse obterPorId(UUID id) {
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada."));

        //Retornando os dados da movimentação
        return toResponse(movimentacao);
    }

    //Metodo para gerar o relatório das movimentações
    public String gerarRelatorioMovimentacoes(LocalDate dataInicio, LocalDate dataFim) throws Exception {

        //Verificar se as datas estão corretas
        if(dataInicio.isAfter(dataFim)) {
            throw new ValidacaoException("A data de início não pode ser maior que a data do fim.");
        }

        //Consultando as movimentações no banco de dados através do ID:
        var movimentacoes = movimentacaoRepository.findByData(dataInicio, dataFim);

        if(movimentacoes.size() == 0) {
            return "Nenhuma movimentação foi encontrada no período de datas informado.";
        }

        //Converter a lista de movimentações em uma lista do DTO
        var response = movimentacoes.stream().map(this::toResponse).toList();

        //Criando os dados que serão enviados para a mensageria
        var relatorioMovimentacao = new RelatorioMovimentacaoRequest(
                "vidu@email.com", //TODO Pegar o e-mail do usuário logado
                dataInicio,
                dataFim,
                objectMapper.writeValueAsString(response)
        );

        //Enviando dados para a mensageria
        rabbitTemplate.convertAndSend(queue.getName(), objectMapper.writeValueAsString(relatorioMovimentacao));

        return "Sucesso! Os dados foram enviados para análise. Em breve será enviado um relatório para o seu e-mail.";
    }

    //Metodo para validar os dados da movimentação:
    public void validarMovimentacao(MovimentacaoRequest request) {

        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da movimentação é obrigatório.");
        }
        if (request.nome().length() < 6) {
            throw new ValidacaoException("O nome da movimentação deve ter no mínimo 6 caracteres.");
        }
        if (request.valor() <= 0) {
            throw new ValidacaoException("O valor da movimentação deve ser maior do que zero.");
        }
        if (!request.tipo().equals("DESPESA") && !request.tipo().equals("RECEITA")) {
            throw new ValidacaoException("O tipo da movimentação deve ter RECEITA ou DESPESA.");
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
