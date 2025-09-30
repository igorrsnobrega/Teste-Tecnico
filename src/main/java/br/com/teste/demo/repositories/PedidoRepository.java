package br.com.teste.demo.repositories;

import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatusPedido(StatusPedido statusPedido);

    List<Pedido> findByDataCadastroGreaterThanEqual(LocalDateTime data);

    List<Pedido> findByDataCadastroBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Pedido> findByValorGreaterThanEqual(BigDecimal valor);

    List<Pedido> findByValorLessThanEqual(BigDecimal valor);

    List<Pedido> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:status IS NULL OR p.statusPedido = :status) AND " +
           "(:dataInicio IS NULL OR p.dataCadastro >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataCadastro <= :dataFim) AND " +
           "(:valorMinimo IS NULL OR p.valorTotal >= :valorMinimo) AND " +
           "(:valorMaximo IS NULL OR p.valorTotal <= :valorMaximo)")
    List<Pedido> findByFiltros(
            @Param("status") StatusPedido status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo
    );

    // Queries para relatórios
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    Long countPedidosByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                               @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumValorTotalByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                      @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.desconto), 0) FROM Pedido p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumDescontosByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                     @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.frete), 0) FROM Pedido p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumFreteByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                 @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.statusPedido = :status")
    Long countByStatus(@Param("status") StatusPedido status);

    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.statusPedido = :status")
    BigDecimal sumValorTotalByStatus(@Param("status") StatusPedido status);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.statusPedido = :status AND p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    Long countByStatusAndPeriodo(@Param("status") StatusPedido status,
                                  @Param("dataInicio") LocalDateTime dataInicio,
                                  @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.statusPedido = :status AND p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumValorTotalByStatusAndPeriodo(@Param("status") StatusPedido status,
                                               @Param("dataInicio") LocalDateTime dataInicio,
                                               @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COALESCE(SUM(i.quantidade), 0) FROM ItemPedido i JOIN i.pedido p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    Integer sumQuantidadeProdutosByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                          @Param("dataFim") LocalDateTime dataFim);
}
