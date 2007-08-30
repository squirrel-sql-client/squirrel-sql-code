package net.sourceforge.squirrel_sql.plugins.hibernate;

import javax.swing.*;
import java.util.ArrayList;

public interface IHibernateTabController
{

   void addToToolbar(AbstractAction action);

   void displaySqls(ArrayList<String> sqls);

   IHibernateConnectionProvider getHibernateConnectionProvider();
}
