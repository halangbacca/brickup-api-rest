# **Desafio BrickUp**

Criar um sistema no qual é possível cadastrar e concluir tarefas em uma interface web. O servidor deverá
armazenar as informações em um banco de dados MySQL e salvar a foto em arquivo ou no próprio banco de
dados, fornecendo os endpoints REST para a interface web.

## **Ferramentas Utilizadas para Desenvolvimento**

```
IntelliJ IDEA Ultimate 2023.3.1
MySQL 8.0.35
DBeaver 23.3.0
Postman 10.21.2
Spring 3.2.0  
Java 21 LTS
```

## **Dependências**

O desenvolvimento de código em Java, em geral, usufrui de um significativo conjunto de bibliotecas e _frameworks_. Esta
reutilização é incorporada em um projeto por meio de dependências. Para gerenciar foi utiliado o _Maven_.

```
Spring Web MVC
Spring JPA
MySQL JDBC
Lombok
Flyway
ModelMapper
DevTools
Validation
```

## **Scripts**

Todo script para criação do banco de dados se encontra na pasta **resources/db/migration**

## **Métodos**

Requisições para a API devem seguir os padrões:

| Método   | Descrição                                             |
|----------|-------------------------------------------------------|
| `GET`    | Retorna informações de um ou mais registros.          |
| `POST`   | Utilizado para criar um novo registro.                |
| `PATCH`  | Atualiza dados de um registro ou altera sua situação. |
| `DELETE` | Remove um registro do sistema.                        |

## **Respostas**

| Status | Descrição                                                          |
|--------|--------------------------------------------------------------------|
| `200`  | Requisição executada com sucesso (success).                        |
| `201`  | Requisição executada com sucesso (success).                        |
| `400`  | Erros de validação ou os campos informados não existem no sistema. |
| `409`  | Conflito.                                                          |
| `405`  | Método não implementado.                                           |

# **Recursos da API**

| Método   | Endpoint              |
|----------|-----------------------|
| `GET`    | /api/tasks            |
| `GET`    | /api/tasks/{id}       |
| `GET`    | /api/tasks/{id}/image |
| `POST`   | /api/tasks            |
| `PATCH`  | /api/tasks/{id}       |
| `DELETE` | /api/tasks/{id}       |