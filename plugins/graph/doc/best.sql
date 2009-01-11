
-- Script which creates the schema that this documentation is based upon.

create table Best
(
BESTID INTEGER not null PRIMARY KEY,
BestName VARCHAR(250) 
);

create table BestPos
(
BESTID INTEGER not null,
BestPosID Integer not null PRIMARY KEY,
BestPosName VARCHAR(250) 
);

create table BestPosArt
(
BestPosID Integer not null,
BestPosArtID Integer not null PRIMARY KEY,
BestPosArtName VARCHAR(250) 
);

create table Best_lagpl
(
BESTID INTEGER not null,
lagplID INTEGER not null 
);

ALTER TABLE Best_lagpl ADD CONSTRAINT Best_lagpl_PK PRIMARY KEY (BESTID,lagplID);



create table LagPl
(
LagplID INTEGER not null PRIMARY KEY,
LagPlName VARCHAR(250) 
);

ALTER TABLE BestPos
ADD CONSTRAINT FK_BestPos_Best
FOREIGN KEY (BestID)
REFERENCES Best (BestID);

ALTER TABLE BestPosArt
ADD CONSTRAINT FK_BestPosArt_BestPos
FOREIGN KEY (BestPosID)
REFERENCES BestPos (BestPosID);


ALTER TABLE Best_Lagpl
ADD CONSTRAINT FK_BestLagpl_Best
FOREIGN KEY (BestID)
REFERENCES Best (BestID);

ALTER TABLE Best_Lagpl
ADD CONSTRAINT FK_BestLagpl_Lagpl
FOREIGN KEY (LagplID)
REFERENCES LagPl (LagplID);



create table gwaparent 
(
ParentID1 Integer not null,
ParentID2 Integer not null,
ParentText varchar(20)
);

ALTER TABLE gwaparent ADD CONSTRAINT gwaparent_PK PRIMARY KEY (ParentID1,ParentID2);


create table gwachild
(
ChildID Integer not null primary key,
ParentID1 Integer not null,
ParentID2 Integer not null,
ChildText varchar(20)
);

ALTER TABLE gwachild
ADD CONSTRAINT FK_gwachild_gwaparent
FOREIGN KEY (ParentID1,ParentID2)
REFERENCES gwaparent (ParentID1,ParentID2);

