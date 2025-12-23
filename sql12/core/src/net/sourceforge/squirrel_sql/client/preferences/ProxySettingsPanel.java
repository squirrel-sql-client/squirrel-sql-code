package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ProxySettingsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProxySettingsPanel.class);

   JComboBox<String> cboAdditionalSettingsNames = new JComboBox<>();

   JCheckBox httpUseProxyChk = new JCheckBox(s_stringMgr.getString("ProxyPreferencesPanel.useproxy"));
   JLabel httpProxyServerLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.server"), JLabel.RIGHT);
   JTextField httpProxyServer = new JTextField();
   JLabel httpProxyPortLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.port"), JLabel.RIGHT);
   JTextField httpProxyPort = new JTextField();
   JLabel httpProxyUserLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.user"), JLabel.RIGHT);
   JTextField httpProxyUser = new JTextField();
   JLabel httpProxyPasswordLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.password"), JLabel.RIGHT);
   JPasswordField httpProxyPassword = new JPasswordField();
   JLabel httpNonProxyHostsLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.noproxyfor"), JLabel.RIGHT);
   JTextField httpNonProxyHosts = new JTextField();

   JCheckBox socksUseProxyChk = new JCheckBox(s_stringMgr.getString("ProxyPreferencesPanel.useproxy"));
   JLabel socksProxyServerLabel = new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.server"), JLabel.RIGHT);
   JTextField socksProxyServer = new JTextField();
   JLabel socksProxyPortLabel =new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.port"), JLabel.RIGHT);
   JTextField socksProxyPort = new JTextField();
   JButton btnAddProxySetting;
   JButton btnRemoveProxySetting;


   public ProxySettingsPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0, 150), 0,0);
      add(createSettingsNamesPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,5, 5), 0,0);
      add(createProxyPanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0, 0), 0,0);
      add(new JPanel(), gbc);
   }

   private JPanel createSettingsNamesPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      JLabel lblProxyCbo = new JLabel(s_stringMgr.getString("ProxyAdditionalSettingsPanel.additional.settings.name"));
      lblProxyCbo.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PROXY));
      ret.add(lblProxyCbo, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      ret.add(cboAdditionalSettingsNames, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,0), 0,0);
      btnAddProxySetting = createIconButton(SquirrelResources.IImageNames.PLUS);
      ret.add(btnAddProxySetting, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,0), 0,0);
      btnRemoveProxySetting = createIconButton(SquirrelResources.IImageNames.MINUS);
      ret.add(btnRemoveProxySetting, gbc);

      return ret;
   }

   private JButton createIconButton(String plus)
   {
      return GUIUtils.styleAsToolbarButton(new JButton(Main.getApplication().getResources().getIcon(plus)), false, true, cboAdditionalSettingsNames.getPreferredSize().height);
   }

   private JPanel createProxyPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(0, 0, 0, 0);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      ret.add(createHTTPPanel(), gbc);

      gbc.insets = new Insets(4, 0, 0, 0);
      ++gbc.gridy;
      ret.add(createSOCKSPanel(), gbc);
      return ret;
   }

   private JPanel createHTTPPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ProxyPreferencesPanel.httpproxy")));

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.gridx = 0;
      gbc.gridy = 0;
      pnl.add(httpUseProxyChk, gbc);

      gbc.fill = GridBagConstraints.HORIZONTAL;
      ++gbc.gridx;
      pnl.add(httpProxyServerLabel, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyPortLabel, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyUserLabel, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyPasswordLabel, gbc);

      ++gbc.gridy;
      pnl.add(httpNonProxyHostsLabel, gbc);

      ++gbc.gridy;
      --gbc.gridx;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      pnl.add(new MultipleLineLabel(s_stringMgr.getString("ProxyPreferencesPane.notes")), gbc);
      gbc.gridwidth = 1;
      ++gbc.gridx;

      ++gbc.gridx;
      gbc.gridy = 0;
      gbc.weightx = 1;
      pnl.add(httpProxyServer, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyPort, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyUser, gbc);

      ++gbc.gridy;
      pnl.add(httpProxyPassword, gbc);

      ++gbc.gridy;
      pnl.add(httpNonProxyHosts, gbc);

      return pnl;
   }

   private JPanel createSOCKSPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ProxyPreferencesPanel.socksproxy")));

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(4, 4, 4, 4);
      gbc.gridx = 0;
      gbc.gridy = 0;
      pnl.add(socksUseProxyChk, gbc);

      gbc.fill = GridBagConstraints.HORIZONTAL;
      ++gbc.gridx;
      pnl.add(socksProxyServerLabel, gbc);

      ++gbc.gridy;
      pnl.add(socksProxyPortLabel, gbc);

      ++gbc.gridx;
      gbc.gridy = 0;
      gbc.weightx = 1;
      pnl.add(socksProxyServer, gbc);

      ++gbc.gridy;
      pnl.add(socksProxyPort, gbc);

      return pnl;
   }


}
