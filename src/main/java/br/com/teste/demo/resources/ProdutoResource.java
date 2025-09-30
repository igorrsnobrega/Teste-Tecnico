package br.com.teste.demo.resources;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoResource {

    private final ProdutoService produtoService;

    public ProdutoResource(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> getAllProducts() {
        return ResponseEntity.ok(produtoService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ProdutoDTO> createProduct(@RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO produtoCriado = produtoService.createProduct(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ProdutoDTO> updateProduct(@PathVariable Long id,
                                                     @RequestBody ProdutoDTO produtoDTO) {
        return ResponseEntity.ok(produtoService.updateProduct(id, produtoDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        produtoService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<ProdutoDTO>> filterProducts(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo,
            @RequestParam(required = false) StatusProduto status) {

        return ResponseEntity.ok(produtoService.findByFiltros(titulo, categoria, valorMinimo, valorMaximo, status));
    }

    @GetMapping("/por-titulo")
    public ResponseEntity<List<ProdutoDTO>> findByTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(produtoService.findByTitulo(titulo));
    }

    @GetMapping("/por-categoria")
    public ResponseEntity<List<ProdutoDTO>> findByCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(produtoService.findByCategoria(categoria));
    }

    @GetMapping("/por-status")
    public ResponseEntity<List<ProdutoDTO>> findByStatus(@RequestParam StatusProduto status) {
        return ResponseEntity.ok(produtoService.findByStatus(status));
    }

    @GetMapping("/por-valor")
    public ResponseEntity<List<ProdutoDTO>> findByValorRange(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        return ResponseEntity.ok(produtoService.findByValorRange(valorMinimo, valorMaximo));
    }
}
