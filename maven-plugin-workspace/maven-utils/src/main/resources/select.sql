


select
p1.TREEROOTDIR,
p1.projectgroupId,
p1.projectartifactId,
p2.TREEROOTDIR as dependuponrootdir,
p2.PROJECTGROUPID as dependupongroupid,
p2.PROJECTARTIFACTID as dependuponartifactid
from DEPENDENCY d, POMFILE p1, POMFILE p2
where d.POMFILEID = p1.ID
and d.DEPENDSUPONPOMFILEID = p2.ID
--and p2.PROJECTARTIFACTID = 'squirrel-sql'
group by p1.TREEROOTDIR,
p1.projectgroupId,
p1.projectartifactId,
p2.TREEROOTDIR,
p2.PROJECTGROUPID,
p2.PROJECTARTIFACTID
--and p1.TREEROOTDIR = p2.TREEROOTDIR
