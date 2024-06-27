package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

class CellDataTextAreaDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataTextAreaDialog.class);

   public CellDataTextAreaDialog(Component comp, String columnName, ColumnDisplayDefinition colDef,
                                 Object value, int row, int col,
                                 boolean isModelEditable, JTable table)
   {

      super(SwingUtilities.windowForComponent(comp), s_stringMgr.getString("cellDataPopup.valueofColumn", columnName));
      CellDataColumnDataPopupPanel popup = new CellDataColumnDataPopupPanel(value, colDef, isModelEditable);
      popup.setCellDataUpdateInfo(new CellDataUpdateInfo(row, col, table, this));
      setContentPane(popup);

      GUIUtils.enableCloseByEscape(this);
   }

}
