CREATE TABLE tarefas
(
    id         bigint(20)   NOT NULL AUTO_INCREMENT,
    descricao  varchar(100) NOT NULL,
    status     varchar(50)  NOT NULL,
    imagem     varchar(100) DEFAULT NULL,
    finalizado tinyint(1)   NOT NULL,
    PRIMARY KEY (id)
);