package br.com.baozistore.controller;

import br.com.baozistore.model.Pedido;
import br.com.baozistore.repository.ClienteRepository;
import br.com.baozistore.repository.PedidoRepository;
import br.com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoController(PedidoRepository pedidos, ClienteRepository clientes, ProdutoRepository produtos) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Representacao> criar(@Valid @RequestBody Cadastro cadastro) {
        var cliente = clientes.findById(cadastro.clienteId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        var produto = produtos.findById(cadastro.produtoId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
        Pedido pedido = pedidos.save(new Pedido(cliente, produto, cadastro.quantidade()));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(pedido.getId()).toUri();
        return ResponseEntity.created(local).body(Representacao.de(pedido));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<Representacao> listar() {
        return pedidos.findAll(Sort.by("id")).stream().map(Representacao::de).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Representacao consultar(@PathVariable Long id) {
        return Representacao.de(buscar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void excluir(@PathVariable Long id) {
        pedidos.delete(buscar(id));
    }

    private Pedido buscar(Long id) {
        return pedidos.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    public record Cadastro(
            @NotNull @Positive Long clienteId,
            @NotNull @Positive Long produtoId,
            @NotNull @Positive Integer quantidade
    ) {}

    public record Representacao(Long id, Long clienteId, Long produtoId, int quantidade) {
        static Representacao de(Pedido pedido) {
            return new Representacao(pedido.getId(), pedido.getCliente().getId(),
                    pedido.getProduto().getId(), pedido.getQuantidade());
        }
    }
}
