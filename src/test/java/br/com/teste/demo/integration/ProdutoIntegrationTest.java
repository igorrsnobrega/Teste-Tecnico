package br.com.teste.demo.integration;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.models.Produto;
import br.com.teste.demo.repositories.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração - Produtos")
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar, buscar, atualizar e deletar um produto - Fluxo completo")
    @WithMockUser(roles = "ADMIN")
    void deveRealizarFluxoCompletoDeProduto() throws Exception {
        // Criar produto
        ProdutoDTO novoProduto = new ProdutoDTO();
        novoProduto.setTitulo("Notebook Dell");
        novoProduto.setDescricao("Notebook de alta performance");
        novoProduto.setValor(new BigDecimal("3500.00"));
        novoProduto.setCategoria("Informática");
        novoProduto.setStatus(StatusProduto.ATIVO);

        String response = mockMvc.perform(post("/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProdutoDTO produtoCriado = objectMapper.readValue(response, ProdutoDTO.class);
        Long produtoId = produtoCriado.getId();

        // Buscar produto criado
        mockMvc.perform(get("/produtos/" + produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(produtoId))
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"));

        // Atualizar produto
        novoProduto.setTitulo("Notebook Dell Atualizado");
        novoProduto.setValor(new BigDecimal("3200.00"));

        mockMvc.perform(put("/produtos/" + produtoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Notebook Dell Atualizado"))
                .andExpect(jsonPath("$.valor").value(3200.00));

        // Deletar produto
        mockMvc.perform(delete("/produtos/" + produtoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar se foi deletado
        mockMvc.perform(get("/produtos/" + produtoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Deve buscar produtos por filtros - Integração com banco")
    @WithMockUser
    void deveBuscarProdutosPorFiltros() throws Exception {
        // Criar produtos de teste
        Produto produto1 = new Produto();
        produto1.setTitulo("Notebook Dell");
        produto1.setDescricao("Notebook de alta performance");
        produto1.setValor(new BigDecimal("3500.00"));
        produto1.setCategoria("Informática");
        produto1.setStatus(StatusProduto.ATIVO);
        produto1.setDataCadastro(LocalDateTime.now());

        Produto produto2 = new Produto();
        produto2.setTitulo("Mouse Gamer");
        produto2.setDescricao("Mouse com RGB");
        produto2.setValor(new BigDecimal("150.00"));
        produto2.setCategoria("Periféricos");
        produto2.setStatus(StatusProduto.ATIVO);
        produto2.setDataCadastro(LocalDateTime.now());

        produtoRepository.save(produto1);
        produtoRepository.save(produto2);

        // Buscar por categoria
        mockMvc.perform(get("/produtos/filtrar")
                        .param("categoria", "Informática")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Notebook Dell"));

        // Buscar por faixa de valor
        mockMvc.perform(get("/produtos/por-valor")
                        .param("valorMinimo", "100")
                        .param("valorMaximo", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Mouse Gamer"));

        // Buscar por título
        mockMvc.perform(get("/produtos/por-titulo")
                        .param("titulo", "note")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Notebook Dell"));
    }

    @Test
    @DisplayName("Deve listar todos os produtos cadastrados")
    @WithMockUser
    void deveListarTodosProdutos() throws Exception {
        // Criar múltiplos produtos
        Produto produto1 = new Produto();
        produto1.setTitulo("Produto 1");
        produto1.setValor(new BigDecimal("100.00"));
        produto1.setStatus(StatusProduto.ATIVO);
        produto1.setDataCadastro(LocalDateTime.now());

        Produto produto2 = new Produto();
        produto2.setTitulo("Produto 2");
        produto2.setValor(new BigDecimal("200.00"));
        produto2.setStatus(StatusProduto.ATIVO);
        produto2.setDataCadastro(LocalDateTime.now());

        Produto produto3 = new Produto();
        produto3.setTitulo("Produto 3");
        produto3.setValor(new BigDecimal("300.00"));
        produto3.setStatus(StatusProduto.INATIVO);
        produto3.setDataCadastro(LocalDateTime.now());

        produtoRepository.save(produto1);
        produtoRepository.save(produto2);
        produtoRepository.save(produto3);

        // Listar todos
        mockMvc.perform(get("/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // Buscar por status
        mockMvc.perform(get("/produtos/por-status")
                        .param("status", "ATIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Deve validar persistência de dados no banco")
    @WithMockUser(roles = "ADMIN")
    void deveValidarPersistenciaDeDados() throws Exception {
        ProdutoDTO novoProduto = new ProdutoDTO();
        novoProduto.setTitulo("Teclado Mecânico");
        novoProduto.setDescricao("Teclado RGB");
        novoProduto.setValor(new BigDecimal("450.00"));
        novoProduto.setCategoria("Periféricos");
        novoProduto.setStatus(StatusProduto.ATIVO);

        // Criar produto via API
        String response = mockMvc.perform(post("/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoProduto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProdutoDTO produtoCriado = objectMapper.readValue(response, ProdutoDTO.class);

        // Verificar diretamente no repositório
        Produto produtoDoBanco = produtoRepository.findById(produtoCriado.getId()).orElseThrow();
        assert produtoDoBanco.getTitulo().equals("Teclado Mecânico");
        assert produtoDoBanco.getValor().compareTo(new BigDecimal("450.00")) == 0;
        assert produtoDoBanco.getCategoria().equals("Periféricos");
        assert produtoDoBanco.getStatus() == StatusProduto.ATIVO;
    }
}
