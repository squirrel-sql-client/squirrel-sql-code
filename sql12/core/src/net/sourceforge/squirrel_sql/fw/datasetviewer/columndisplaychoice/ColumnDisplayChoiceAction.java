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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.List;

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

      popupMenu.addSeparator();


      SelectedCellInfo selectedCellInfo = selectedTabsDisplayHandler.getSelectedCellInfo();

      if(false == selectedCellInfo.hasSelectedCell())
      {
         JMenuItem mnuNoQuickChoice = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.click.cell.for.quick.choice"));
         mnuNoQuickChoice.setEnabled(false);
         popupMenu.add(mnuNoQuickChoice);
      }
      else
      {
         List<ColumnDisplayDefinition> columnsShowingAsImage = _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().getColumnsShowingAsImage();

         if(selectedCellInfo.isExtTableColumnCellSelected())
         {
            ColumnDisplayDefinition selColDisp = selectedCellInfo.getSelectedColumnsDisplayDefinition();

            if(false == columnsShowingAsImage.stream().anyMatch(cd -> cd.matchesByQualifiedName(selColDisp)))
            {
               JCheckBoxMenuItem mnuDisplayAsImage = new JCheckBoxMenuItem(getImageMenuText(selColDisp));
               mnuDisplayAsImage.addActionListener(e1 -> displayAsImage(selColDisp, mnuDisplayAsImage));
               popupMenu.add(mnuDisplayAsImage);
            }
         }
         else if(columnsShowingAsImage.isEmpty())
         {
            JMenuItem mnuNoChoiceFOrCol = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.cannot.offer.display.choice.for.selected.cell"));
            mnuNoChoiceFOrCol.setEnabled(false);
            popupMenu.add(mnuNoChoiceFOrCol);
         }

         for(ColumnDisplayDefinition colDisp : columnsShowingAsImage)
         {
            JCheckBoxMenuItem mnuDisplayAsImage = new JCheckBoxMenuItem(getImageMenuText(colDisp));
            mnuDisplayAsImage.setSelected(true);
            mnuDisplayAsImage.addActionListener(e1 -> displayAsImage(colDisp, mnuDisplayAsImage));
            popupMenu.add(mnuDisplayAsImage);
         }
      }

      popupMenu.addSeparator();

      JMenuItem mnuMore = new JMenuItem(s_stringMgr.getString("ColumnDisplayChoiceAction.more"));
      popupMenu.add(mnuMore);


      popupMenu.show(tabButton, 0, tabButton.getHeight());
   }

   private static String getImageMenuText(ColumnDisplayDefinition selColDisp)
   {
      String colName = ColumnDisplayUtil.getColumnName(selColDisp);
      String imageMenuText = s_stringMgr.getString("ColumnDisplayChoiceAction.display.image", colName, selColDisp.getSqlTypeName());
      return imageMenuText;
   }

   private void onToggleShowCellDetail(JMenuItem chkMnuShowCellDetails)
   {
      _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().setCellDetailVisible(chkMnuShowCellDetails.isSelected());
   }

   private void displayAsImage(ColumnDisplayDefinition colDisp, JCheckBoxMenuItem mnuDisplayAsImage)
   {
      if(mnuDisplayAsImage.isSelected())
      {
         _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().setCellDetailVisible(mnuDisplayAsImage.isSelected());
      }

      _resultTabProvider.getResultTab().getSelectedResultTabsDisplayHandler().displayColumnAsImage(colDisp, mnuDisplayAsImage.isSelected());
   }

   @Override
   public void setSQLPanel(ISQLPanelAPI sqlPanelApi)
   {
      _resultTabProvider.setSQLPanelAPI(sqlPanelApi);
      setEnabled(null != sqlPanelApi);
   }
}
