package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class ProxyPreferencesPanel implements IGlobalPreferencesPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProxyPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private MyPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Default ctor.
	 */
	public ProxyPreferencesPanel()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		((MyPanel)getPanelComponent()).loadData(_app, _app.getSquirrelPreferences());
	}

   public void uninitialize(IApplication app)
   {
      
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new MyPanel();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return s_stringMgr.getString("ProxyPreferencesPanel.title");
	}

	public String getHint()
	{
		return s_stringMgr.getString("ProxyPreferencesPanel.hint");
	}

	private static final class MyPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private JCheckBox _httpUseProxyChk = new JCheckBox(s_stringMgr.getString("ProxyPreferencesPanel.useproxy"));
		private JLabel _httpProxyServerLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.server"), JLabel.RIGHT);
		private JTextField _httpProxyServer = new JTextField();
		private JLabel _httpProxyPortLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.port"), JLabel.RIGHT);
		private JTextField _httpProxyPort = new JTextField();
		private JLabel _httpProxyUserLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.user"), JLabel.RIGHT);
		private JTextField _httpProxyUser = new JTextField();
		private JLabel _httpProxyPasswordLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.password"), JLabel.RIGHT);		
		private JPasswordField _httpProxyPassword = new JPasswordField();
		private JLabel _httpNonProxyHostsLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.noproxyfor"), JLabel.RIGHT);
		private JTextField _httpNonProxyHosts = new JTextField();
		private JCheckBox _socksUseProxyChk = new JCheckBox(s_stringMgr.getString("ProxyPreferencesPanel.useproxy"));
		private JLabel _socksProxyServerLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.server"), JLabel.RIGHT);
		private JTextField _socksProxyServer = new JTextField();
		private JLabel _socksProxyPortLabel = 
			new JLabel(s_stringMgr.getString("ProxyPreferencesPanel.port"), JLabel.RIGHT);
		private JTextField _socksProxyPort = new JTextField();
		

		MyPanel()
		{
			super(new GridBagLayout());
			createUserInterface();
		}

		void loadData(IApplication app, SquirrelPreferences prefs)
		{
			final IProxySettings proxySettings = prefs.getProxySettings();

			_httpUseProxyChk.setSelected(proxySettings.getHttpUseProxy());
			_httpProxyServer.setText(proxySettings.getHttpProxyServer());
			_httpProxyPort.setText(proxySettings.getHttpProxyPort());
			_httpNonProxyHosts.setText(proxySettings.getHttpNonProxyHosts());
			_httpProxyUser.setText(proxySettings.getHttpProxyUser());
			_httpProxyPassword.setText(proxySettings.getHttpProxyPassword());

			_socksUseProxyChk.setSelected(proxySettings.getSocksUseProxy());
			_socksProxyServer.setText(proxySettings.getSocksProxyServer());
			_socksProxyPort.setText(proxySettings.getSocksProxyPort());

			updateControlStatus();
		}

		void applyChanges(SquirrelPreferences prefs)
		{
			final ProxySettings proxySettings = new ProxySettings();

			proxySettings.setHttpUseProxy(_httpUseProxyChk.isSelected());
			proxySettings.setHttpProxyServer(_httpProxyServer.getText());
			proxySettings.setHttpProxyPort(_httpProxyPort.getText());
			proxySettings.setHttpNonProxyHosts(_httpNonProxyHosts.getText());
			proxySettings.setHttpProxyUser(_httpProxyUser.getText());

			String password = new String(_httpProxyPassword.getPassword());
			proxySettings.setHttpProxyPassword(password);

			proxySettings.setSocksUseProxy(_socksUseProxyChk.isSelected());
			proxySettings.setSocksProxyServer(_socksProxyServer.getText());
			proxySettings.setSocksProxyPort(_socksProxyPort.getText());

			prefs.setProxySettings(proxySettings);
		}

		private void updateControlStatus()
		{
			final boolean http = _httpUseProxyChk.isSelected();
			_httpProxyServerLabel.setEnabled(http);
			_httpProxyServer.setEnabled(http);
			_httpProxyPortLabel.setEnabled(http);
			_httpProxyPort.setEnabled(http);
			_httpNonProxyHostsLabel.setEnabled(http);
			_httpNonProxyHosts.setEnabled(http);
			_httpProxyUserLabel.setEnabled(http);
			_httpProxyUser.setEnabled(http);
			_httpProxyPasswordLabel.setEnabled(http);
			_httpProxyPassword.setEnabled(http);

			final boolean socks = _socksUseProxyChk.isSelected();
			_socksProxyServerLabel.setEnabled(socks);
			_socksProxyServer.setEnabled(socks);
			_socksProxyPortLabel.setEnabled(socks);
			_socksProxyPort.setEnabled(socks);
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			add(createHTTPPanel(), gbc);
			++gbc.gridy;
			add(createSOCKSPanel(), gbc);

			final ActionListener lis = new MyActionHandler();
			_httpUseProxyChk.addActionListener(lis);
			_socksUseProxyChk.addActionListener(lis);
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
			pnl.add(_httpUseProxyChk, gbc);

			gbc.fill = GridBagConstraints.HORIZONTAL;
			++gbc.gridx;
			pnl.add(_httpProxyServerLabel, gbc);
			
			++gbc.gridy;		
			pnl.add(_httpProxyPortLabel, gbc);

			++gbc.gridy;
			pnl.add(_httpProxyUserLabel, gbc);

			++gbc.gridy;
			pnl.add(_httpProxyPasswordLabel, gbc);

			++gbc.gridy;
			pnl.add(_httpNonProxyHostsLabel, gbc);

			++gbc.gridy;
			--gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new MultipleLineLabel(s_stringMgr.getString("ProxyPreferencesPane.notes")), gbc);
			gbc.gridwidth = 1;
			++gbc.gridx;

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnl.add(_httpProxyServer, gbc);

			++gbc.gridy;
			pnl.add(_httpProxyPort, gbc);

			++gbc.gridy;
			pnl.add(_httpProxyUser, gbc);

			++gbc.gridy;
			pnl.add(_httpProxyPassword, gbc);

			++gbc.gridy;
			pnl.add(_httpNonProxyHosts, gbc);

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
			pnl.add(_socksUseProxyChk, gbc);

			gbc.fill = GridBagConstraints.HORIZONTAL;
			++gbc.gridx;		
			pnl.add(_socksProxyServerLabel, gbc);

			++gbc.gridy;
			pnl.add(_socksProxyPortLabel, gbc);

			++gbc.gridx;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnl.add(_socksProxyServer, gbc);

			++gbc.gridy;
			pnl.add(_socksProxyPort, gbc);

			return pnl;
		}

		private final class MyActionHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateControlStatus();
			}
		}
	}
}
