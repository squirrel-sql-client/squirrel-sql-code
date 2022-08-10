package net.sourceforge.squirrel_sql.client.gui.db.passwordaccess;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class PasswordInAliasPanel extends JPanel
{
   JPasswordField txtPassword = new JPasswordField();
   JPanel passwordPanel = new JPanel(new GridLayout(1, 1));
   JTextField txtPasswordReadable = new JTextField();
   SmallTabButton<Object> btnCopyPassword = new SmallTabButton<>(null, Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_COPY_PASSWORD));
   SmallTabButton<Object> btnShowPassword = new SmallTabButton<>(null, Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_SHOW_PASSWORD));

   public PasswordInAliasPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      passwordPanel.add(txtPassword);
      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      add(passwordPanel, gbc);

      JPanel pnlIcons = new JPanel(new GridLayout(1, 2));
      pnlIcons.add(btnCopyPassword);
      pnlIcons.add(btnShowPassword);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      add(pnlIcons, gbc);
   }
}
