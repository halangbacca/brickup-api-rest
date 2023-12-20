CREATE TABLE tasks
(
    id          BIGINT(20)   NOT NULL AUTO_INCREMENT,
    description VARCHAR(100) NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    link_image  VARCHAR(1000) NULL,
    PRIMARY KEY (id)
);