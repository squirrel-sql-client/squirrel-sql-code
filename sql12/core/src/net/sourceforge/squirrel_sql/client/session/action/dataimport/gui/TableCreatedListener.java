package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

@FunctionalInterface
public interface TableCreatedListener
{
   void tableCreated(ITableInfo createdTableInfo);
}
