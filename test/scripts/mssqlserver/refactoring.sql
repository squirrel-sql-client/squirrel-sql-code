CREATE TABLE dbo.Vendors 
    (VendorID int PRIMARY KEY, VendorName nvarchar (50), 
    CreditRating tinyint)
GO

ALTER TABLE dbo.Vendors ADD CONSTRAINT CK_Vendor_CreditRating
    CHECK (CreditRating >= 1 AND CreditRating <= 5)
GO

    ALTER TABLE dbo.Vendors DROP CONSTRAINT CK_Vendor_CreditRating

SELECT * FROM sys.check_constraints 

SELECT * FROM sys.key_constraints 

alter table dbo.Vendors add constraint fooc unique  (VendorName)

drop table testlength

create table testlength ( mychar varchar(8000))

create table datetest ( mydate datetime)

insert into datetest values ('')



 
