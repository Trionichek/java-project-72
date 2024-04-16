DROP TABLE IF EXISTS url_check;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at timestamp
);

CREATE TABLE url_check (
    id INT PRIMARY KEY AUTO_INCREMENT,
    url_id INT REFERENCES urls(id) NOT NULL,
    status_code INT,
    h1 VARCHAR(100),
    title VARCHAR(100),
    description VARCHAR(255),
    created_at timestamp
);