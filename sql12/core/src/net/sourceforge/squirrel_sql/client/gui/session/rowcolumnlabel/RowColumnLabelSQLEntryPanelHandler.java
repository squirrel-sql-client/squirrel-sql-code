package net.sourceforge.squirrel_sql.client.gui.session.rowcolumnlabel;

import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.BaseSQLTab;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class RowColumnLabelSQLEntryPanelHandler
{
   private ISQLEntryPanel _sqlEntryPanel;

   private MainPanel _mainPanel;

   private CaretListener _caretListener;

   public RowColumnLabelSQLEntryPanelHandler(ISQLEntryPanel sqlEntryPanel, CaretListener caretListener)
   {
      _sqlEntryPanel = sqlEntryPanel;
      _caretListener = caretListener;
      _sqlEntryPanel.addCaretListener(_caretListener);
   }

   public RowColumnLabelSQLEntryPanelHandler(MainPanel mainPanel, CaretListener caretListener)
   {
      _mainPanel = mainPanel;

      _mainPanel.addMainPanelTabSelectionListener(newSelectedMainPanelTab -> onMainTabSelected(newSelectedMainPanelTab));

      _caretListener = caretListener;

      _mainPanel.getMainSQLPanel().getSQLEntryPanel().addCaretListener(_caretListener);
   }

   private void onMainTabSelected(IMainPanelTab newSelectedMainPanelTab)
   {
      if(newSelectedMainPanelTab instanceof BaseSQLTab)
      {
         ISQLEntryPanel sqlEntryPanel = ((BaseSQLTab) newSelectedMainPanelTab).getSQLPanel().getSQLEntryPanel();
         sqlEntryPanel.addCaretListener(_caretListener);

         fireDummyCaretEventToTriggerRowColumnUpdate(sqlEntryPanel);
      }
   }

   private void fireDummyCaretEventToTriggerRowColumnUpdate(ISQLEntryPanel sqlEntryPanel)
   {
      _caretListener.caretUpdate(new CaretEvent(sqlEntryPanel) {
         @Override
         public int getDot()
         {
            throw new IllegalStateException("Should not be called");
         }

         @Override
         public int getMark()
         {
            throw new IllegalStateException("Should not be called");
         }
      });
   }

   public CaretPositionInfo getCaretPositionInfo()
   {
      if (null != _sqlEntryPanel)
      {
         return new CaretPositionInfo(_sqlEntryPanel.getCaretLineNumber(), _sqlEntryPanel.getCaretLinePosition(), _sqlEntryPanel.getCaretPosition());
      }
      else
      {
         SQLPanel sqlPanel = _mainPanel.getSelectedSQLPanel();

         if(null == sqlPanel)
         {
            return null;
         }

         ISQLEntryPanel sqlEntryPanel = sqlPanel.getSQLEntryPanel();
         return new CaretPositionInfo(sqlEntryPanel.getCaretLineNumber(), sqlEntryPanel.getCaretLinePosition(), sqlEntryPanel.getCaretPosition());
      }
   }
}
