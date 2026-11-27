# Baozi Store

API REST para clientes, produtos e pedidos de uma loja de pães chineses. Cada pedido referencia um cliente e um produto e registra a quantidade comprada.

## Executar

Requisitos: JDK 21 e Maven 3.9 ou compatível.

```sh
mvn clean package
java -jar target/baozi-store-1.0.0.jar
```

Endereço padrão: `http://127.0.0.1:8080`. O H2 mantém os dados em `data/` na pasta de execução. O Spring Data JPA cria as tabelas ao iniciar a API pela primeira vez.

## Operações

| Recurso | Criar | Listar | Consultar | Excluir |
| --- | --- | --- | --- | --- |
| Cliente | POST /clientes | GET /clientes | GET /clientes/{id} | DELETE /clientes/{id} |
| Produto | POST /produtos | GET /produtos | GET /produtos/{id} | DELETE /produtos/{id} |
| Pedido | POST /pedidos | GET /pedidos | GET /pedidos/{id} | DELETE /pedidos/{id} |

Exemplos de corpos JSON:

```json
{"nome":"ClienteExemplo","clienteDesde":"2026-11-26"}
```

```json
{"nome":"Baozi tradicional","preco":8.50,"estoque":true}
```

```json
{"clienteId":1,"produtoId":1,"quantidade":3}
```

Use os IDs retornados nas respostas de criação de cliente e produto. POST retorna 201 e o cabeçalho Location. GET retorna 200. DELETE retorna 204. O servidor retorna 400 para dados inválidos, 404 para IDs inexistentes e 409 quando a exclusão de um cliente ou produto conflita com um pedido vinculado.

## Postman

Importe `postman/baozi-store.postman_collection.json` e rode as requisições em ordem. A coleção grava os IDs dos cadastros e usa esses IDs nas consultas e exclusões. As requisições de exclusão do pedido precedem a remoção dos registros vinculados.

## Organização

- `model` contém as entidades JPA.
- `repository` usa Spring Data JPA para persistência.
- `controller` valida e trata requisições HTTP e retorna JSON por Spring MVC.

