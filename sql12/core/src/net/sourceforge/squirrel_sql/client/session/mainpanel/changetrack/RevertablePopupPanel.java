package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RevertablePopupPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RevertablePopupPanel.class);

   JButton btnRevert;
   JButton btnCopy;
   JTextPane txtFormerText;


   public RevertablePopupPanel(String formerText, Font sqlEntryAreaFont)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnRevert = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.REVERT));
      btnRevert.setToolTipText(s_stringMgr.getString("RevertablePopupPanel.revert"));
      add(GUIUtils.styleAsToolbarButton(btnRevert, false, false), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,2,0,0), 0,0);
      btnCopy = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY));
      btnCopy.setToolTipText(s_stringMgr.getString("RevertablePopupPanel.copy"));
      add(GUIUtils.styleAsToolbarButton(btnCopy, false, false), gbc);

      gbc = new GridBagConstraints(0,1,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2,0,0,0), 0,0);

      txtFormerText = new JTextPane();
      txtFormerText.setEditable(false);
      txtFormerText.setBackground(ChangeTrackPanel.GUTTER_COLOR);
      txtFormerText.setFont(sqlEntryAreaFont);
      txtFormerText.setText(formerText);
      add(txtFormerText, gbc);
   }
}
