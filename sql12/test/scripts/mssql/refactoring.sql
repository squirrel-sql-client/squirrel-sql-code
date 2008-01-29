

alter table fktestchildtable alter column (fkchildid, fkchildid2)
constraint name fkcon1 FOREIGN KEY REFERENCES fktestparenttable (parentid, parentid2)

drop index fooidx on fktestparenttable

create unique index fooidx on fktestparenttable (parentid2)



alter table fktestchildtable  
ADD CONSTRAINT foocon FOREIGN KEY (fkchildid, fkchildid2) REFERENCES fktestparenttable (parentid, parentid2)

create view fooview as select myid from integerdatatable
GO

EXEC sp_rename 
    @objname = 'tablerenametest', 
    @newname = 'tablewasrenamed', 
    @objtype = 'TABLE'

EXEC sp_helptext 



SELECT * FROM integerdatatable 

create view fooview as select myid from integerdatatable
GO
