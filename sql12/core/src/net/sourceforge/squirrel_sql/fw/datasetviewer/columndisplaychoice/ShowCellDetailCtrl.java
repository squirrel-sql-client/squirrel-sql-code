package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import javax.swing.JToggleButton;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowCellDetailCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowCellDetailCtrl.class);

   private final ResultTabProvider _resultTabProvider;
   private JToggleButton _button;

   public ShowCellDetailCtrl(ResultTab resultTab)
   {
      _resultTabProvider = new ResultTabProvider(resultTab);
   }

   public JToggleButton getTabButton()
   {
      if(null == _button)
      {
         _button = GUIUtils.styleAsTabButton(new JToggleButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PRESENTATION)));
         _button.setToolTipText(s_stringMgr.getString("ShowCellDetailCtrl.toggle.show.cellDetail.tooltip"));

         _button.setSelected(ColumnDisplayUtil.isShowCellDetail());

         _button.addActionListener(e -> onShowDetail());
      }
      return _button;
   }

   private void onShowDetail()
   {
      ResultDataSetAndCellDetailDisplayHandler selectedTabsDisplayHandler = _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler();

      _button.setEnabled(true);

      if(null == selectedTabsDisplayHandler)
      {
         CellDetailDisplayAvailableInfo.INFO_NO_DISPLAY_HANDLER.displayNotAvailableMessage();
         _button.setSelected(false);
         return;
      }
      else if(false == selectedTabsDisplayHandler.getCellDetailDisplayAvailableInfo().isAvailable())
      {
         selectedTabsDisplayHandler.getCellDetailDisplayAvailableInfo().displayNotAvailableMessage();
         _button.setSelected(false);
         return;
      }

      selectedTabsDisplayHandler.setCellDetailVisible(_button.isSelected());
   }

   public void finishedCreatingResultTab()
   {
      IResultTab resultTab = _resultTabProvider.getResultTab();

      if(null == resultTab)
      {
         return;
      }

      ResultDataSetAndCellDetailDisplayHandler selectedResultTabsDisplayHandler = resultTab.getSelectedResultTabsDisplayHandler();

      if(null == selectedResultTabsDisplayHandler)
      {
         return;
      }

      selectedResultTabsDisplayHandler.setCloseListener(() -> onClosedByPanelButton());
   }

   private void onClosedByPanelButton()
   {
      if(_button.isSelected())
      {
         _button.doClick();
      }
   }

   public boolean isOpen()
   {
      ResultDataSetAndCellDetailDisplayHandler selectedTabsDisplayHandler = _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler();
      if(null != selectedTabsDisplayHandler)
      {
         return selectedTabsDisplayHandler.isOpen();
      }

      return false;
   }
}
