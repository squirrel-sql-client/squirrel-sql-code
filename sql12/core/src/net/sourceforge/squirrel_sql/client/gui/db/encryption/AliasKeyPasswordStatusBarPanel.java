package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasKeyPasswordStatusBarPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasKeyPasswordStatusBarPanel.class);
   private JLabel _lblState;

   public AliasKeyPasswordStatusBarPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;
      String tooltip = s_stringMgr.getString("AliasKeyPasswordStatusBarPanel.tooltip") ;


      gbc = new GridBagConstraints(0,0,1,1, 1,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      _lblState = new JLabel();
      GUIUtils.setPreferredWidth(_lblState, 150);
      GUIUtils.setMinimumWidth(_lblState, 150);
      _lblState.setToolTipText(tooltip);
      add(_lblState, gbc);

      gbc = new GridBagConstraints(1,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ImageIcon icon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PASSWORD_12X12);
      SmallTabButton btnOpenAliasKeyPasswordDialog = new SmallTabButton(tooltip, icon);
      btnOpenAliasKeyPasswordDialog.addActionListener(e -> Main.getApplication().getActionCollection().get(AliasKeyPasswordEncryptionAction.class).actionPerformed(e));
      add(btnOpenAliasKeyPasswordDialog, gbc);

      updatePanel();
   }

   public void updatePanel()
   {
      if(false == Main.getApplication().getAliasKeyPasswordManager().isUseKeyPassword())
      {
         _lblState.setText(s_stringMgr.getString("AliasKeyPasswordStatusBarPanel.status.unused"));
         return;
      }


      if(Main.getApplication().getAliasKeyPasswordManager().isLoggedIn())
      {
         _lblState.setText(s_stringMgr.getString("AliasKeyPasswordStatusBarPanel.status.logged.in"));
      }
      else
      {
         _lblState.setText(s_stringMgr.getString("AliasKeyPasswordStatusBarPanel.status.logged.out"));
      }
   }
}
