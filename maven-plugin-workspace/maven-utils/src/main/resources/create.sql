

create table pomfile (
	id integer primary key,
	path varchar(2000) not null unique,
	name varchar(255) not null,
	treerootdir varchar(255)	not null
);

create table dependency (
     id integer primary key,
	pomfileid integer not null references pomfile(id),
	dependsuponpomfileid integer not null references pomfile(id) 
);