package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class FindColumnsCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsCtrl.class);

   private final FindColumnsDlg _dlg;
   private final JavabeanArrayDataSet _resultDataSet;

   private FindColumnsScope _findColumnsScope;

   private SearchResultReader _searchResultReader;


   public FindColumnsCtrl(FindColumnsScope findColumnsScope)
   {
      _findColumnsScope = findColumnsScope;

      _findColumnsScope.getSession().addSimpleSessionListener(() -> {_searchResultReader.stopSearching();  close();});

      _dlg = new FindColumnsDlg(findColumnsScope.getOwningWindow());

      _searchResultReader = new SearchResultReader(_dlg, res -> displayResult(res));

      _resultDataSet = new JavabeanArrayDataSet(FindColumnsResultBean.class);

      for (FindColumnsResultTableDefinition def : FindColumnsResultTableDefinition.values())
      {
         _resultDataSet.setColHeader(def.getBeanPropName(), def.getColHeader());
         _resultDataSet.setColPos(def.getBeanPropName(), def.ordinal());
      }

      _dlg.tblSearchResult.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _dlg.tblSearchResult.getTable().getSelectionModel().setSelectionInterval(0,0);

      onFind();

      GUIUtils.enableCloseByEscape(_dlg, dialog -> _searchResultReader.stopSearching());
      _dlg.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e)
         {
            _searchResultReader.stopSearching();
         }
      });

      GUIUtils.initLocation(_dlg, 500, 500);

      _dlg.txtFilter.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      // Would together with onKeyPressed() execute find twice.
      //_dlg.getRootPane().setDefaultButton(_dlg.btnFind);


      _dlg.btnFind.addActionListener(e -> onFind());
      _dlg.btnStopSearching.addActionListener(e -> _searchResultReader.stopSearching());

      _dlg.btnClose.addActionListener(e -> onClose());


      _dlg.setVisible(true);

      GUIUtils.forceFocus(_dlg.txtFilter);
   }

   private void onClose()
   {
      _searchResultReader.stopSearching();
      close();
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_ENTER)
      {
         _dlg.btnFind.doClick();
      }
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
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

         final SchemaInfo schemaInfo = _findColumnsScope.getSession().getSchemaInfo();

         ArrayList<FindColumnsResultBean> res = new ArrayList<>();

         final ITableInfo[] tableInfos = schemaInfo.getITableInfos();

         _searchResultReader.findAndShowResults(filterString, schemaInfo, res, tableInfos);
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private void displayResult(ArrayList<FindColumnsResultBean> searchResults)
   {
      try
      {
         _resultDataSet.setJavaBeanList(searchResults);

         _dlg.tblSearchResult.show(_resultDataSet);
         _dlg.tblSearchResult.getTable().getButtonTableHeader().adjustAllColWidths(true);

         _dlg.txtStatus.setText(s_stringMgr.getString("FindColumnsCtrl.result.count", _resultDataSet.getSize()));
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
