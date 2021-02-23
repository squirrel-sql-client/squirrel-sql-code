package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class FindColumnsCtrl
{
   private static final String PREF_KEY_FILTER_STRING = "FindColumnsCtrl.filter.string";

   private static final String PREF_KEY_FIND_IN_OBJECT_NAME = "FindColumnsCtrl.find.in.objectName";
   private static final String PREF_KEY_FIND_IN_COLUMN_NAME = "FindColumnsCtrl.find.in.columnName";
   private static final String PREF_KEY_FIND_IN_COLUMN_TYPE_NAME = "FindColumnsCtrl.find.in.columnTypeName";
   private static final String PREF_KEY_FIND_IN_REMARKS = "FindColumnsCtrl.find.in.remarks";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsCtrl.class);



   private final FindColumnsDlg _dlg;
   private final JavabeanArrayDataSet _resultDataSet;

   private FindColumnsScope _findColumnsScope;

   private SearchResultReader _searchResultReader;


   public FindColumnsCtrl(FindColumnsScope findColumnsScope)
   {
      try
      {
         _findColumnsScope = findColumnsScope;

         _findColumnsScope.getSession().addSimpleSessionListener(() -> onClose());

         _dlg = new FindColumnsDlg(findColumnsScope.getOwningWindow(), findColumnsScope.getDialogTitle());

         _searchResultReader = new SearchResultReader(_dlg, (res, numberOfTablesDone, totalNumberOfTables) -> displayResult(res, numberOfTablesDone, totalNumberOfTables));

         _resultDataSet = new JavabeanArrayDataSet(FindColumnsResultBean.class);

         for (FindColumnsResultTableDefinition def : FindColumnsResultTableDefinition.values())
         {
            _resultDataSet.setColHeader(def.getBeanPropName(), def.getColHeader());
            _resultDataSet.setColPos(def.getBeanPropName(), def.ordinal());
         }

         _dlg.tblSearchResult.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         _dlg.tblSearchResult.getTable().getSelectionModel().setSelectionInterval(0,0);

         _resultDataSet.setJavaBeanList(new ArrayList<>());
         _dlg.tblSearchResult.show(_resultDataSet);
         _dlg.tblSearchResult.getTable().getButtonTableHeader().adjustAllColWidths(true);


         GUIUtils.enableCloseByEscape(_dlg, dialog -> onClose());
         _dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
               onWindowClosing();
            }
         });

         GUIUtils.initLocation(_dlg, 800, 500);

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


         applyPrefs();

         _dlg.setVisible(true);

         GUIUtils.forceFocus(_dlg.txtFilter);
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private void onWindowClosing()
   {
      storePrefs();
      _searchResultReader.stopSearching();
   }

   private void onClose()
   {
      storePrefs();
      _searchResultReader.stopSearching();
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void applyPrefs()
   {
      _dlg.txtFilter.setText(Props.getString(PREF_KEY_FILTER_STRING, null));
      _dlg.txtFilter.selectAll();

      _dlg.chkColumnName.setSelected(Props.getBoolean(PREF_KEY_FIND_IN_COLUMN_NAME, true));
      _dlg.chkObjectName.setSelected(Props.getBoolean(PREF_KEY_FIND_IN_OBJECT_NAME, false));
      _dlg.chkColumnTypeName.setSelected(Props.getBoolean(PREF_KEY_FIND_IN_COLUMN_TYPE_NAME, false));
      _dlg.chkRemarks.setSelected(Props.getBoolean(PREF_KEY_FIND_IN_REMARKS, false));
   }


   private void storePrefs()
   {
      if (StringUtilities.isEmpty(_dlg.txtFilter.getText(), true))
      {
         Props.putString(PREF_KEY_FILTER_STRING, null);
      }
      else
      {
         Props.putString(PREF_KEY_FILTER_STRING, _dlg.txtFilter.getText().trim());
      }

      Props.putBoolean(PREF_KEY_FIND_IN_OBJECT_NAME, _dlg.chkObjectName.isSelected());
      Props.putBoolean(PREF_KEY_FIND_IN_COLUMN_NAME, _dlg.chkColumnName.isSelected());
      Props.putBoolean(PREF_KEY_FIND_IN_COLUMN_TYPE_NAME, _dlg.chkColumnTypeName.isSelected());
      Props.putBoolean(PREF_KEY_FIND_IN_REMARKS, _dlg.chkRemarks.isSelected());
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_ENTER)
      {
         _dlg.btnFind.doClick();
      }
   }

   private void onFind()
   {

      ColumnSearchCriterion searchCriterion = new ColumnSearchCriterion();

      if(StringUtilities.isEmpty(_dlg.txtFilter.getText(), true))
      {
         if( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("FindColumnsCtrl.msg.list.all.tables")) )
         {
            return;
         }
      }
      else
      {
         searchCriterion.setFilterString(_dlg.txtFilter.getText());
      }

      searchCriterion.setFindInObjectName(_dlg.chkObjectName.isSelected());
      searchCriterion.setFindInColumnName(_dlg.chkColumnName.isSelected());
      searchCriterion.setFindInColumnTypeName(_dlg.chkColumnTypeName.isSelected());
      searchCriterion.setFindInRemarks(_dlg.chkRemarks.isSelected());

      _searchResultReader.findAndShowResults(searchCriterion, _findColumnsScope.getITableInfos(), _findColumnsScope.getSession().getSchemaInfo());
   }


   private void displayResult(ArrayList<FindColumnsResultBean> searchResults, int numberOfTablesDone, int totalNumberOfTables)
   {
      try
      {
         _resultDataSet.setJavaBeanList(searchResults);

         _dlg.tblSearchResult.show(_resultDataSet);
         _dlg.tblSearchResult.getTable().getButtonTableHeader().adjustAllColWidths(true);

         _dlg.txtStatus.setText(s_stringMgr.getString("FindColumnsCtrl.result.count", _resultDataSet.getSize(), numberOfTablesDone, totalNumberOfTables));
      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
