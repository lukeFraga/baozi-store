package br.com.baozistore.controller;

import br.com.baozistore.model.Produto;
import br.com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository produtos;

    public ProdutoController(ProdutoRepository produtos) {
        this.produtos = produtos;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Cadastro cadastro) {
        Produto produto = produtos.save(
                new Produto(cadastro.nome().strip(), cadastro.preco(), cadastro.estoque()));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(produto.getId()).toUri();
        return ResponseEntity.created(local).body(produto);
    }

    @GetMapping
    public List<Produto> listar() {
        return produtos.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Produto consultar(@PathVariable Long id) {
        return produtos.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        produtos.delete(consultar(id));
    }

    public record Cadastro(
            @NotBlank @Size(max = 120) String nome,
            @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal preco,
            @NotNull Boolean estoque
    ) {}
}
