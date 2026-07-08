package br.com.viduink.dezoito_api_financas.controllers;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaRequest;
import br.com.viduink.dezoito_api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.viduink.dezoito_api_financas.exceptions.ValidacaoException;
import br.com.viduink.dezoito_api_financas.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("criar")
    public ResponseEntity<?> criar(@RequestBody CategoriaRequest request) {

        try {
            var response = categoriaService.criar(request);

            //HTTP 201 (CREATED)
            return ResponseEntity.status(201).body(response);

        } catch (ValidacaoException e) {

            //HTTP 400 (BAD REQUEST)
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("alterar/{id}")
    public ResponseEntity<?> alterar(@PathVariable UUID id, @RequestBody CategoriaRequest request) {

        try {
            var response = categoriaService.alterar(id, request);

            //HTTP 200 (OK)
            return ResponseEntity.status(200).body(response);

        } catch (RegistroNaoEncontradoException e) {

            //HTTP 404 (NOT FOUND)
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("excluir/{id}")
    public ResponseEntity<?> excluir(@PathVariable UUID id) {

        try {
            var response = categoriaService.excluir(id);

            //HTTP 200 (OK)
            return ResponseEntity.status(200).body(response);

        } catch (RegistroNaoEncontradoException e) {

            //HTTP 404 (NOT FOUND)
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("consultar")
    public ResponseEntity<?> consultar() {

            var response = categoriaService.consultar();

            //HTTP 200 (OK)
            return ResponseEntity.status(200).body(response);
            //desenvolvimento dos serviços e api de categoria
    }
}
