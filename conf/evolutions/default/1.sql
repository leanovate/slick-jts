# --- !Ups
CREATE ALIAS IF NOT EXISTS SPATIAL_INIT FOR
  "org.h2gis.h2spatialext.CreateSpatialExtension.initSpatialExtension";
CALL SPATIAL_INIT();

CREATE TABLE shapes (
    id bigint(20) NOT NULL,
    borough_name varchar(255) NOT NULL,
    shape geometry NOT NULL,
    PRIMARY KEY (id)
);

CREATE SPATIAL INDEX ON shapes(shape);
