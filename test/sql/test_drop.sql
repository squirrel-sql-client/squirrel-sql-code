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
    i_data varchar(100)
);

create table e ( 
    e_pkcol integer primary key, 
    i_fk integer references i(i_pkcol),
    e_data varchar(100)
);

create table g ( 
    g_pkcol integer primary key, 
    e_fk integer references e(e_pkcol), 
    i_fk integer references i(i_pkcol),
    gdata varchar(100)
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

-- Data


INSERT INTO I (I_PKCOL,I_DATA) VALUES (0,'Some Data');
INSERT INTO I (I_PKCOL,I_DATA) VALUES (1,'Some more data');

INSERT INTO E (E_PKCOL,I_FK,E_DATA) VALUES (0,0,'Some E data');
INSERT INTO E (E_PKCOL,I_FK,E_DATA) VALUES (1,1,'Some more E data');

INSERT INTO G (G_PKCOL,E_FK,I_FK,GDATA) VALUES (0,0,0,'Some G Data');
INSERT INTO G (G_PKCOL,E_FK,I_FK,GDATA) VALUES (1,1,1,'Some more G data');

INSERT INTO C (C_PKCOL,E_FK,G_FK) VALUES (0,0,0);
INSERT INTO C (C_PKCOL,E_FK,G_FK) VALUES (1,1,1);

INSERT INTO F (F_PKCOL,C_FK,G_FK) VALUES (0,0,0);
INSERT INTO F (F_PKCOL,C_FK,G_FK) VALUES (1,1,1);

INSERT INTO A (A_PKCOL,C_FK,I_FK,F_FK) VALUES (0,0,0,0);
INSERT INTO A (A_PKCOL,C_FK,I_FK,F_FK) VALUES (1,1,1,1);

INSERT INTO D (D_PKCOL,A_FK,F_FK) VALUES (0,0,0);
INSERT INTO D (D_PKCOL,A_FK,F_FK) VALUES (1,1,1);

INSERT INTO B (B_PKCOL,D_FK,I_FK,F_FK) VALUES (0,0,0,0);
INSERT INTO B (B_PKCOL,D_FK,I_FK,F_FK) VALUES (1,1,1,1);

INSERT INTO H (H_PKCOL,A_FK,C_FK,G_FK,F_FK) VALUES (0,0,0,0,0);
INSERT INTO H (H_PKCOL,A_FK,C_FK,G_FK,F_FK) VALUES (1,1,1,1,1);

INSERT INTO J (J_PKCOL,A_FK,C_FK,G_FK,F_FK,H_FK) VALUES (0,0,0,0,0,0);
INSERT INTO J (J_PKCOL,A_FK,C_FK,G_FK,F_FK,H_FK) VALUES (1,1,1,1,1,1);
