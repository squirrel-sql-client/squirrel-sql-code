package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.Window;
import java.util.List;
import javax.swing.JOptionPane;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class ProxySettingsAddCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProxySettingsAddCtrl.class);

   private ProxySettingsAddDlg _dlg;
   private boolean _ok;

   public ProxySettingsAddCtrl(List<String> listOfExistingAdditionalNames, Window parent)
   {
      _dlg = new ProxySettingsAddDlg(parent);

      _dlg.btnOK.addActionListener(e -> onOk(listOfExistingAdditionalNames));
      _dlg.btnCancel.addActionListener(e -> close());

      GUIUtils.forceFocus(_dlg.txtName);
      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 320,120);
      _dlg.setVisible(true);
   }

   private void onOk(List<String> listOfExistingAdditionalNames)
   {
      if(StringUtils.isBlank(_dlg.txtName.getText()))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("ProxySettingsAddCtrl.proxy.settings.name.cannot.be.empty"));
         return;
      }

      if(   StringUtils.equalsIgnoreCase(_dlg.txtName.getText(), ProxyPreferenceTabComponent.DEFAULT_PROXY_SETTINGS_NAME)
         || listOfExistingAdditionalNames.stream().anyMatch(n -> StringUtils.equalsIgnoreCase(n, _dlg.txtName.getText()))
      )
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("ProxySettingsAddCtrl.proxy.settings.name.already.exists"));
         return;
      }

      _ok = true;

      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getNewSettingsName()
   {
      return _dlg.txtName.getText();
   }
}
