package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;


public class ViewObjectAtCursorInObjectTreeAction extends SquirrelAction
											implements ISQLPanelAction
{

	/** Current panel. */
	private ISQLPanelAPI _panel;


	/**
	 * Ctor specifying Application API.
	 *
	 * @param	app	Application API.
	 */
	public ViewObjectAtCursorInObjectTreeAction(IApplication app)
	{
		super(app);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
	}

	/**
	 * View the Object at cursor in the Object Tree
	 *
	 * @param	evt		Event being executed.
	 */
	public synchronized void actionPerformed(ActionEvent evt)
	{
      try
      {
         if (_panel != null)
         {
            String dbObjectStringAtCursor = getDbObjectStringAtCursor();

            IDatabaseObjectInfo[] databaseObjectInfos = getMatchingDatabaseObjectInfos(dbObjectStringAtCursor);

            if(0 == databaseObjectInfos.length)
            {
               String msg = "Could not find a database object for string " + dbObjectStringAtCursor;
               JOptionPane.showMessageDialog(_panel.getSession().getApplication().getMainFrame(), msg);
               return;
            }
            else if(1 < databaseObjectInfos.length)
            {
               String msg = "Found the following matching database objects matching " + dbObjectStringAtCursor + ".\n";

               for (int i = 0; i < Math.min(5, databaseObjectInfos.length); i++)
               {
                  msg += databaseObjectInfos[i].getQualifiedName() + "\n";
               }

               if(databaseObjectInfos.length > 5)
               {
                  msg += "...\n";
               }

               msg += "Will select the first.";
               JOptionPane.showMessageDialog(_panel.getSession().getApplication().getMainFrame(), msg);
            }

            _panel.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
            boolean success = _panel.getSession().getObjectTreeAPIOfActiveSessionWindow().selectInObjectTree(databaseObjectInfos[0]);

            if(false == success)
            {
               String msg = "Could not locate the database object " + databaseObjectInfos[0] + "in Object tree";
               JOptionPane.showMessageDialog(_panel.getSession().getApplication().getMainFrame(), msg);
            }

         }
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private IDatabaseObjectInfo[] getMatchingDatabaseObjectInfos(String dbObjectStringAtCursor) throws SQLException
   {
      if(null == dbObjectStringAtCursor)
      {
         return new IDatabaseObjectInfo[0];
      }

      String[] buf = dbObjectStringAtCursor.split("\\.");

      String catalog = null;
      String schema = null;
      String object = null;

      if(buf.length >= 3)
      {
         catalog = buf[buf.length-3];
      }
      if(buf.length >= 2)
      {
         schema = buf[buf.length-2];
      }
      if(buf.length >= 1)
      {
         object = buf[buf.length-1];
      }

      String caseBuf = _panel.getSession().getSchemaInfo().getCaseSensitiveTableName(object);

      if(null != caseBuf)
      {
         object = caseBuf;
      }

      _panel.getSession().getSchemaInfo().getCaseSensitiveTableName(object);


      return _panel.getSession().getSQLConnection().getSQLMetaData().getTables(catalog, schema, object, new String[]{"TABLE", "VIEW"});

   }

   private String getDbObjectStringAtCursor()
   {
      String selectedText = _panel.getSQLEntryPanel().getSelectedText();

      if(null != selectedText)
      {
         return selectedText;
      }
      else
      {
         

         return null;
      }
   }
}
