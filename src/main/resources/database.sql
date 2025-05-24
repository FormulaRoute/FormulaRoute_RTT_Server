CREATE TABLE lap(
id uuid unique primary key,
name varchar(30) null,
added TIMESTAMP WITHOUT TIME ZONE DEFAULT(now() AT TIME ZONE 'America/Sao_Paulo')
);

CREATE TABLE parameters(
id uuid unique primary key,
key varchar(30) null,
value varchar(30) null,
added TIMESTAMP WITHOUT TIME ZONE DEFAULT(now() AT TIME ZONE 'America/Sao_Paulo')
);

ALTER TABLE parameters ADD COLUMN lap_id UUID;
ALTER TABLE parameters ADD CONSTRAINT lap_fk FOREIGN KEY (lap_id)
REFERENCES lap (id);