package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

@FunctionalInterface
public interface TableCreatedListener
{
   void tableCreated(ITableInfo createdTableInfo);
}
