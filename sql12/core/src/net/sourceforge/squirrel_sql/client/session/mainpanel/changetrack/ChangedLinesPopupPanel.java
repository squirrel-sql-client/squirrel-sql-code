package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class ChangedLinesPopupPanel extends JPanel
{
   public ChangedLinesPopupPanel(String formerText, Font sqlEntryAreaFont)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      JButton btnRevert = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.REVERT));
      add(GUIUtils.styleAsToolbarButton(btnRevert, false, false), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,2,0,0), 0,0);
      JButton btnCopy = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY));
      add(GUIUtils.styleAsToolbarButton(btnCopy, false, false), gbc);

      gbc = new GridBagConstraints(0,1,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2,0,0,0), 0,0);
      MultipleLineLabel lblFormerText = new MultipleLineLabel(formerText);
      lblFormerText.setBackground(ChangeTrackPanel.GUTTER_COLOR);
      lblFormerText.setFont(sqlEntryAreaFont);
      lblFormerText.setLineWrap(false);
      //lblFormerText.setWrapStyleWord(false);
      add(lblFormerText, gbc);

      //setBorder(BorderFactory.createLineBorder(Color.lightGray));


      btnRevert.addActionListener(e -> onRevertChanged());
      btnCopy.addActionListener(e -> onCopyFormer());

   }

   private void onCopyFormer()
   {
      System.out.println("ChangedLinesPopupPanel.onCopyFormer");
   }

   private void onRevertChanged()
   {
      System.out.println("ChangedLinesPopupPanel.onRevertChanged");
   }
}
