# Budgeting API - Assistente Financeiro Inteligente com Spring AI

Projeto desenvolvido como parte da formação Spring Boot na DIO, aplicando conceitos avançados de **Spring AI** integrados a uma arquitetura limpa e orientada ao domínio (DDD / Clean Architecture).

---

## 📖 O que o projeto faz?

A **Budgeting API** é um assistente financeiro inteligente que permite registrar e consultar transações financeiras utilizando comandos de voz ou requisições REST tradicionais.

A aplicação processa comandos em linguagem natural através de um fluxo completo de Inteligência Artificial:

```
[Áudio do Usuário (.m4a/.mp3)]
               │
               ▼
   1. Transcrição (Speech-to-Text) ──► Whisper-1 (Spring AI TranscriptionModel)
               │
               ▼
   2. Raciocínio & Tool Calling    ──► GPT-4o Mini (Spring AI ChatClient + @Tool)
               │
               ▼
   3. Persistência & Negócio       ──► Casos de Uso (DDD + Spring Data JPA + MySQL)
               │
               ▼
   4. Síntese de Voz (TTS)         ──► gpt-4o-mini-tts (Spring AI TextToSpeechModel)
               │
               ▼
[Resposta em Áudio (.mp3)]
```

### Fluxo de ponta a ponta:
1. **Entrada por Voz:** O usuário grava um áudio dizendo, por exemplo, *"Gastei 80 reais no mercado"* ou *"Comprei 40 reais em remédios na farmácia"* e envia para a API.
2. **Transcrição (Speech-to-Text):** O modelo `whisper-1` transcreve o áudio para texto em português brasileiro.
3. **Seleção de Ferramenta (Tool Calling):** O modelo `gpt-4o-mini`, orientado pelas instruções do sistema (`prompts/system-message.st`), compreende o contexto, converte o valor monetário para centavos e seleciona a ferramenta adequada (`persist-transaction` ou `list-transactions-by-category`), categorizando o gasto automaticamente.
4. **Execução de Domínio:** O caso de uso correspondente executa a lógica de negócio e persiste os dados no banco relacional (MySQL).
5. **Resposta em Áudio (Text-to-Speech):** A confirmação textual gerada pelo assistente é convertida em fala humana natural (MP3) e devolvida na resposta HTTP.

---

## 🏗️ Arquitetura do Projeto

O projeto foi construído respeitando as camadas de **Clean Architecture** e **Domain-Driven Design (DDD)**:

```
src/main/java/dio/budgeting/
├── domain/                      # Camada de Domínio (regras puras de negócio)
│   ├── Category.java            # Enum com categorias: GROCERIES, PHARMA, AUTO
│   ├── Transaction.java         # Entidade rica do domínio
│   ├── TransactionId.java       # Identificador fortemente tipado (UUID)
│   └── TransactionRepository.java # Contrato da porta de saída (Repository)
│
├── application/                 # Camada de Aplicação (Casos de Uso)
│   ├── PersistTransactionUseCase.java          # Ferramenta AI: persist-transaction
│   ├── ListTransactionsByCategoryUseCase.java  # Ferramenta AI: list-transactions-by-category
│   ├── input/
│   │   └── PersistTransactionInput.java        # DTO de entrada anotado com @ToolParam
│   └── output/
│       └── TransactionOutput.java              # DTO de saída com valor formatado em Reais
│
└── infrastructure/              # Camada de Infraestrutura (Adaptadores externos)
    ├── http/                    # Adaptador HTTP / REST Controller
    │   ├── TransactionController.java
    │   ├── request/TransactionRequest.java
    │   └── response/TransactionResponse.java
    └── persistence/             # Adaptador de persistência (Spring Data JPA)
        ├── entity/TransactionEntity.java
        └── repository/
            ├── JpaTransactionRepository.java
            └── TransactionEntityRepository.java
```

---

## 🚀 Endpoints da API

### 1. Processamento por Voz (Inteligência Artificial)
- **POST** `/transactions/ai`
- **Content-Type:** `multipart/form-data`
- **Parâmetro:** `file` (arquivo de áudio, ex: `.m4a`, `.mp3`, `.wav`)
- **Resposta:** Arquivo de áudio `audio.mp3` gerado pela síntese de voz (TTS) com a resposta do assistente.

### 2. Cadastro Manual de Transação
- **POST** `/transactions`
- **Content-Type:** `application/json`
- **Corpo da requisição:**
  ```json
  {
    "description": "Compras no hortifrúti",
    "category": "GROCERIES",
    "amount": 8000
  }
  ```
  *(Nota: `amount` é informado em centavos. Exemplo: 8000 = R$ 80,00)*
- **Resposta:** `201 Created`
  ```json
  {
    "id": "e44dcfd6-44b2-4dcf-85d0-379e4da741a4",
    "category": "GROCERIES",
    "description": "Compras no hortifrúti",
    "amount": 80.0
  }
  ```

### 3. Listagem por Categoria
- **GET** `/transactions/{category}`
- **Categorias disponíveis:** `GROCERIES`, `PHARMA`, `AUTO`
- **Exemplo:** `GET /transactions/GROCERIES`
- **Resposta:** `200 OK`
  ```json
  [
    {
      "id": "e44dcfd6-44b2-4dcf-85d0-379e4da741a4",
      "category": "GROCERIES",
      "description": "Compras no hortifrúti",
      "amount": 80.0
    }
  ]
  ```

---

## 🛠️ Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 4.0.5**
- **Spring AI 2.0.0-M4** (Modelos OpenAI: ChatClient, TranscriptionModel, TextToSpeechModel)
- **Spring Data JPA & Hibernate**
- **MySQL 9** (Persistência relacional em produção e desenvolvimento)
- **H2 Database** (Banco em memória isolado para suíte de testes automatizados)
- **Docker & Docker Compose** (Orquestração do contêiner MySQL)
- **Lombok**
- **JUnit 5, AssertJ & Mockito**

---

## ⚙️ Como Executar

### Pré-requisitos
- JDK 25 instalado
- Docker e Docker Compose instalados (opcional para rodar MySQL em contêiner)
- Chave de API da OpenAI (`OPENAI_API_KEY`)

### 1. Configurar a Chave da OpenAI
Defina a variável de ambiente:

**No Linux / macOS:**
```bash
export OPENAI_API_KEY="sk-..."
```

**No Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-..."
```

**No Windows (CMD):**
```cmd
set OPENAI_API_KEY=sk-...
```

### 2. Iniciar o Banco de Dados (Docker Compose)
Caso queira subir o banco MySQL manualmente via Docker:
```bash
docker compose up -d
```
*(O Spring Boot também conta com `spring-boot-docker-compose` para inicialização automática do contêiner durante o `bootRun`).*

### 3. Executar os Testes
Para rodar a suíte completa de testes unitários e de integração de contexto:
```bash
./gradlew test
```
*(No Windows, utilize `.\gradlew.bat test`)*

### 4. Iniciar a Aplicação
```bash
./gradlew bootRun
```
A API estará disponível em `http://localhost:8080`.
