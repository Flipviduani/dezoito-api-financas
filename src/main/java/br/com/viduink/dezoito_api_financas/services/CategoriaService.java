package br.com.viduink.dezoito_api_financas.services;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaRequest;
import br.com.viduink.dezoito_api_financas.dtos.CategoriaResponse;
import br.com.viduink.dezoito_api_financas.entities.Categoria;
import br.com.viduink.dezoito_api_financas.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaResponse criar(CategoriaRequest request){

        //Criando um objeto da entidade 'Categoria'
        var categoria = new Categoria();

        //Capturando os dados recebidos
        categoria.setNome(request.nome());

        //Salvar a categoria no banco de daos
        categoriaRepository.save(categoria);

        //Retornar a resposta
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome()
        );
    }
}
