package br.com.viduink.dezoito_api_financas.controllers;

import br.com.viduink.dezoito_api_financas.dtos.MovimentacaoRequest;
import br.com.viduink.dezoito_api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.viduink.dezoito_api_financas.exceptions.ValidacaoException;
import br.com.viduink.dezoito_api_financas.services.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequestMapping("/ap1/v1/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping("criar")
    public ResponseEntity<?> criar(@RequestBody MovimentacaoRequest request) {
        try {
            var response = movimentacaoService.criar(request);

            return ResponseEntity.status(201).body(response);

        } catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());

        } catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("alterar/{id}")
    public ResponseEntity<?> alterar(@PathVariable UUID id, @RequestBody MovimentacaoRequest request) {
        try {
            var response = movimentacaoService.alterar(id, request);

            return ResponseEntity.status(200).body(response);

        } catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());

        } catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("excluir/{id}")
    public ResponseEntity<?> excluir(@PathVariable UUID id) {

        try {
            var response = movimentacaoService.excluir(id);

            return ResponseEntity.status(200).body(response);

        } catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }

    }


    @GetMapping("consultar")
    public ResponseEntity<?> consultar(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam (defaultValue = "0") int pageIndex,
            @RequestParam (defaultValue = "25") int pageSize
            ) {
        try {
            var response = movimentacaoService.consultar(dataInicio, dataFim, pageIndex, pageSize);

            return ResponseEntity.status(200).body(response);

        } catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }

    @GetMapping("obter/{id}")
    public ResponseEntity<?> obter(@PathVariable UUID id) {
        try{
            var response = movimentacaoService.obterPorId(id);

            return ResponseEntity.status(200).body(response);

        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
