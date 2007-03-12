select * from a
select a from a
select a.* from a
select a.b from a
select a.b.* from a
select a.b.c from a
select * from a, b
select a from a, b
select a.* from a, b
select a.b from a, b
select a.b.* from a, b
select a.b.c from a, b
select * from a where b in (1)
select * from a where b in (1, 2, 3)
select * from a where b is null
select * from a where (b is null)
select * from a where b is null and c is null
select * from a where (b is null) and (c is null)
select * from a where (b = 10) and (c is null)
select * from a where ((a.b.b is null) and (a.b.c is null))