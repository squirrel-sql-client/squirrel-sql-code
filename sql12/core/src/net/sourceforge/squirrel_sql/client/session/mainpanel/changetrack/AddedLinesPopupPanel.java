package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class AddedLinesPopupPanel extends JPanel
{
   public AddedLinesPopupPanel()
   {
      super(new GridLayout());
      JButton btn = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.REVERT));
      add(GUIUtils.styleAsToolbarButton(btn, false, false));

      btn.addActionListener(e -> onRevertAdded());
   }

   private void onRevertAdded()
   {
      System.out.println("AddedLinesPopupPanel.onRevertAdded");
   }
}
