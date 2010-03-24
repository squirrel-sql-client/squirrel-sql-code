package net.sourceforge.squirrel_sql.plugins.hibernate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public interface IHibernateTabController
{

   void addToToolbar(AbstractAction action);

   void displaySqls(ArrayList<String> sqls);

   void displayObjects(HibernateConnection con, String hqlQuery);

   boolean isDisplayObjects();

   IHibernateConnectionProvider getHibernateConnectionProvider();

}
