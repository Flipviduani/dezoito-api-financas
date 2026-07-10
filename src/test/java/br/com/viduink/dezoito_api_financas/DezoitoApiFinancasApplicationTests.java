package br.com.viduink.dezoito_api_financas;

import br.com.viduink.dezoito_api_financas.dtos.CategoriaRequest;
import br.com.viduink.dezoito_api_financas.dtos.CategoriaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiFinancasApplicationTests {

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

        //ASSERT: O ID da categoria deve vir preenchido com um UUID aleatório
        assertNotNull(response.id());

        //ASSERT: O nome da categoria retornado deve ser igual ao enviado no cadastro
        assertEquals(request.nome(), response.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da categoria estiver vazio.")
    public void validarNomeDaCategoriaObrigatorioTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da categoria é obrigatório."));
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da categoria tiver menos de 6 caracteres.")
    public void validarNomeDaCategoriaMinimoDeCaracteresTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("Teste");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da categoria deve ter no minimo 6 caracteres."));
    }

    @Test
    @DisplayName("Deve editar uma categoria com sucesso.")
    public void editarCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste - criar categoria primeiro)
        var createRequest = new CategoriaRequest("Categoria Original");
        var createResult = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        var createJsonContent = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var createResponse = objectMapper.readValue(createJsonContent, CategoriaResponse.class);
        var categoriaId = createResponse.id();

        //ACT (Executar o endpoint PUT /api/v1/categorias/alterar/{id})
        var updateRequest = new CategoriaRequest("Categoria Atualizada");
        var updateResult = mockMvc.perform(
                        put("/api/v1/categorias/alterar/" + categoriaId) //Requisição PUT para atualizar
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()) //Esperando retorno HTTP 200
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var updateJsonContent = updateResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var updateResponse = objectMapper.readValue(updateJsonContent, CategoriaResponse.class);

        //ASSERT: O ID deve ser o mesmo
        assertEquals(categoriaId, updateResponse.id());

        //ASSERT: O nome deve ser atualizado
        assertEquals(updateRequest.nome(), updateResponse.nome());
    }

    @Test
    @DisplayName("Deve retornar erro ao editar categoria que não existe.")
    public void editarCategoriaInexistenteTest() throws Exception {

        //ARRANGE (Preparar um ID que não existe)
        var categoriaId = UUID.randomUUID();
        var request = new CategoriaRequest("Categoria Teste");

        //ACT (Executar o endpoint PUT /api/v1/categorias/alterar/{id})
        var result = mockMvc.perform(
                        put("/api/v1/categorias/alterar/" + categoriaId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()) //Esperando retorno HTTP 404
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve excluir uma categoria com sucesso.")
    public void excluirCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste - criar categoria primeiro)
        var createRequest = new CategoriaRequest("Categoria para Excluir");
        var createResult = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        var createJsonContent = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var createResponse = objectMapper.readValue(createJsonContent, CategoriaResponse.class);
        var categoriaId = createResponse.id();

        //ACT (Executar o endpoint DELETE /api/v1/categorias/excluir/{id})
        var deleteResult = mockMvc.perform(
                        delete("/api/v1/categorias/excluir/" + categoriaId)) //Requisição DELETE
                .andExpect(status().isOk()) //Esperando retorno HTTP 200
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var deleteJsonContent = deleteResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var deleteResponse = objectMapper.readValue(deleteJsonContent, CategoriaResponse.class);

        //ASSERT: O ID deve ser o mesmo
        assertEquals(categoriaId, deleteResponse.id());

        //ASSERT: O nome deve ser atualizado
        assertEquals(createRequest.nome(), deleteResponse.nome());
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir categoria que não existe.")
    public void excluirCategoriaInexistenteTest() throws Exception {

        //ARRANGE (Preparar um ID que não existe)
        var categoriaId = UUID.randomUUID();

        //ACT (Executar o endpoint DELETE /api/v1/categorias/excluir/{id})
        var result = mockMvc.perform(
                        delete("/api/v1/categorias/excluir/" + categoriaId))
                .andExpect(status().isNotFound()) //Esperando retorno HTTP 404
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve obter uma categoria por ID com sucesso.")
    public void obterCategoriaPorIdTest() throws Exception {

        //ARRANGE (Preparar os dados do teste - criar categoria primeiro)
        var createRequest = new CategoriaRequest("Categoria Teste");
        var createResult = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        var createJsonContent = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var createResponse = objectMapper.readValue(createJsonContent, CategoriaResponse.class);
        var categoriaId = createResponse.id();

        //ACT (Executar o endpoint GET /api/v1/categorias/obter/{id})
        var getResult = mockMvc.perform(
                        get("/api/v1/categorias/obter/" + categoriaId)) //Requisição GET
                .andExpect(status().isOk()) //Esperando retorno HTTP 200
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var getJsonContent = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var getResponse = objectMapper.readValue(getJsonContent, CategoriaResponse.class);

        //ASSERT: O ID deve ser o mesmo
        assertEquals(categoriaId, getResponse.id());

        //ASSERT: O nome deve ser o mesmo
        assertEquals(createRequest.nome(), getResponse.nome());
    }

    @Test
    @DisplayName("Deve retornar erro ao obter categoria que não existe.")
    public void obterCategoriaPorIdInexistenteTest() throws Exception {

        //ARRANGE (Preparar um ID que não existe)
        var categoriaId = UUID.randomUUID();

        //ACT (Executar o endpoint GET /api/v1/categorias/obter/{id})
        var result = mockMvc.perform(
                        get("/api/v1/categorias/obter/" + categoriaId))
                .andExpect(status().isNotFound()) //Esperando retorno HTTP 404
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve consultar todas as categorias cadastradas.")
    public void consultarCategoriasTest() throws Exception {

        //ARRANGE (Preparar os dados do teste - criar algumas categorias)
        var request1 = new CategoriaRequest("Categoria Um");
        var request2 = new CategoriaRequest("Categoria Dois");
        var request3 = new CategoriaRequest("Categoria Três");

        mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isCreated());

        //ACT (Executar o endpoint GET /api/v1/categorias/consultar)
        var result = mockMvc.perform(
                        get("/api/v1/categorias/consultar")) //Requisição GET
                .andExpect(status().isOk()) //Esperando retorno HTTP 200
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var response = objectMapper.readValue(jsonContent, CategoriaResponse[].class);

        //ASSERT: A lista deve ter pelo menos 3 categorias
        assertNotNull(response);
        assertTrue(response.length >= 3);

        //ASSERT: Verificar se as categorias criadas estão presentes
        var nomes = Arrays.stream(response).map(CategoriaResponse::nome).toList();
        assertTrue(nomes.contains("Categoria Um"));
        assertTrue(nomes.contains("Categoria Dois"));
        assertTrue(nomes.contains("Categoria Três"));
    }
}
