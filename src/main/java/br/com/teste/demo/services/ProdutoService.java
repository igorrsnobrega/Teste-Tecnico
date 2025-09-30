package br.com.teste.demo.services;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProdutoService {

    List<ProdutoDTO> getAllProducts();

    ProdutoDTO getProductById(Long id);

    ProdutoDTO createProduct(ProdutoDTO produtoDTO);

    ProdutoDTO updateProduct(Long id, ProdutoDTO produtoDTO);

    void deleteProduct(Long id);

    List<ProdutoDTO> findByTitulo(String titulo);

    List<ProdutoDTO> findByCategoria(String categoria);

    List<ProdutoDTO> findByStatus(StatusProduto status);

    List<ProdutoDTO> findByValorRange(BigDecimal valorMinimo, BigDecimal valorMaximo);

    List<ProdutoDTO> findByFiltros(String titulo, String categoria, BigDecimal valorMinimo,
                                    BigDecimal valorMaximo, StatusProduto status);
}
