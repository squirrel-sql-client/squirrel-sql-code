package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;
import java.awt.event.ActionEvent;


public class EscapeDateAction extends SquirrelAction implements ISQLPanelAction
{
   private ISession _session;

   public EscapeDateAction(IApplication app, net.sourceforge.squirrel_sql.fw.util.Resources rsrc)
   {
      super(app, rsrc);
   }

   public void actionPerformed(ActionEvent e)
   {
      Frame owningFrame = SessionUtils.getOwningFrame(_session);


      new EscapeDateController(owningFrame, new EscapeDateListener()
      {
         @Override
         public void setDateString(String escapedString)
         {
             _session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().replaceSelection(escapedString);
         }
      }, false);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         _session = panel.getSession();
      }
      else
      {
         _session = null;
      }
      setEnabled(null != _session);
   }

}
