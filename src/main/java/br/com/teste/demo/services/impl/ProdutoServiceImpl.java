package br.com.teste.demo.services.impl;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.exceptions.ResourceNotFoundException;
import br.com.teste.demo.models.Produto;
import br.com.teste.demo.repositories.ProdutoRepository;
import br.com.teste.demo.services.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<ProdutoDTO> getAllProducts() {
        return produtoRepository.findAll().stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ProdutoDTO getProductById(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        return ProdutoDTO.fromEntity(produto);
    }

    @Override
    @Transactional
    public ProdutoDTO createProduct(ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        produto.setTitulo(produtoDTO.getTitulo());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setValor(produtoDTO.getValor());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setStatus(produtoDTO.getStatus() != null ? produtoDTO.getStatus() : StatusProduto.ATIVO);
        produto.setDataCadastro(LocalDateTime.now());
        produto.setDataAtualizacao(LocalDateTime.now());

        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoDTO.fromEntity(produtoSalvo);
    }

    @Override
    @Transactional
    public ProdutoDTO updateProduct(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));

        produto.setTitulo(produtoDTO.getTitulo());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setValor(produtoDTO.getValor());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setStatus(produtoDTO.getStatus());
        produto.setDataAtualizacao(LocalDateTime.now());

        Produto produtoAtualizado = produtoRepository.save(produto);
        return ProdutoDTO.fromEntity(produtoAtualizado);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com id: " + id);
        }
        produtoRepository.deleteById(id);
    }

    @Override
    public List<ProdutoDTO> findByTitulo(String titulo) {
        return produtoRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoDTO> findByCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria).stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoDTO> findByStatus(StatusProduto status) {
        return produtoRepository.findByStatus(status).stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoDTO> findByValorRange(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return produtoRepository.findByValorBetween(valorMinimo, valorMaximo).stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoDTO> findByFiltros(String titulo, String categoria, BigDecimal valorMinimo,
                                          BigDecimal valorMaximo, StatusProduto status) {
        return produtoRepository.findByFiltros(titulo, categoria, valorMinimo, valorMaximo, status).stream()
                .map(ProdutoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
