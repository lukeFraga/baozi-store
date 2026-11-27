package br.com.baozistore.controller;

import br.com.baozistore.model.Cliente;
import br.com.baozistore.repository.ClienteRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository clientes;

    public ClienteController(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cadastro cadastro) {
        Cliente cliente = clientes.save(new Cliente(cadastro.nome().strip(), cadastro.clienteDesde()));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cliente.getId()).toUri();
        return ResponseEntity.created(local).body(cliente);
    }

    @GetMapping
    public List<Cliente> listar() {
        return clientes.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Cliente consultar(@PathVariable Long id) {
        return clientes.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        clientes.delete(consultar(id));
    }

    public record Cadastro(
            @NotBlank @Size(max = 120) String nome,
            @NotNull LocalDate clienteDesde
    ) {}
}
