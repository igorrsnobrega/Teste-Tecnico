package br.com.teste.demo.resources;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.services.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoResource.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Testes do ProdutoResource")
class ProdutoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoDTO produtoDTO;
    private List<ProdutoDTO> listaProdutos;

    @BeforeEach
    void setUp() {
        produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);
        produtoDTO.setTitulo("Notebook Dell");
        produtoDTO.setDescricao("Notebook de alta performance");
        produtoDTO.setValor(new BigDecimal("3500.00"));
        produtoDTO.setCategoria("Informática");
        produtoDTO.setStatus(StatusProduto.ATIVO);

        listaProdutos = Arrays.asList(produtoDTO);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    @WithMockUser
    void deveRetornarTodosProdutos() throws Exception {
        when(produtoService.getAllProducts()).thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Notebook Dell"));

        verify(produtoService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Deve retornar produto por ID")
    @WithMockUser
    void deveRetornarProdutoPorId() throws Exception {
        when(produtoService.getProductById(1L)).thenReturn(produtoDTO);

        mockMvc.perform(get("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"));

        verify(produtoService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Deve criar um novo produto com role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deveCriarNovoProdutoComRoleAdmin() throws Exception {
        when(produtoService.createProduct(any(ProdutoDTO.class))).thenReturn(produtoDTO);

        mockMvc.perform(post("/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"));

        verify(produtoService, times(1)).createProduct(any(ProdutoDTO.class));
    }

    @Test
    @DisplayName("Deve criar um novo produto com role OPERADOR")
    @WithMockUser(roles = "OPERADOR")
    void deveCriarNovoProdutoComRoleOperador() throws Exception {
        when(produtoService.createProduct(any(ProdutoDTO.class))).thenReturn(produtoDTO);

        mockMvc.perform(post("/produtos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"));

        verify(produtoService, times(1)).createProduct(any(ProdutoDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarProdutoExistente() throws Exception {
        when(produtoService.updateProduct(eq(1L), any(ProdutoDTO.class))).thenReturn(produtoDTO);

        mockMvc.perform(put("/produtos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Notebook Dell"));

        verify(produtoService, times(1)).updateProduct(eq(1L), any(ProdutoDTO.class));
    }

    @Test
    @DisplayName("Deve deletar um produto com role ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deveDeletarProdutoComRoleAdmin() throws Exception {
        doNothing().when(produtoService).deleteProduct(1L);

        mockMvc.perform(delete("/produtos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(produtoService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Deve filtrar produtos")
    @WithMockUser
    void deveFiltrarProdutos() throws Exception {
        when(produtoService.findByFiltros(anyString(), anyString(), any(), any(), any()))
                .thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos/filtrar")
                        .param("titulo", "note")
                        .param("categoria", "Informática")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(produtoService, times(1))
                .findByFiltros(anyString(), anyString(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar produtos por título")
    @WithMockUser
    void deveBuscarProdutosPorTitulo() throws Exception {
        when(produtoService.findByTitulo("note")).thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos/por-titulo")
                        .param("titulo", "note")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(produtoService, times(1)).findByTitulo("note");
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    @WithMockUser
    void deveBuscarProdutosPorCategoria() throws Exception {
        when(produtoService.findByCategoria("Informática")).thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos/por-categoria")
                        .param("categoria", "Informática")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(produtoService, times(1)).findByCategoria("Informática");
    }

    @Test
    @DisplayName("Deve buscar produtos por status")
    @WithMockUser
    void deveBuscarProdutosPorStatus() throws Exception {
        when(produtoService.findByStatus(StatusProduto.ATIVO)).thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos/por-status")
                        .param("status", "ATIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(produtoService, times(1)).findByStatus(StatusProduto.ATIVO);
    }

    @Test
    @DisplayName("Deve buscar produtos por faixa de valor")
    @WithMockUser
    void deveBuscarProdutosPorFaixaDeValor() throws Exception {
        when(produtoService.findByValorRange(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(listaProdutos);

        mockMvc.perform(get("/produtos/por-valor")
                        .param("valorMinimo", "1000")
                        .param("valorMaximo", "5000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(produtoService, times(1))
                .findByValorRange(any(BigDecimal.class), any(BigDecimal.class));
    }
}
