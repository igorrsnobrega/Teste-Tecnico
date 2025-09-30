package br.com.teste.demo.services;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.models.Produto;
import br.com.teste.demo.repositories.ProdutoRepository;
import br.com.teste.demo.services.impl.ProdutoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoService")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Produto produto;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setTitulo("Notebook Dell");
        produto.setDescricao("Notebook de alta performance");
        produto.setValor(new BigDecimal("3500.00"));
        produto.setCategoria("Informática");
        produto.setStatus(StatusProduto.ATIVO);
        produto.setDataCadastro(LocalDateTime.now());

        produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);
        produtoDTO.setTitulo("Notebook Dell");
        produtoDTO.setDescricao("Notebook de alta performance");
        produtoDTO.setValor(new BigDecimal("3500.00"));
        produtoDTO.setCategoria("Informática");
        produtoDTO.setStatus(StatusProduto.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    void deveRetornarTodosProdutos() {
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.getAllProducts();

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).isEqualTo("Notebook Dell");
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDTO produtoEncontrado = produtoService.getProductById(1L);

        assertThat(produtoEncontrado).isNotNull();
        assertThat(produtoEncontrado.getTitulo()).isEqualTo("Notebook Dell");
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não for encontrado por ID")
    void deveLancarExcecaoQuandoProdutoNaoForEncontradoPorId() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.getProductById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produto não encontrado");

        verify(produtoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve criar um novo produto")
    void deveCriarNovoProduto() {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO novoProduto = produtoService.createProduct(produtoDTO);

        assertThat(novoProduto).isNotNull();
        assertThat(novoProduto.getTitulo()).isEqualTo("Notebook Dell");
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    void deveAtualizarProdutoExistente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        produtoDTO.setTitulo("Notebook Dell Atualizado");
        ProdutoDTO produtoAtualizado = produtoService.updateProduct(1L, produtoDTO);

        assertThat(produtoAtualizado).isNotNull();
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.updateProduct(999L, produtoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produto não encontrado");

        verify(produtoRepository, times(1)).findById(999L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve deletar um produto existente")
    void deveDeletarProdutoExistente() {
        when(produtoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(produtoRepository).deleteById(1L);

        produtoService.deleteProduct(1L);

        verify(produtoRepository, times(1)).existsById(1L);
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(produtoRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> produtoService.deleteProduct(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produto não encontrado");

        verify(produtoRepository, times(1)).existsById(999L);
        verify(produtoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve buscar produtos por título")
    void deveBuscarProdutosPorTitulo() {
        when(produtoRepository.findByTituloContainingIgnoreCase("note")).thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.findByTitulo("note");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).contains("Notebook");
        verify(produtoRepository, times(1)).findByTituloContainingIgnoreCase("note");
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    void deveBuscarProdutosPorCategoria() {
        when(produtoRepository.findByCategoria("Informática")).thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.findByCategoria("Informática");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getCategoria()).isEqualTo("Informática");
        verify(produtoRepository, times(1)).findByCategoria("Informática");
    }

    @Test
    @DisplayName("Deve buscar produtos por status")
    void deveBuscarProdutosPorStatus() {
        when(produtoRepository.findByStatus(StatusProduto.ATIVO)).thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.findByStatus(StatusProduto.ATIVO);

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getStatus()).isEqualTo(StatusProduto.ATIVO);
        verify(produtoRepository, times(1)).findByStatus(StatusProduto.ATIVO);
    }

    @Test
    @DisplayName("Deve buscar produtos por faixa de valor")
    void deveBuscarProdutosPorFaixaDeValor() {
        when(produtoRepository.findByValorBetween(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.findByValorRange(
                new BigDecimal("1000.00"),
                new BigDecimal("5000.00")
        );

        assertThat(produtos).hasSize(1);
        verify(produtoRepository, times(1)).findByValorBetween(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Deve buscar produtos por filtros customizados")
    void deveBuscarProdutosPorFiltros() {
        when(produtoRepository.findByFiltros(anyString(), anyString(), any(BigDecimal.class),
                any(BigDecimal.class), any(StatusProduto.class)))
                .thenReturn(Arrays.asList(produto));

        List<ProdutoDTO> produtos = produtoService.findByFiltros(
                "note",
                "Informática",
                new BigDecimal("1000.00"),
                new BigDecimal("5000.00"),
                StatusProduto.ATIVO
        );

        assertThat(produtos).hasSize(1);
        verify(produtoRepository, times(1)).findByFiltros(
                anyString(), anyString(), any(BigDecimal.class),
                any(BigDecimal.class), any(StatusProduto.class)
        );
    }
}
