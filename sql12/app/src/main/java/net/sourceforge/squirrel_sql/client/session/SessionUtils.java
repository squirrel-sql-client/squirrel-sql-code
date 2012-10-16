package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.*;

public class SessionUtils
{
   public static Frame getOwningFrame(ISession destSession)
   {
      Frame owningFrame;
      if(destSession.getActiveSessionWindow().hasSQLPanelAPI())
      {
         owningFrame = GUIUtils.getOwningFrame(destSession.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel().getTextComponent());
      }
      else
      {
         owningFrame = GUIUtils.getOwningFrame(destSession.getObjectTreeAPIOfActiveSessionWindow().getDetailTabComp());
      }
      return owningFrame;
   }

   public static Frame getOwningFrame(ISQLPanelAPI sqlPanelAPI)
   {
      return GUIUtils.getOwningFrame(sqlPanelAPI.getSQLEntryPanel().getTextComponent());
   }
}
