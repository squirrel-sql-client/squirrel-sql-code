package net.sourceforge.squirrel_sql.plugins.hibernate;

import javax.swing.AbstractAction;

public class HibernateChannel
{
   private HibernateTabController _hibernateTabController;

   public HibernateChannel(HibernateTabController hibernateTabController)
   {
      _hibernateTabController = hibernateTabController;
   }

   public HibernateConnection getHibernateConnection()
   {
      return _hibernateTabController.getHibernateConnection();
   }

   public void addConnectionListener(ConnectionListener connectionListener)
   {
      _hibernateTabController.addConnectionListener(connectionListener);
   }

   public void addToToolbar(AbstractAction action)
   {
      _hibernateTabController.addToToolbar(action);
   }

   public void displayObjects(HibernateConnection con, String hqlQuery)
   {
      _hibernateTabController.displayObjects(con, hqlQuery);
   }

   public void viewInMappedObjects(String wordAtCursor)
   {
      _hibernateTabController.viewInMappedObjects(wordAtCursor);
   }
}
