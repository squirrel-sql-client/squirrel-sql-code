package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import java.awt.event.ActionEvent;

public class ExecuteAllSqlsAction extends SquirrelAction implements ISQLPanelAction
{
   private ISQLPanelAPI _panel;

   public ExecuteAllSqlsAction(IApplication app)
   {
      super(app);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && getApplication().getSquirrelPreferences().isAllowRunAllSQLsInEditor());
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (_panel == null)
      {
         return;
      }

      CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
      cursorChg.show();
      try
      {
         _panel.executeAllSQLs();
      }
      finally
      {
         cursorChg.restore();
      }

   }
}

