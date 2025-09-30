package br.com.teste.demo.repositories;

import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import br.com.teste.demo.models.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedidoId(Long pedidoId);

    List<Pagamento> findByFormaPagamento(FormaPagamento formaPagamento);

    List<Pagamento> findByStatusPagamento(StatusPagamento statusPagamento);

    Optional<Pagamento> findByCodigoTransacao(String codigoTransacao);

    List<Pagamento> findByDataPagamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT p FROM Pagamento p WHERE p.pedido.id = :pedidoId AND p.statusPagamento = :status")
    List<Pagamento> findByPedidoIdAndStatus(@Param("pedidoId") Long pedidoId,
                                             @Param("status") StatusPagamento status);

    @Query("SELECT p FROM Pagamento p WHERE " +
           "(:formaPagamento IS NULL OR p.formaPagamento = :formaPagamento) AND " +
           "(:statusPagamento IS NULL OR p.statusPagamento = :statusPagamento) AND " +
           "(:dataInicio IS NULL OR p.dataPagamento >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataPagamento <= :dataFim)")
    List<Pagamento> findByFiltros(
            @Param("formaPagamento") FormaPagamento formaPagamento,
            @Param("statusPagamento") StatusPagamento statusPagamento,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
