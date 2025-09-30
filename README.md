# Demo API

API REST para gerenciamento de recursos com autenticação JWT, desenvolvida com Spring Boot 3.5.6 e Java 21.

## Tecnologias

- Java 21
- Spring Boot 3.5.6
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Swagger (Springfox)
- Docker & Docker Compose
- Lombok
- Maven

## Requisitos

- Docker e Docker Compose instalados
- OU Java 21 + Maven 3.9+ + PostgreSQL

## Como Executar

### Opção 1: Com Docker (Recomendado)

```bash
# Subir a aplicação e o banco de dados
docker-compose up --build

# Ou em background
docker-compose up -d --build

# Parar os containers
docker-compose down

# Parar e remover volumes (limpa o banco)
docker-compose down -v
```

### Opção 2: Localmente

1. Configure o PostgreSQL:
```sql
CREATE DATABASE demo_db;
```

2. Configure o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/demo_db
spring.datasource.username=postgres
spring.datasource.password=secret
```

3. Execute a aplicação:
```bash
mvn clean install
mvn spring-boot:run
```

## Acessos

- **Aplicação:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/
- **API Docs:** http://localhost:8080/v2/api-docs

## Documentação da API

A documentação completa da API está disponível através do Swagger UI. Para acessar:

1. Inicie a aplicação
2. Acesse http://localhost:8080/swagger-ui/
3. Para endpoints protegidos, clique em "Authorize" e insira o token JWT no formato: `Bearer {seu-token}`

## Estrutura do Projeto

```
src/main/java/br/com/teste/demo/
├── config/          # Configurações (Swagger, Security, etc)
├── dtos/            # Data Transfer Objects
├── enums/           # Enumerações
├── models/          # Entidades JPA
├── repositories/    # Repositórios Spring Data
├── resources/       # Controllers REST
├── security/        # Configurações de segurança JWT
└── services/        # Lógica de negócio
```

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_DATASOURCE_URL` | URL do banco PostgreSQL | `jdbc:postgresql://localhost:5432/demo_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `secret` |

## Build

```bash
# Compilar o projeto
mvn clean package

# Pular testes
mvn clean package -DskipTests

# Executar testes
mvn test
```

## Docker

### Build da imagem
```bash
docker build -t demo-api .
```

### Executar container
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/demo_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  demo-api
```

## Licença

Apache 2.0
