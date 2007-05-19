package net.sourceforge.squirrel_sql.plugins.hibernate;

import javax.swing.*;

public interface IHQLTabController
{
   void displaySQLs(String sqls);

   void addToToolbar(AbstractAction action);
}
