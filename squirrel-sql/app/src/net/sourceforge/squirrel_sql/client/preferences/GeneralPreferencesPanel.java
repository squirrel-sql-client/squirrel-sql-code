package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class GeneralPreferencesPanel implements IGlobalPreferencesPanel {
	private MyPanel _myPanel = new MyPanel();

	private IApplication _app;


	public GeneralPreferencesPanel() {
		super();
	}

	public void initialize(IApplication app)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		_myPanel.loadData(_app.getSquirrelPreferences());
	}

	public Component getPanelComponent() {
		return _myPanel;
	}

	public void applyChanges() {
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle() {
		return MyPanel.i18n.TAB_TITLE;
	}

	public String getHint() {
		return MyPanel.i18n.TAB_HINT;
	}

	private static final class MyPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String LOGIN_TIMEOUT = "Login Timeout (Seconds):";
			String SHOW_CONTENTS = "Show Window Contents While Dragging";
			String SHOW_TOOLTIPS = "Show Tooltips";
			String TAB_HINT = "General";
			String TAB_TITLE = "General";
		}

		private JCheckBox _showContents = new JCheckBox(i18n.SHOW_CONTENTS);
		private JCheckBox _showToolTips = new JCheckBox(i18n.SHOW_TOOLTIPS);
		private IntegerField _loginTimeout = new IntegerField();

		MyPanel() {
			super();
			createUserInterface();
		}

		void loadData(SquirrelPreferences prefs) {
			_showContents.setSelected(prefs.getShowContentsWhenDragging());
			_showToolTips.setSelected(prefs.getShowToolTips());
			_loginTimeout.setInt(prefs.getLoginTimeout());
		}

		void applyChanges(SquirrelPreferences prefs) {
			prefs.setShowContentsWhenDragging(_showContents.isSelected());
			prefs.setShowToolTips(_showToolTips.isSelected());
			prefs.setLoginTimeout(_loginTimeout.getInt());
		}

		private void createUserInterface() {
			_loginTimeout.setColumns(4);

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createAppearancePanel(), gbc);
			++gbc.gridy;
			add(createSQLPanel(), gbc);
		}

		private JPanel createAppearancePanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Appearance"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_showContents, gbc);
			++gbc.gridy;
			pnl.add(_showToolTips, gbc);
			
			return pnl;
		}

		private JPanel createSQLPanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(i18n.LOGIN_TIMEOUT), gbc);

			++gbc.gridx;
			pnl.add(_loginTimeout, gbc);
			
			return pnl;
		}
	}
}
