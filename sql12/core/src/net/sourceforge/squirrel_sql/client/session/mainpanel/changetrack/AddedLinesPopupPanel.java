package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class AddedLinesPopupPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AddedLinesPopupPanel.class);

   public AddedLinesPopupPanel()
   {
      super(new GridLayout());
      JButton btn = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.REVERT));
      btn.setToolTipText(s_stringMgr.getString("RevertablePopupPanel.revert"));
      add(GUIUtils.styleAsToolbarButton(btn, false, false));

      btn.addActionListener(e -> onRevertAdded());
   }

   private void onRevertAdded()
   {
      System.out.println("AddedLinesPopupPanel.onRevertAdded");
   }
}
