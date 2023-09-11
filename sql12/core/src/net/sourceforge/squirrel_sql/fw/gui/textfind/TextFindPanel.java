package net.sourceforge.squirrel_sql.fw.gui.textfind;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class TextFindPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextFindPanel.class);

   final JComboBox cboTextToFind = new JComboBox();
   final JButton btnDown;
   final JButton btnUp;
   final JButton btnConfig;
   final JButton btnHide;

   public TextFindPanel(boolean permanent)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2,5,2,0),0,0 );
      add(cboTextToFind, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,5,2,0),0,0 );
      btnDown = new JButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.TABLE_DESCENDING));
      btnDown.setToolTipText(s_stringMgr.getString("TextFindPanel.findNext"));
      btnDown.setBorder(BorderFactory.createEtchedBorder());
      btnDown.setFocusable(false);
      add(btnDown, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,5,2,0),0,0 );
      btnUp = new JButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.TABLE_ASCENDING));
      btnUp.setToolTipText(s_stringMgr.getString("TextSetFindPanel.findPrevious"));
      btnUp.setBorder(BorderFactory.createEtchedBorder());
      btnUp.setFocusable(false);
      add(btnUp, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,5,2,0),0,0 );
      btnConfig = new JButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.CONFIGURE));
      btnConfig.setToolTipText(s_stringMgr.getString("TextSetFindPanel.configure"));
      btnConfig.setBorder(BorderFactory.createEtchedBorder());
      btnConfig.setFocusable(false);
      add(btnConfig, gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,5,2,5),0,0 );
      btnHide = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.CLOSE));
      if (permanent)
      {
         btnHide.setToolTipText(s_stringMgr.getString("TextSetFindPanel.clear.search.highlight"));
      }
      else
      {
         btnHide.setToolTipText(s_stringMgr.getString("TextSetFindPanel.hide"));
      }
      btnHide.setBorder(BorderFactory.createEtchedBorder());
      btnHide.setFocusable(false);
      add(btnHide, gbc);
   }
}
