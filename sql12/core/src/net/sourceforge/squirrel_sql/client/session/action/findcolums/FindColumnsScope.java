package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.awt.Window;
import java.util.List;

public class FindColumnsScope
{
   private IObjectTreeAPI _objectTreeAPI;
   private ISession _session;

   private Window _owningWindow;
   private List<ITableInfo> _tablesInGraph;

   public FindColumnsScope(Window owningWindow, List<ITableInfo> tablesInGraph, ISession session)
   {
      _owningWindow = owningWindow;
      _tablesInGraph = tablesInGraph;
      _session = session;
   }

   public FindColumnsScope(IObjectTreeAPI objectTreeAPI, ISession session)
   {
      _objectTreeAPI = objectTreeAPI;
      _session = session;
      _owningWindow = GUIUtils.getOwningWindow(_session.getSessionPanel());

      if(null != _objectTreeAPI)
      {
         _session = _objectTreeAPI.getSession();
         _owningWindow = GUIUtils.getOwningWindow(_objectTreeAPI.getObjectTree());
      }
   }



   public ISession getSession()
   {
      return _session;
   }

   public Window getOwningWindow()
   {
      return _owningWindow;
   }
}
