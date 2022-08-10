package net.sourceforge.squirrel_sql.client.gui.db.passwordaccess;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PasswordAccessPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PasswordAccessPrefsPanel.class);

   JCheckBox chkShowAliasPasswordCopyButton = new JCheckBox(s_stringMgr.getString("PasswordAccessPanel.showAliasPasswordCopyButton"));
   JCheckBox chkShowAliasPasswordShowButton = new JCheckBox(s_stringMgr.getString("PasswordAccessPanel.showAliasPasswordShowButton"));

   public PasswordAccessPrefsPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0);
      MultipleLineLabel lblDesc = new MultipleLineLabel(s_stringMgr.getString("PasswordAccess.description"));
      add(lblDesc, gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      final JPanel pnlCopy = new JPanel(new BorderLayout());
      pnlCopy.add(chkShowAliasPasswordCopyButton, BorderLayout.CENTER);
      pnlCopy.add(new JLabel(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_COPY_PASSWORD)), BorderLayout.EAST);
      add(pnlCopy, gbc);


      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 0, 5), 0, 0);
      final JPanel pnlShow = new JPanel(new BorderLayout());
      pnlShow.add(chkShowAliasPasswordShowButton, BorderLayout.CENTER);
      pnlShow.add(new JLabel(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_SHOW_PASSWORD)), BorderLayout.EAST);
      add(pnlShow, gbc);


//      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
//      ret.add(new JPanel(), gbc);

      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("PasswordAccessPanel.password.access")));

   }
}
