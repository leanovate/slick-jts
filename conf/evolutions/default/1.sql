# --- !Ups
CREATE TABLE shapes (
    id bigint(20) NOT NULL,
    district_name varchar(255) NOT NULL,
    shape geometry NOT NULL,
    PRIMARY KEY (id)
);

CREATE SPATIAL INDEX ON shapes(shape);
