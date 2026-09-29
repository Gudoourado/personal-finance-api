# Personal Finance API

API REST para controle de finanças pessoais desenvolvida com **Java 17** e **Spring Boot 3.2**.

## Tecnologias

- Java 17
- Spring Boot 3.2.4
- Spring Data JPA / Hibernate
- PostgreSQL (H2 em memória no perfil `dev`)
- Bean Validation
- Maven

## Como Executar

Pré-requisitos: Java 17 ou mais recente e Maven.

```bash
git clone https://github.com/Gudoourado/personal-finance-api.git
cd personal-finance-api/finance-api
```

### Rápido, sem instalar banco (perfil `dev`)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Usa um banco H2 em memória: sobe em segundos e os dados somem quando a aplicação para.

- API: http://localhost:8082
- Console do H2: http://localhost:8082/h2-console (JDBC URL `jdbc:h2:mem:financedb`, usuário `sa`, sem senha)

### Com PostgreSQL

Crie o banco `financedb` e rode:

```bash
mvn spring-boot:run
```

Usuário e senha vêm das variáveis `DB_USERNAME` e `DB_PASSWORD` (padrão: `postgres` / `postgres`).

## Endpoints

### Categorias
| Método | Endpoint               | Descrição               |
|--------|------------------------|-------------------------|
| GET    | /api/categories        | Listar categorias       |
| GET    | /api/categories/{id}   | Buscar por ID           |
| POST   | /api/categories        | Criar categoria         |
| PUT    | /api/categories/{id}   | Atualizar categoria     |
| DELETE | /api/categories/{id}   | Deletar categoria       |

### Transações
| Método | Endpoint                              | Descrição                    |
|--------|---------------------------------------|------------------------------|
| GET    | /api/transactions                     | Listar transações            |
| GET    | /api/transactions/{id}                | Buscar por ID                |
| POST   | /api/transactions                     | Criar transação              |
| PUT    | /api/transactions/{id}                | Atualizar transação          |
| DELETE | /api/transactions/{id}                | Deletar transação            |
| GET    | /api/transactions/type/{type}         | Filtrar por tipo             |
| GET    | /api/transactions/category/{id}       | Filtrar por categoria        |
| GET    | /api/transactions/date-range          | Filtrar por período          |
| GET    | /api/transactions/search?keyword=     | Buscar por palavra-chave     |
| GET    | /api/transactions/summary             | Resumo financeiro            |

## Exemplo de Request

```json
{
  "description": "Salário mensal",
  "amount": 3500.00,
  "type": "RECEITA",
  "date": "2025-03-01",
  "categoryId": 1,
  "notes": "Pagamento referente a março"
}
```

## Autor

**Gustavo Dourado** - Desenvolvedor Backend Jr
