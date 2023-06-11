package net.sourceforge.squirrel_sql.client.session.action.dbdiff.tableselectiondiff;

import javax.swing.*;

@FunctionalInterface
public interface DiffTableProvider
{
   JTable getTable();
}
