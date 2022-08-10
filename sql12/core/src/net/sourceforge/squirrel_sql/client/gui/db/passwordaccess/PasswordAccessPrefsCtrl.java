package net.sourceforge.squirrel_sql.client.gui.db.passwordaccess;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import javax.swing.JPanel;

public class PasswordAccessPrefsCtrl
{
   private final PasswordAccessPrefsPanel _panel;

   public PasswordAccessPrefsCtrl()
   {
      _panel = new PasswordAccessPrefsPanel();
   }

   public void loadData(SquirrelPreferences prefs)
   {
      _panel.chkShowAliasPasswordCopyButton.setSelected(prefs.getShowAliasPasswordCopyButton());
      _panel.chkShowAliasPasswordShowButton.setSelected(prefs.getShowAliasPasswordShowButton());
   }

   public void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setShowAliasPasswordCopyButton(_panel.chkShowAliasPasswordCopyButton.isSelected());
      prefs.setShowAliasPasswordShowButton(_panel.chkShowAliasPasswordShowButton.isSelected());
   }

   public JPanel getPanel()
   {
      return _panel;
   }
}
