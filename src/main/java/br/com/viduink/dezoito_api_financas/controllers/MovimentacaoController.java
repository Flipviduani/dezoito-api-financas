package br.com.viduink.dezoito_api_financas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ap1/v1/movimentacoes")
public class MovimentacaoController {

    @PostMapping("criar")
    public ResponseEntity<?> criar() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("alterar")
    public ResponseEntity<?> alterar() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("excluir")
    public ResponseEntity<?> excluir() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("consultar")
    public ResponseEntity<?> consultar() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("obter")
    public ResponseEntity<?> obter() {
        return ResponseEntity.ok().build();
    }
}
