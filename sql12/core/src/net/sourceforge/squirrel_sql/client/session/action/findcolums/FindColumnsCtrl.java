package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class FindColumnsCtrl
{
   private final FindColumnsDlg _dlg;
   private final JavabeanArrayDataSet _resultDataSet;
   private IObjectTreeAPI _objectTreeAPI;

   public FindColumnsCtrl(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;

      _dlg = new FindColumnsDlg(Main.getApplication().getMainFrame());

      _resultDataSet = new JavabeanArrayDataSet(FindColumnsResultBean.class);

      for (FindColumnsResultTableDefinition def : FindColumnsResultTableDefinition.values())
      {
         _resultDataSet.setColHeader(def.getBeanPropName(), def.getColHeader());
         _resultDataSet.setColPos(def.getBeanPropName(), def.ordinal());
      }

      _dlg.tblSearchResult.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _dlg.tblSearchResult.getTable().getSelectionModel().setSelectionInterval(0,0);

      onFind();

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 500, 500);

      _dlg.txtFilter.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      _dlg.btnFind.addActionListener(e -> onFind());

      _dlg.getRootPane().setDefaultButton(_dlg.btnFind);

      _dlg.setVisible(true);

      GUIUtils.forceFocus(_dlg.txtFilter);
   }

   private void onFind()
   {
      try
      {
         if(StringUtilities.isEmpty(_dlg.txtFilter.getText(), true))
         {
            _resultDataSet.setJavaBeanList(new ArrayList<>());
            _dlg.tblSearchResult.show(_resultDataSet);
            _dlg.tblSearchResult.getTable().getButtonTableHeader().adjustAllColWidths(true);
            return;
         }

         final String filterString = _dlg.txtFilter.getText().trim().toLowerCase();

         final SchemaInfo schemaInfo = _objectTreeAPI.getSession().getSchemaInfo();

         ArrayList<FindColumnsResultBean> res = new ArrayList<>();

         for (ITableInfo tableInfo : schemaInfo.getITableInfos())
         {
            final ExtendedColumnInfo[] columnInfos
                  = schemaInfo.getExtendedColumnInfos(tableInfo.getCatalogName(), tableInfo.getSchemaName(), tableInfo.getSimpleName());

            for (ExtendedColumnInfo columnInfo : columnInfos)
            {
               if(-1 < columnInfo.getColumnName().toLowerCase().indexOf(filterString))
               {
                  final FindColumnsResultBean bean = new FindColumnsResultBean();
                  bean.setCatalogName(tableInfo.getCatalogName());
                  bean.setSchemaName(tableInfo.getSchemaName());
                  bean.setObjectName(tableInfo.getSimpleName());
                  bean.setObjectTypeName(tableInfo.getType());
                  bean.setColumnName(columnInfo.getColumnName());
                  bean.setColumnTypeName(columnInfo.getColumnType());
                  bean.setNullable(columnInfo.isNullable() ? 1:0);
                  bean.setSize(columnInfo.getColumnSize());
                  bean.setPrecision(columnInfo.getTableColumnInfo().getRadix());
                  bean.setDecimalDigits(columnInfo.getDecimalDigits());
                  bean.setOrdinalPosition(columnInfo.getTableColumnInfo().getOrdinalPosition());
                  bean.setRemarks(columnInfo.getRemarks());
                  bean.setJavaSqlType(columnInfo.getColumnTypeID());

                  res.add(bean);
               }
            }

         }
         _resultDataSet.setJavaBeanList(res);

         _dlg.tblSearchResult.show(_resultDataSet);
         _dlg.tblSearchResult.getTable().getButtonTableHeader().adjustAllColWidths(true);
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_ENTER)
      {
         _dlg.btnFind.doClick();
      }
   }
}
