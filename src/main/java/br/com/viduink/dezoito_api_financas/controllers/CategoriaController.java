package br.com.viduink.dezoito_api_financas.controllers;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaRequest;
import br.com.viduink.dezoito_api_financas.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("criar")
    public ResponseEntity<?> criar(@RequestBody CategoriaRequest request) {
        var response = categoriaService.criar(request);
        return ResponseEntity.status(201).body(response);

    }
}
