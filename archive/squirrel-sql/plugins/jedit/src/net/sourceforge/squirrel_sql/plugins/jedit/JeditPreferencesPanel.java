package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

/**
 * New Session and Current Session preferences panel for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPreferencesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(JeditPreferencesPanel.class);

	/** Plugin preferences object. */
	private JeditPreferences _prefs;

	/** Component to display in the preferences dialog. */
	private MyPanel _myPanel;

	/**
	 * Ctor.
	 *
	 * @param	prefs	The preferences to be maintained.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>prefs</TT> is <TT>null</TT>.
	 */
	public JeditPreferencesPanel(JeditPreferences prefs)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}
		_prefs = prefs;

		// Create the actual panel that will be displayed in dialog.
		_myPanel = new MyPanel();
	}

	/**
	 * Panel is being loaded for the Application Preferences. This means that
	 * the settings are for newly created sessions.
	 *
	 * @param   app	 Application API.
	 */
	public void initialize(IApplication app)
	{
		_myPanel.loadData(_prefs);
	}

	/**
	 * Panel is being loaded for the Session Properties. This means that
	 * the settings are for the current session only.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app, ISession session)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_myPanel.loadData(_prefs);
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return  the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges(_prefs);
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return  the title for this panel.
	 */
	public String getTitle()
	{
		return MyPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return  the hint for this panel.
	 */
	public String getHint()
	{
		return MyPanel.i18n.TAB_HINT;
	}

	/**
	 * Component to be displayed in the preferences dialog.
	 */
	private final static class MyPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String TAB_TITLE = "jEdit";
			String TAB_HINT = "jEdit text control usage";
			String ACTIVE = "Use jEdit text control";
			String BLINK_CARET = "Blink Caret";
			String BLOCK_CARET = "Block Caret";
			String BRACKET_HIGHLIGHTING = "Highlight matching brackets";
			String EOL_MARKERS = "End of line markers";
			String HIGHLIGHT_CURRENT_LINE = "Highlight current line";
		}

		private JCheckBox _activeChk = new JCheckBox(i18n.ACTIVE);
		private JCheckBox _eolMarkersChk = new JCheckBox(i18n.EOL_MARKERS);
		private JCheckBox _blockCaretEnabledChk =
			new JCheckBox(i18n.BLOCK_CARET);
		private JCheckBox _bracketHighlighting =
			new JCheckBox(i18n.BRACKET_HIGHLIGHTING);
		private JCheckBox _currentLineHighlighting =
			new JCheckBox(i18n.HIGHLIGHT_CURRENT_LINE);
		private JCheckBox _blinkCaretChk = new JCheckBox(i18n.BLINK_CARET);

		private ColorButtonListener _colorBtnActionListener =
			new ColorButtonListener();

		private JLabel _keywordColorLbl1 = createColorLabel();
		private JLabel _keywordColorLbl2 = createColorLabel();
		private JLabel _keywordColorLbl3 = createColorLabel();
		private JLabel _tableColorLbl = createColorLabel();
		private JLabel _columnColorLbl = createColorLabel();
		private JLabel _caretColorLbl = createColorLabel();
		private JLabel _selectionColorLbl = createColorLabel();
		private JLabel _lineHighlightColorLbl = createColorLabel();
		private JLabel _eolMarkerColorLbl = createColorLabel();
		private JLabel _bracketHighlightColorLbl = createColorLabel();

		private JButton _keywordColorBtn1 =
			new ColorButton("Keywords", _keywordColorLbl1);
		private JButton _keywordColorBtn2 =
			new ColorButton("Data Types", _keywordColorLbl2);
		private JButton _keywordColorBtn3 =
			new ColorButton("Functions", _keywordColorLbl3);
		private JButton _tableColorBtn =
			new ColorButton("Tables", _tableColorLbl);
		private JButton _columnColorBtn =
			new ColorButton("Columns", _columnColorLbl);
		private JButton _caretColorBtn =
			new ColorButton("Caret", _caretColorLbl);
		private JButton _selectionColorBtn =
			new ColorButton("Selection", _selectionColorLbl);
		private JButton _lineHighlightColorBtn =
			new ColorButton("Line Highlight", _lineHighlightColorLbl);
		private JButton _eolMarkerColorBtn =
			new ColorButton("EOL Markers", _eolMarkerColorLbl);
		private JButton _bracketHighlightColorBtn =
			new ColorButton("Bracket Highlight", _bracketHighlightColorLbl);

		MyPanel()
		{
			super();
			createUserInterface();
		}

		void loadData(JeditPreferences prefs)
		{
			_activeChk.setSelected(prefs.getUseJeditTextControl());
			_eolMarkersChk.setSelected(prefs.getEOLMarkers());
			_blockCaretEnabledChk.setSelected(prefs.isBlockCaretEnabled());
			_bracketHighlighting.setSelected(prefs.getBracketHighlighting());
			_currentLineHighlighting.setSelected(
				prefs.getCurrentLineHighlighting());
			_blinkCaretChk.setSelected(prefs.getBlinkCaret());

			_keywordColorLbl1.setBackground(new Color(prefs.getKeyword1RGB()));
			_keywordColorLbl2.setBackground(new Color(prefs.getKeyword2RGB()));
			_keywordColorLbl3.setBackground(new Color(prefs.getKeyword3RGB()));
			_tableColorLbl.setBackground(new Color(prefs.getTableRGB()));
			_columnColorLbl.setBackground(new Color(prefs.getColumnRGB()));
			_caretColorLbl.setBackground(new Color(prefs.getCaretRGB()));
			_selectionColorLbl.setBackground(
				new Color(prefs.getSelectionRGB()));
			_lineHighlightColorLbl.setBackground(
				new Color(prefs.getLineHighlightRGB()));
			_eolMarkerColorLbl.setBackground(
				new Color(prefs.getEOLMarkerRGB()));
			_bracketHighlightColorLbl.setBackground(
				new Color(prefs.getBracketHighlightRGB()));

			updateControlStatus();
		}

		void applyChanges(JeditPreferences prefs)
		{
			prefs.setUseJeditTextControl(_activeChk.isSelected());
			prefs.setEOLMarkers(_eolMarkersChk.isSelected());
			prefs.setBlockCaretEnabled(_blockCaretEnabledChk.isSelected());
			prefs.setBracketHighlighting(_bracketHighlighting.isSelected());
			prefs.setCurrentLineHighlighting(_currentLineHighlighting.isSelected());
			prefs.setBlinkCaret(_blinkCaretChk.isSelected());

			prefs.setKeyword1RGB(getLabelRGB(_keywordColorLbl1));
			prefs.setKeyword2RGB(getLabelRGB(_keywordColorLbl2));
			prefs.setKeyword3RGB(getLabelRGB(_keywordColorLbl3));
			prefs.setTableRGB(getLabelRGB(_tableColorLbl));
			prefs.setColumnRGB(getLabelRGB(_columnColorLbl));
			prefs.setCaretRGB(getLabelRGB(_caretColorLbl));
			prefs.setSelectionRGB(getLabelRGB(_selectionColorLbl));
			prefs.setLineHighlightRGB(getLabelRGB(_lineHighlightColorLbl));
			prefs.setEOLMarkerRGB(getLabelRGB(_eolMarkerColorLbl));
			prefs.setBracketHighlightRGB(getLabelRGB(_bracketHighlightColorLbl));
		}

		private int getLabelRGB(JLabel lbl)
		{
			return lbl.getBackground().getRGB();
		}

		private void updateControlStatus()
		{
			final boolean useJeditControl = _activeChk.isSelected();
			_keywordColorBtn1.setEnabled(useJeditControl);
			_keywordColorBtn2.setEnabled(useJeditControl);
			_keywordColorBtn3.setEnabled(useJeditControl);
			_tableColorBtn.setEnabled(useJeditControl);
			_columnColorBtn.setEnabled(useJeditControl);
			_caretColorBtn.setEnabled(useJeditControl);
			_selectionColorBtn.setEnabled(useJeditControl);
			_lineHighlightColorBtn.setEnabled(useJeditControl);
			_eolMarkerColorBtn.setEnabled(useJeditControl);
			_bracketHighlightColorBtn.setEnabled(useJeditControl);
			_eolMarkersChk.setEnabled(useJeditControl);
			_blockCaretEnabledChk.setEnabled(useJeditControl);
			_bracketHighlighting.setEnabled(useJeditControl);
			_currentLineHighlighting.setEnabled(useJeditControl);
			_blinkCaretChk.setEnabled(useJeditControl);
		}

		private void createUserInterface()
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			_activeChk.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					updateControlStatus();
				}
			});

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(_activeChk, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(createGeneralPanel(), gbc);

			++gbc.gridy;
			add(createColorPanel(), gbc);
		}

		private JPanel createGeneralPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("General"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_eolMarkersChk, gbc);

			++gbc.gridy;
			pnl.add(_blinkCaretChk, gbc);

			++gbc.gridx;
			pnl.add(_blockCaretEnabledChk, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(_currentLineHighlighting, gbc);

			++gbc.gridx;
			pnl.add(_bracketHighlighting, gbc);

			return pnl;
		}

		private JPanel createColorPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Colors"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			GUIUtils.setJButtonSizesTheSame(
				new JButton[] {
					_keywordColorBtn1,
					_keywordColorBtn2,
					_keywordColorBtn3 });

			// Col1
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_keywordColorBtn1, gbc);

			++gbc.gridy;
			pnl.add(_keywordColorBtn3, gbc);

			++gbc.gridy;
			pnl.add(_columnColorBtn, gbc);

			++gbc.gridy;
			pnl.add(_selectionColorBtn, gbc);

			++gbc.gridy;
			pnl.add(_eolMarkerColorBtn, gbc);

			// Col2
			gbc.fill = gbc.HORIZONTAL;
			gbc.weightx = 0.5;

			gbc.gridy = 0;
			++gbc.gridx;
			pnl.add(_keywordColorLbl1, gbc);

			++gbc.gridy;
			pnl.add(_keywordColorLbl3, gbc);

			++gbc.gridy;
			pnl.add(_columnColorLbl, gbc);

			++gbc.gridy;
			pnl.add(_selectionColorLbl, gbc);

			++gbc.gridy;
			pnl.add(_eolMarkerColorLbl, gbc);

			// Col3
			//			gbc.fill = 0;
			gbc.weightx = 0;

			gbc.gridy = 0;
			++gbc.gridx;
			pnl.add(_keywordColorBtn2, gbc);

			++gbc.gridy;
			pnl.add(_tableColorBtn, gbc);

			++gbc.gridy;
			pnl.add(_caretColorBtn, gbc);

			++gbc.gridy;
			pnl.add(_lineHighlightColorBtn, gbc);

			++gbc.gridy;
			pnl.add(_bracketHighlightColorBtn, gbc);

			// Col4
			gbc.fill = gbc.HORIZONTAL;
			gbc.weightx = 0.5;

			gbc.gridy = 0;
			++gbc.gridx;
			pnl.add(_keywordColorLbl2, gbc);

			++gbc.gridy;
			pnl.add(_tableColorLbl, gbc);

			++gbc.gridy;
			pnl.add(_caretColorLbl, gbc);

			++gbc.gridy;
			pnl.add(_lineHighlightColorLbl, gbc);

			++gbc.gridy;
			pnl.add(_bracketHighlightColorLbl, gbc);

			return pnl;
		}

		private JLabel createColorLabel()
		{
			JLabel lbl = new JLabel(" ");
			lbl.setPreferredSize(new Dimension(75, 25));
			lbl.setBorder(BorderFactory.createLineBorder(Color.white));
			lbl.setOpaque(true);
			return lbl;
		}

		private class ColorButton extends JButton
		{
			JLabel _previewLbl;
			ColorButton(String title, JLabel previewLbl)
			{
				super(title);
				_previewLbl = previewLbl;
				addActionListener(_colorBtnActionListener);
			}
		}

		private class ColorButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				final Object src = evt.getSource();
				if (src instanceof ColorButton)
				{
					final ColorButton btn = (ColorButton) src;
					final Color color =
						JColorChooser.showDialog(
							MyPanel.this,
							"Select Color",
							btn._previewLbl.getBackground());
					if (color != null)
					{
						btn._previewLbl.setBackground(color);
					}
				}
			}
		}

	}
}
