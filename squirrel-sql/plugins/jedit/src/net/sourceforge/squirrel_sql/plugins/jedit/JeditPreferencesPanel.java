package net.sourceforge.squirrel_sql.plugins.jedit;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

/**
 * Global preferences panel for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPreferencesPanel implements IGlobalPreferencesPanel, ISessionPropertiesPanel {
	/** Plugin preferences object. */
	private JeditPreferences _prefs;

	/** Component to display in the Global preferences dialog. */
	private MyPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Current session. Will be <TT>null</TT> if control is being displayed in
	 * Application Perferences dialog.
	 */
	private ISession _session;

	/**
	 * Ctor.
	 *
	 * @param	plugin	The jEdit plugin.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>JeditPlugin</TT> is <TT>null</TT>.
	 */
	public JeditPreferencesPanel(JeditPlugin plugin, JeditPreferences prefs)
			throws IllegalArgumentException {
		super();
		if (plugin == null) {
			throw new IllegalArgumentException("Null JeditPlugin passed");
		}
		if (prefs == null) {
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}
		_prefs = prefs;
		// Create the actual panel that will be displayed in dialog.
		_myPanel = new MyPanel(plugin);
	}

	/**
	 * Panel is being loaded for the Application Preferences. This means that
	 * the settings are for newly created sessions.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *		  if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app) throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_session = null;
		_myPanel.loadData(_app, _session, _prefs);
	}

	/**
	 * Panel is being loaded for the Session Properties. This means that
	 * the settings are for the current session only.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *		  if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app, ISession session)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}

		_app = app;
		_session = session;
		_myPanel.loadData(_app, _session, _prefs);
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent() {
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges() {
		_myPanel.applyChanges();
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return  the title for this panel.
	 */
	public String getTitle() {
		return MyPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint() {
		return MyPanel.i18n.TAB_HINT;
	}

	/**
	 * Component to be displayed in the preferences dialog.
	 */
	private final static class MyPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String TAB_TITLE = "jEdit";
			String TAB_HINT = "jEdit text control usage";
			String ACTIVE = "Use jEdit text control";
			String BLINK_CARET = "Blink Caret";
			String BLOCK_CARET = "Block Caret";
			String BRACKET_HIGHLIGHTING = "Highlight matching brackets";
			String EOL_MARKERS = "End of line markers";
			String HIGHLIGHT_CURRENT_LINE = "Highlight current line";
		}

		private JeditPlugin _plugin;
		private IApplication _app;
		private ISession _session;
		private JeditPreferences _prefs;
		
		private JCheckBox _activeChk = new JCheckBox(i18n.ACTIVE);
		private JCheckBox _eolMarkersChk = new JCheckBox(i18n.EOL_MARKERS);
		private JCheckBox _blockCaretEnabledChk = new JCheckBox(i18n.BLOCK_CARET);
		private JCheckBox _bracketHighlighting = new JCheckBox(i18n.BRACKET_HIGHLIGHTING);
		private JCheckBox _currentLineHighlighting = new JCheckBox(i18n.HIGHLIGHT_CURRENT_LINE);
		private JCheckBox _blinkCaretChk = new JCheckBox(i18n.BLINK_CARET);
		private JLabel _keywordColorLbl1;
		private JLabel _keywordColorLbl2;
		private JLabel _keywordColorLbl3;
		private JButton _keywordColorBtn1;
		private JButton _keywordColorBtn2;
		private JButton _keywordColorBtn3;
		private MyActionListener _actionListener = new MyActionListener();

		MyPanel(JeditPlugin plugin) {
			super();
			_plugin = plugin;
			createUserInterface();
		}

		void loadData(IApplication app, ISession session, JeditPreferences prefs) {
			_app = app;
			_session = session;
			_prefs = prefs;
			
			_activeChk.setSelected(_prefs.getUseJeditTextControl());
			_eolMarkersChk.setSelected(_prefs.getEolMarkers());
			_blockCaretEnabledChk.setSelected(_prefs.isBlockCaretEnabled());
			_bracketHighlighting.setSelected(_prefs.getBracketHighlighting());
			_currentLineHighlighting.setSelected(_prefs.getCurrentLineHighlighting());
			_blinkCaretChk.setSelected(_prefs.getBlinkCaret());
			_keywordColorLbl1.setBackground(new Color(_prefs.getKeyword1RGB()));
			_keywordColorLbl2.setBackground(new Color(_prefs.getKeyword2RGB()));
			_keywordColorLbl3.setBackground(new Color(_prefs.getKeyword3RGB()));
		}

		void applyChanges() {
			_prefs.setUseJeditTextControl(_activeChk.isSelected());
			_prefs.setEolMarkers(_eolMarkersChk.isSelected());
			_prefs.setBlockCaretEnabled(_blockCaretEnabledChk.isSelected());
			_prefs.setBracketHighlighting(_bracketHighlighting.isSelected());
			_prefs.setCurrentLineHighlighting(_currentLineHighlighting.isSelected());
			_prefs.setKeyword1RGB(_keywordColorLbl1.getBackground().getRGB());
			_prefs.setBlinkCaret(_blinkCaretChk.isSelected());
			_prefs.setKeyword2RGB(_keywordColorLbl2.getBackground().getRGB());
			_prefs.setKeyword3RGB(_keywordColorLbl3.getBackground().getRGB());
		}

		private void createUserInterface() {
			final GridBagConstraints gbc = new GridBagConstraints();
			setLayout(new GridBagLayout());

			_keywordColorLbl1 = createColorLabel();
			_keywordColorLbl2 = createColorLabel();
			_keywordColorLbl3 = createColorLabel();

			_keywordColorBtn1 = new ColorButton("Keywords", _keywordColorLbl1);
			_keywordColorBtn2 = new ColorButton("Data Types", _keywordColorLbl2);
			_keywordColorBtn3 = new ColorButton("Functions", _keywordColorLbl3);

			GUIUtils.setJButtonSizesTheSame(new JButton[] {
					_keywordColorBtn1, _keywordColorBtn2, _keywordColorBtn3});

			_activeChk.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					final boolean useJeditControl = ((JCheckBox)evt.getSource()).isSelected();
					_keywordColorBtn1.setEnabled(useJeditControl);
					_keywordColorBtn2.setEnabled(useJeditControl);
					_keywordColorBtn3.setEnabled(useJeditControl);
					_eolMarkersChk.setEnabled(useJeditControl);
					_blockCaretEnabledChk.setEnabled(useJeditControl);
					_bracketHighlighting.setEnabled(useJeditControl);
					_currentLineHighlighting.setEnabled(useJeditControl);
					_blinkCaretChk.setEnabled(useJeditControl);
				}
			});

			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(2, 2, 2, 2);

			gbc.gridx = gbc.gridy = 0;
			add(_activeChk, gbc);

			++gbc.gridy;
			add(_eolMarkersChk, gbc);

			++gbc.gridy;
			add(_blinkCaretChk, gbc);

			++gbc.gridy;
			add(_blockCaretEnabledChk, gbc);

			++gbc.gridy;
			add(_currentLineHighlighting, gbc);

			++gbc.gridy;
			add(_bracketHighlighting, gbc);

			++gbc.gridy;
			add(_keywordColorBtn1, gbc);
			++gbc.gridx;
			add(_keywordColorLbl1, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(_keywordColorBtn2, gbc);
			++gbc.gridx;
			add(_keywordColorLbl2, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(_keywordColorBtn3, gbc);
			++gbc.gridx;
			add(_keywordColorLbl3, gbc);
		}
		
		private JLabel createColorLabel() {
			JLabel lbl = new JLabel();
			lbl.setPreferredSize(new Dimension(75, 25));
			lbl.setBorder(BorderFactory.createLineBorder(Color.white));
			lbl.setOpaque(true);
			return lbl;
		}

		private class ColorButton extends JButton {
			ColorButton(String title, JLabel previewLbl) {
				super(title);
				addActionListener(_actionListener);
				putClientProperty("preview", previewLbl);
			}
		}

		private class MyActionListener implements ActionListener {
			public void actionPerformed(ActionEvent evt) {
				final Object src = evt.getSource();
				if (src instanceof JButton) {
					final JButton btn = (JButton)src;
					final JLabel lbl = (JLabel)btn.getClientProperty("preview");
					final Color color = JColorChooser.showDialog(MyPanel.this, "Select Color", lbl.getBackground());
					if (color != null) {
						lbl.setBackground(color);
					}
				}
			}
		}

	}
}