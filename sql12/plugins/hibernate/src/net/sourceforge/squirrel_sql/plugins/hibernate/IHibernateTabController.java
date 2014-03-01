package net.sourceforge.squirrel_sql.plugins.hibernate;


import javax.swing.*;

public interface IHibernateTabController
{
   void addToToolbar(AbstractAction action);

   void displayObjects(HibernateConnection con, String hqlQuery);

   IHibernateConnectionProvider getHibernateConnectionProvider();
}
