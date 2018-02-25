package net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class RowsWindowFrameRegistryProvider
{
   static RowsWindowFrameRegistry getRegistry(ISession session)
   {
      RowsWindowFrameRegistry rowsWindowFrameRegistry = (RowsWindowFrameRegistry) session.getSessionLocal(RowsWindowFrameRegistry.class);

      if(null == rowsWindowFrameRegistry)
      {
         rowsWindowFrameRegistry = new RowsWindowFrameRegistry(session);
         session.putSessionLocal(RowsWindowFrameRegistry.class, rowsWindowFrameRegistry);
      }
      return rowsWindowFrameRegistry;
   }
}
