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
   JButton btnRevert;

   public AddedLinesPopupPanel()
   {
      super(new GridLayout());
      btnRevert = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.REVERT));
      btnRevert.setToolTipText(s_stringMgr.getString("RevertablePopupPanel.revert"));
      add(GUIUtils.styleAsToolbarButton(btnRevert, false, false));

   }
}
