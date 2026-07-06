package br.com.viduink.dezoito_api_financas;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaRequest;
import br.com.viduink.dezoito_api_financas.dtos.CategoriaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class DezoitoApiFinancasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso.")
    public void criarCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("Categoria teste");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isCreated()) //Esperando retorno HTTP 201
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

        //ASSERT: O ID da categoria deve vir preenchido com UUID aleatório
        assertNotNull(response.id());

        //ASSERT: O nome da categoria retornado deve ser igual ao enviado no cadastro
        assertEquals(request.nome(), response.nome());
    }
}
