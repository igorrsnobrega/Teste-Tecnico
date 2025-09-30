package br.com.teste.demo.repositories;

import br.com.teste.demo.models.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("SELECT i.produto.id as produtoId, i.produto.titulo as produtoTitulo, " +
           "SUM(i.quantidade) as quantidadeVendida, " +
           "SUM(i.valorTotal) as valorTotalVendido, " +
           "COUNT(DISTINCT i.pedido.id) as numeroPedidos " +
           "FROM ItemPedido i " +
           "WHERE i.pedido.dataCadastro BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY i.produto.id, i.produto.titulo " +
           "ORDER BY SUM(i.quantidade) DESC")
    List<Object[]> findProdutosMaisVendidosByPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                                      @Param("dataFim") LocalDateTime dataFim);
}
