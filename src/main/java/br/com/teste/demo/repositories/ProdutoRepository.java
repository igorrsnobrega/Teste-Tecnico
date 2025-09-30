package br.com.teste.demo.repositories;

import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByTituloContainingIgnoreCase(String titulo);

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByStatus(StatusProduto status);

    List<Produto> findByValorGreaterThanEqual(BigDecimal valor);

    List<Produto> findByValorLessThanEqual(BigDecimal valor);

    List<Produto> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);

    @Query("SELECT p FROM Produto p WHERE " +
           "(:titulo IS NULL OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:valorMinimo IS NULL OR p.valor >= :valorMinimo) AND " +
           "(:valorMaximo IS NULL OR p.valor <= :valorMaximo) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<Produto> findByFiltros(
            @Param("titulo") String titulo,
            @Param("categoria") String categoria,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo,
            @Param("status") StatusProduto status
    );
}
