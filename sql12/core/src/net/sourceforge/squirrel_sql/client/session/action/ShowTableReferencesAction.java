package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.ReferencesFrameStarter;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.RootTable;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ShowTableReferencesAction extends SquirrelAction implements IObjectTreeAction
{
   private IObjectTreeAPI _tree;

   public ShowTableReferencesAction(IApplication app)
   {
      super(app);
   }

   public void setObjectTree(IObjectTreeAPI tree)
   {
      _tree = tree;
      setEnabled(null != _tree);
   }

   public void actionPerformed(ActionEvent e)
   {
      if (_tree != null)
      {
         List<ITableInfo> tables = _tree.getSelectedTables();
         if (tables.size() > 0)
         {
            ITableInfo tableInfo = tables.get(0);


            ResultMetaDataTable table = new ResultMetaDataTable(tableInfo.getCatalogName(), tableInfo.getSchemaName(), tableInfo.getSimpleName());
            RootTable rootTable = new RootTable(table, new ArrayList<InStatColumnInfo>());
            ReferencesFrameStarter.showReferences(rootTable, _tree.getSession(), getApplication().getMainFrame());
         }
      }
   }
}
