package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultLabelNameSwitcher
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(ResultLabelNameSwitcher.class);



   private final String _resultsTabTitle;
   private final int _tabIndex;
   private final JTabbedPane _tabResultTabs;

   private final ImageIcon _labeledIcon;
   private final ImageIcon _namedIcon;

   private ImageIcon _currentIcon;
   private SmallTabButton _btnLabelNameSwitch;
   private IDataSetViewer _dataSetViewer;

   public ResultLabelNameSwitcher(String resultsTabTitle, int tabIndex, ISession session, JTabbedPane tabResultTabs)
   {
      _resultsTabTitle = resultsTabTitle;
      _tabIndex = tabIndex;
      _tabResultTabs = tabResultTabs;

      _tabResultTabs.setTitleAt(tabIndex, resultsTabTitle);

      _labeledIcon = session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.LABELED);
      _namedIcon = session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.NAMED);

   }

   public void initLabelSwitch()
   {

      JPanel pnl = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,0,1,0), 0,0);
      JLabel title = new JLabel(_resultsTabTitle);
      pnl.add(title, gbc);

      if(DataTypeGeneral.isUseColumnLabelInsteadColumnName())
      {
         _currentIcon = _labeledIcon;
      }
      else
      {
         _currentIcon = _namedIcon;
      }

      _btnLabelNameSwitch = new SmallTabButton(s_stringMgr.getString("ResultLabelNameSwitcher.buttonToolTipHtml"), _currentIcon);

      _btnLabelNameSwitch.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onSwitchLabelName();
         }
      });


      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,2,1,0), 0,0);
      pnl.add(_btnLabelNameSwitch, gbc);
      pnl.setOpaque(false);

      _tabResultTabs.setTabComponentAt(_tabIndex, pnl);
   }

   private void onSwitchLabelName()
   {
      if(_currentIcon == _namedIcon)
      {
         _currentIcon = _labeledIcon;
         _dataSetViewer.switchColumnHeader(ColumnHeaderDisplay.COLUMN_LABEL);
      }
      else
      {
         _dataSetViewer.switchColumnHeader(ColumnHeaderDisplay.COLUMN_NAME);
         _currentIcon = _namedIcon;
      }

      _btnLabelNameSwitch.setIcon(_currentIcon);
   }

   public void setCurrentResult(ResultSetDataSet rsds, IDataSetViewer dataSetViewer)
   {
      if (false == needsLabelSwitch(rsds, dataSetViewer))
      {
         return;
      }

      _dataSetViewer = dataSetViewer;
      initLabelSwitch();

   }

   private boolean needsLabelSwitch(ResultSetDataSet rsds, IDataSetViewer dataSetViewer)
   {
      return dataSetViewer instanceof DataSetViewerTablePanel && hasLabelsDifferentFormNames(rsds);
   }

   private boolean hasLabelsDifferentFormNames(ResultSetDataSet rsds)
   {
      for (ColumnDisplayDefinition columnDisplayDefinition : rsds.getDataSetDefinition().getColumnDefinitions())
      {
         if(false == StringUtilities.equalsRespectNullModuloEmptyAndWhiteSpace(columnDisplayDefinition.getColumnName(), columnDisplayDefinition.getLabel()))
         {
            return true;
         }
      }

      return false;
   }

   public void moreResultsHaveBeenRead(ResultSetDataSet rsds)
   {
      if (false == needsLabelSwitch(rsds, _dataSetViewer))
      {
         return;
      }

      if (_currentIcon == _namedIcon)
      {
         _dataSetViewer.switchColumnHeader(ColumnHeaderDisplay.COLUMN_NAME);
      }
      else
      {
         _dataSetViewer.switchColumnHeader(ColumnHeaderDisplay.COLUMN_LABEL);
      }
   }
}
