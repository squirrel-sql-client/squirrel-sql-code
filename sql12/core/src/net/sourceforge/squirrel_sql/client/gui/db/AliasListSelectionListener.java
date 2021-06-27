package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

@FunctionalInterface
public interface AliasListSelectionListener
{
   void selectionChanged(ISQLAlias alias);
}
