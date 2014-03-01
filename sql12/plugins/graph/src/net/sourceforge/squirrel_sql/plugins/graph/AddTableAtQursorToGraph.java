package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddTableAtQursorToGraph extends SquirrelAction implements ISQLPanelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AddTableAtQursorToGraph.class);


   private ISQLPanelAPI _panel;
   private GraphPlugin _plugin;

   public AddTableAtQursorToGraph(IApplication app, PluginResources resources, GraphPlugin plugin)
   {
      super(app, resources);
      _plugin = plugin;
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && _panel.isInMainSessionWindow());
   }

   /**
    * View the Object at cursor in the Object Tree
    *
    * @param	evt		Event being executed.
    */
   public synchronized void actionPerformed(ActionEvent evt)
   {
      if (_panel == null)
      {
         return;
      }

      String stringAtCursor = _panel.getSQLEntryPanel().getWordAtCursor();

      TableQualifier tq = new TableQualifier(stringAtCursor);

      ITableInfo[] tableInfos = _panel.getSession().getSchemaInfo().getITableInfos(tq.getCatalog(), tq.getSchema(), tq.getTableName());

      if(0 == tableInfos.length)
      {
         JOptionPane.showMessageDialog(_panel.getSQLEntryPanel().getTextComponent(), s_stringMgr.getString("graph.AddTableAtQursorToGraph.noTable", stringAtCursor));
      }

      TableToGraph.sendToGraph(_plugin, _panel.getSession(), new TableToAddWrapper(tableInfos[0]));
   }

}
