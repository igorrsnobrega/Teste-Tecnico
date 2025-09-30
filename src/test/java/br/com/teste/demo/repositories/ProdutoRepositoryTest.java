package br.com.teste.demo.repositories;

import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.models.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do ProdutoRepository")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();

        produto1 = new Produto();
        produto1.setTitulo("Notebook Dell");
        produto1.setDescricao("Notebook de alta performance");
        produto1.setValor(new BigDecimal("3500.00"));
        produto1.setCategoria("Informática");
        produto1.setStatus(StatusProduto.ATIVO);
        produto1.setDataCadastro(LocalDateTime.now());

        produto2 = new Produto();
        produto2.setTitulo("Mouse Gamer");
        produto2.setDescricao("Mouse com RGB");
        produto2.setValor(new BigDecimal("150.00"));
        produto2.setCategoria("Periféricos");
        produto2.setStatus(StatusProduto.ATIVO);
        produto2.setDataCadastro(LocalDateTime.now());

        produtoRepository.saveAll(List.of(produto1, produto2));
    }

    @Test
    @DisplayName("Deve salvar um produto com sucesso")
    void deveSalvarProdutoComSucesso() {
        Produto novoProduto = new Produto();
        novoProduto.setTitulo("Teclado Mecânico");
        novoProduto.setDescricao("Teclado mecânico RGB");
        novoProduto.setValor(new BigDecimal("450.00"));
        novoProduto.setCategoria("Periféricos");
        novoProduto.setStatus(StatusProduto.ATIVO);
        novoProduto.setDataCadastro(LocalDateTime.now());

        Produto produtoSalvo = produtoRepository.save(novoProduto);

        assertThat(produtoSalvo.getId()).isNotNull();
        assertThat(produtoSalvo.getTitulo()).isEqualTo("Teclado Mecânico");
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        Optional<Produto> produtoEncontrado = produtoRepository.findById(produto1.getId());

        assertThat(produtoEncontrado).isPresent();
        assertThat(produtoEncontrado.get().getTitulo()).isEqualTo("Notebook Dell");
    }

    @Test
    @DisplayName("Deve buscar produtos por título contendo texto")
    void deveBuscarProdutosPorTituloContendo() {
        List<Produto> produtos = produtoRepository.findByTituloContainingIgnoreCase("note");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).contains("Notebook");
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    void deveBuscarProdutosPorCategoria() {
        List<Produto> produtos = produtoRepository.findByCategoria("Informática");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getCategoria()).isEqualTo("Informática");
    }

    @Test
    @DisplayName("Deve buscar produtos por status")
    void deveBuscarProdutosPorStatus() {
        List<Produto> produtos = produtoRepository.findByStatus(StatusProduto.ATIVO);

        assertThat(produtos).hasSize(2);
    }

    @Test
    @DisplayName("Deve buscar produtos com valor maior ou igual")
    void deveBuscarProdutosComValorMaiorOuIgual() {
        List<Produto> produtos = produtoRepository.findByValorGreaterThanEqual(new BigDecimal("1000.00"));

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).isEqualTo("Notebook Dell");
    }

    @Test
    @DisplayName("Deve buscar produtos com valor entre valores")
    void deveBuscarProdutosComValorEntre() {
        List<Produto> produtos = produtoRepository.findByValorBetween(
                new BigDecimal("100.00"),
                new BigDecimal("500.00")
        );

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).isEqualTo("Mouse Gamer");
    }

    @Test
    @DisplayName("Deve buscar produtos por filtros customizados")
    void deveBuscarProdutosPorFiltros() {
        List<Produto> produtos = produtoRepository.findByFiltros(
                "note",
                "Informática",
                new BigDecimal("1000.00"),
                new BigDecimal("5000.00"),
                StatusProduto.ATIVO
        );

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getTitulo()).isEqualTo("Notebook Dell");
    }

    @Test
    @DisplayName("Deve deletar um produto")
    void deveDeletarProduto() {
        produtoRepository.deleteById(produto1.getId());

        Optional<Produto> produtoDeletado = produtoRepository.findById(produto1.getId());
        assertThat(produtoDeletado).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar um produto")
    void deveAtualizarProduto() {
        produto1.setTitulo("Notebook Dell Atualizado");
        produto1.setDataAtualizacao(LocalDateTime.now());

        Produto produtoAtualizado = produtoRepository.save(produto1);

        assertThat(produtoAtualizado.getTitulo()).isEqualTo("Notebook Dell Atualizado");
        assertThat(produtoAtualizado.getDataAtualizacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum produto for encontrado")
    void deveRetornarListaVaziaQuandoNenhumProdutoForEncontrado() {
        List<Produto> produtos = produtoRepository.findByTituloContainingIgnoreCase("inexistente");

        assertThat(produtos).isEmpty();
    }
}
