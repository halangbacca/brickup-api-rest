CREATE TABLE tasks
(
    id           bigint(20)   NOT NULL AUTO_INCREMENT,
    description  varchar(100) NOT NULL,
    status       varchar(50)  NOT NULL,
    image        varchar(100) DEFAULT NULL,
    is_completed tinyint(1)   NOT NULL,
    PRIMARY KEY (id)
);