package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ColumnDisplayChoiceAction extends SquirrelAction implements ISQLPanelAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColumnDisplayChoiceAction.class);
   private static ILogger s_log = LoggerController.createLogger(ColumnDisplayChoiceAction.class);


   private final ResultTabProvider _resultTabProvider;

   public ColumnDisplayChoiceAction(ResultTab resultTab)
   {
      super(Main.getApplication());
      _resultTabProvider = new ResultTabProvider(resultTab);
   }

   public ColumnDisplayChoiceAction()
   {
      this(null);
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(false == _resultTabProvider.hasResultTab())
      {
         return;
      }

      if(false == e.getSource() instanceof TabButton)
      {
         s_log.error("ColumnDisplayChoiceAction.actionPerformed() called with unknown source.");
         return;
      }
      TabButton tabButton = (TabButton) e.getSource();

      ResultDataSetAndCellDetailDisplayHandler selectedTabsDisplayHandler = _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler();

      if(null == selectedTabsDisplayHandler)
      {
         CellDetailDisplayAvailableInfo.INFO_NO_DISPLAY_HANDLER.displayNotAvailableMessage();
         return;
      }
      else if(false == selectedTabsDisplayHandler.getCellDetailDisplayAvailableInfo().isAvailable())
      {
         selectedTabsDisplayHandler.getCellDetailDisplayAvailableInfo().displayNotAvailableMessage();
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();

      JMenuItem chkMnuShowCellDetails = new JCheckBoxMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.toggle.show.cellDetail"));
      popupMenu.add(chkMnuShowCellDetails);
      chkMnuShowCellDetails.addActionListener(ae -> onToggleShowCellDetail(chkMnuShowCellDetails));
      chkMnuShowCellDetails.setSelected(selectedTabsDisplayHandler.isCellDetailVisible());
      onToggleShowCellDetail(chkMnuShowCellDetails);


      SelectedCellInfo selectedCellInfo = selectedTabsDisplayHandler.getSelectedCellInfo();

      if(false == selectedCellInfo.hasSelectedCell())
      {
         JMenuItem mnuNoQuickCoice = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.click.cell.for.quick.choice"));
         mnuNoQuickCoice.setEnabled(false);
         popupMenu.add(mnuNoQuickCoice);
      }
      else
      {
         if(selectedCellInfo.isExtTableColumnCellSelected())
         {
            ColumnDisplayDefinition colDisp = selectedCellInfo.getSelectedColumnsDisplayDefinition();

            JMenuItem mnuDisplayAsImage = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.display.image", colDisp.getFullTableColumnName(), colDisp.getSqlTypeName()));
            mnuDisplayAsImage.addActionListener(e1 -> displayAsImage());
            popupMenu.add(mnuDisplayAsImage);
         }
         else
         {
            JMenuItem mnuNoChoiceFOrCol = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.cannot.offer.display.choice.for.selected.cell"));
            mnuNoChoiceFOrCol.setEnabled(false);
            popupMenu.add(mnuNoChoiceFOrCol);
         }
      }

      JMenuItem mnuMore = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.more"));
      popupMenu.add(mnuMore);


      popupMenu.show(tabButton, 0, tabButton.getHeight());
   }

   private void onToggleShowCellDetail(JMenuItem chkMnuShowCellDetails)
   {
      _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().setCellDetailVisible(chkMnuShowCellDetails.isSelected());
   }

   private void displayAsImage()
   {
      _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().setCellDetailVisible(true);
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelApi)
   {
      _resultTabProvider.setSQLPanelAPI(sqlPanelApi);
      setEnabled(null != sqlPanelApi);
   }
}
