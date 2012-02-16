


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
group by p1.TREEROOTDIR,
p1.projectgroupId,
p1.projectartifactId,
p2.TREEROOTDIR,
p2.PROJECTGROUPID,
p2.PROJECTARTIFACTID
and p1.TREEROOTDIR = p2.TREEROOTDIR

select
p1.TREEROOTDIR,
p1.projectgroupId||':'||p1.projectartifactId,
p2.TREEROOTDIR as dependuponrootdir,
p2.PROJECTGROUPID||':'||p2.PROJECTARTIFACTID as dependuponcoordinates
from DEPENDENCY d, POMFILE p1, POMFILE p2
where d.POMFILEID = p1.ID
and d.DEPENDSUPONPOMFILEID = p2.ID
and p1.TREEROOTDIR != p2.TREEROOTDIR
group by p1.TREEROOTDIR,
p1.projectgroupId||':'||p1.projectartifactId,
p2.TREEROOTDIR,
p2.PROJECTGROUPID||':'||p2.PROJECTARTIFACTID

"select " +
"p1.TREEROOTDIR, " +
"p2.TREEROOTDIR as dependuponrootdir " +
"from DEPENDENCY d, POMFILE p1, POMFILE p2 " +
"where d.POMFILEID = p1.ID " +
"and d.DEPENDSUPONPOMFILEID = p2.ID " +
"and p1.TREEROOTDIR != p2.TREEROOTDIR " +
"group by p1.TREEROOTDIR, " +
"p2.TREEROOTDIR ";




