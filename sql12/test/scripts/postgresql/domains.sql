

CREATE DOMAIN int_unsigned AS integer CONSTRAINT int_unsigned_check CHECK ((VALUE >= 0));

CREATE DOMAIN smallint_unsigned AS integer CONSTRAINT int_unsigned_check CHECK ((VALUE >= 0));

CREATE TABLE blocks_roles ( 
module character varying(64) NOT NULL, 
delta character varying(32) NOT NULL, 
rid int_unsigned NOT NULL 
); 
 
CREATE TABLE comments ( 
cid integer NOT NULL, 
pid integer DEFAULT 0 NOT NULL, 
nid integer DEFAULT 0 NOT NULL, 
uid integer DEFAULT 0 NOT NULL, 
subject character varying(64) DEFAULT ''::character varying NOT NULL, 
comment text NOT NULL, 
hostname character varying(128) DEFAULT ''::character varying NOT NULL, 
"timestamp" integer DEFAULT 0 NOT NULL, 
score integer DEFAULT 0 NOT NULL, 
status smallint_unsigned DEFAULT (0)::smallint NOT NULL, 
format smallint DEFAULT (0)::smallint NOT NULL, 
thread character varying(255) NOT NULL, 
users text, 
name character varying(60), 
mail character varying(64), 
homepage character varying(255) 
); 
 
CREATE TABLE content_field_latitude ( 
vid int_unsigned DEFAULT 0 NOT NULL, 
nid int_unsigned DEFAULT 0 NOT NULL, 
field_latitude_value double precision 
); 
 
CREATE TABLE files ( 
fid integer NOT NULL, 
nid int_unsigned DEFAULT 0 NOT NULL, 
filename character varying(255) DEFAULT ''::character varying NOT NULL, 
filepath character varying(255) DEFAULT ''::character varying NOT NULL, 
filemime character varying(255) DEFAULT ''::character varying NOT NULL, 
filesize int_unsigned DEFAULT 0 NOT NULL, 
CONSTRAINT files_fid_check CHECK ((fid >= 0)) 
);  



