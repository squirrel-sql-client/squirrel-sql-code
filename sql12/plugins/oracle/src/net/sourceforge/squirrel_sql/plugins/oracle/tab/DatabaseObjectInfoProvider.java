package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

@FunctionalInterface
public interface DatabaseObjectInfoProvider
{
   IDatabaseObjectInfo getDatabaseObjectInfo();
}
