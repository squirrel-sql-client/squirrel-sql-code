
-- script which creates the schema that this documentation is based upon.

create table best
(
bestid integer not null primary key,
bestname varchar(250)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table bestpos
(
bestid integer not null,
bestposid integer not null primary key,
bestposname varchar(250)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table bestposart
(
bestposid integer not null,
bestposartid integer not null primary key,
bestposartname varchar(250)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table best_lagpl
(
bestid integer not null,
lagplid integer not null
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table best_lagpl add constraint best_lagpl_pk primary key (bestid,lagplid);



create table lagpl
(
lagplid integer not null primary key,
lagplname varchar(250)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table bestpos
add constraint fk_bestpos_best
foreign key (bestid)
references best (bestid);

alter table bestposart
add constraint fk_bestposart_bestpos
foreign key (bestposid)
references bestpos (bestposid);


alter table best_lagpl
add constraint fk_bestlagpl_best
foreign key (bestid)
references best (bestid);

alter table best_lagpl
add constraint fk_bestlagpl_lagpl
foreign key (lagplid)
references lagpl (lagplid);



create table gwaparent
(
parentid1 integer not null,
parentid2 integer not null,
parenttext varchar(20)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table gwaparent add constraint gwaparent_pk primary key (parentid1,parentid2);


create table gwachild
(
childid integer not null primary key,
parentid1 integer not null,
parentid2 integer not null,
childtext varchar(20)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table gwachild
add constraint fk_gwachild_gwaparent
foreign key (parentid1,parentid2)
references gwaparent (parentid1,parentid2);

