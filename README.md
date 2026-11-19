# Jogadores
Cadastrar os jogadores no sistema e fornecer uma interface para que as demais aplicações possam verificar as suas credenciais e autorizações

## Diagrama de classes

![Diagrama de classes v3](Diagrama%20Jogadores%20V3.webp)

[Versão em PDF](Diagrama_Jogadores_v3.pdf)

## Como executar

Local (precisa de Java 21; defina `JWT_SECRET` e, se preciso, `BROKER_URL`):

```
./mvnw spring-boot:run
```

Docker:

```
cp .env.example .env   # preencha JWT_SECRET
docker compose up --build
```

A documentação Swagger fica em `http://localhost:8080/swagger-ui.html`. A configuração de segurança deve liberar `/swagger-ui.html`, `/swagger-ui/**` e `/v3/api-docs/**`.

## Eventos publicados

| Tópico | Quando | Campos | Consumidor |
|---|---|---|---|
| `jogador.criado` | Após cadastrar um jogador | `jogadorId`, `nome`, `email`, `dataCriacao` | Distribuição de Cartas |

O evento vai ao Broker por `POST /publicar` (`{"topico": ..., "dados": {...}}`). Se o Broker estiver fora do ar, o erro é registrado em log e o cadastro não é bloqueado.
