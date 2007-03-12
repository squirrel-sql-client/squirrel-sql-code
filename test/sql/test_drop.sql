--DROP TABLE "TEST--DROP"."J";

--DROP TABLE "TEST--DROP"."B";

--DROP TABLE "TEST--DROP"."H";

--DROP TABLE "TEST--DROP"."D";

--DROP TABLE "TEST--DROP"."A";

--DROP TABLE "TEST--DROP"."F";

--DROP TABLE "TEST--DROP"."C";

--DROP TABLE "TEST--DROP"."G";

--DROP TABLE "TEST--DROP"."E";

--DROP TABLE "TEST--DROP"."I";




create table i (
    i_pkcol integer primary key, 
    i_data varchar2(100)
);

create table e ( 
    e_pkcol integer primary key, 
    i_fk integer references i(i_pkcol),
    e_data varchar2(100)
);

create table g ( 
    g_pkcol integer primary key, 
    e_fk integer references e(e_pkcol), 
    i_fk integer references i(i_pkcol),
    gdata varchar2(100)
);

create table c ( 
    c_pkcol integer primary key, 
    e_fk integer references e(e_pkcol),
    g_fk integer references g(g_pkcol)
);

create table f ( 
    f_pkcol integer primary key, 
    c_fk integer references c(c_pkcol),
    g_fk integer references g(g_pkcol)
);

create table a ( 
    a_pkcol integer primary key, 
    c_fk integer references c(c_pkcol),
    i_fk integer references i(i_pkcol),
    f_fk integer references f(f_pkcol)
);

create table d ( 
    d_pkcol integer primary key, 
    a_fk integer references a(a_pkcol),
    f_fk integer references f(f_pkcol)
);

create table b ( 
    b_pkcol integer primary key, 
    d_fk integer references d(d_pkcol),
    i_fk integer references i(i_pkcol),
    f_fk integer references f(f_pkcol)
);

create table h (
    h_pkcol integer primary key, 
    a_fk integer references a(a_pkcol),   
    c_fk integer references c(c_pkcol),
    g_fk integer references g(g_pkcol),
    f_fk integer references f(f_pkcol)
);

create table j (
    j_pkcol integer primary key, 
    a_fk integer references a(a_pkcol),   
    c_fk integer references c(c_pkcol),
    g_fk integer references g(g_pkcol),
    f_fk integer references f(f_pkcol),
    h_fk integer references h(h_pkcol)
);


