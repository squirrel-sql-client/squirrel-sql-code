package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

import javax.swing.JTable;

@FunctionalInterface
public interface DiffTableProvider
{
   JTable getTable();
}
