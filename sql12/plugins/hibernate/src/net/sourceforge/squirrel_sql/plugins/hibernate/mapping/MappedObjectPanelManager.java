package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.plugins.hibernate.HQLTabController;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;

public class MappedObjectPanelManager
{
   private MappedObjectPanel _panel = new MappedObjectPanel(); 

   public MappedObjectPanelManager(HQLTabController hqlTabController, ISession session)
   {


   }

   public JComponent getComponent()
   {
      return _panel;
   }
}
