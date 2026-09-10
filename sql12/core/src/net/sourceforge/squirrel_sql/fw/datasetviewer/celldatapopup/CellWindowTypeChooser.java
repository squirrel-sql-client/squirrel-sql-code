package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class CellWindowTypeChooser
{
   private final CellDataWindow _cellDataWindow;
   private final JPanel _chooserPanel;
   private final JToggleButton _button;

   public CellWindowTypeChooser(CellDataWindow cellDataWindow)
   {
      _cellDataWindow = cellDataWindow;

      _chooserPanel = new JPanel(new GridLayout(1, 1));
      _button = GUIUtils.styleAsToolbarButton(new JToggleButton(CellWindowType.getSelected().getIcon()));
      _button.setToolTipText(CellWindowType.getSelected().getToggleToToolTipText());

      if(CellWindowType.getSelected() == CellWindowType.FRAME)
      {
         _button.setSelected(true);
      }

      _button.addActionListener(e -> onWindowTypeChanged());
      _chooserPanel.add(_button);
   }

   public JPanel getPanel()
   {

      return _chooserPanel;
   }

   private void onWindowTypeChanged()
   {
      CellWindowType newType;
      if(_button.isSelected())
      {
         newType = CellWindowType.FRAME;
      }
      else
      {
         newType = CellWindowType.DIALOG;
      }


      CellWindowType.setSelected(newType);

      CellDataDialogHandler.reopenAsType(_cellDataWindow, newType);

   }
}
