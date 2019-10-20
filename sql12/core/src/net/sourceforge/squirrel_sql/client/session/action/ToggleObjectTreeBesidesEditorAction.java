package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLPanelSplitter;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;

import java.awt.event.ActionEvent;

/**
 * This <CODE>Action</CODE> allows the user to commit the current SQL
 * transaction.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ToggleObjectTreeBesidesEditorAction extends SquirrelAction implements ISQLPanelAction, IToggleAction
{
   private ToggleComponentHolder _toggleComponentHolder;
   private ISQLPanelAPI _sqlPanelApi;

   private boolean _execActionPerformed = true;


   public ToggleObjectTreeBesidesEditorAction()
   {
      super(Main.getApplication());

      _toggleComponentHolder = new ToggleComponentHolder();

      setEnabled(false);
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _sqlPanelApi = panel;

      setEnabled(null != _sqlPanelApi);

      if (null == _sqlPanelApi)
      {
         return;
      }

      try
      {
         _execActionPerformed = false;
         _toggleComponentHolder.setSelected(_sqlPanelApi.getSQLPanelSplitter().isSplit());
      }
      finally
      {
         _execActionPerformed = true;
      }
   }

   @Override
   public ToggleComponentHolder getToggleComponentHolder()
   {
      return _toggleComponentHolder;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if (false == _execActionPerformed)
      {
         return;
      }

      SQLPanelSplitter sqlPanelSplitter = _sqlPanelApi.getSQLPanelSplitter();
      sqlPanelSplitter.split(_toggleComponentHolder.isSelected());

      Main.getApplication().getActionCollection().activationChanged(_sqlPanelApi.getSession().getActiveSessionWindow());
   }
}
