package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import org.gjt.sp.jedit.syntax.JEditTextArea;
import org.gjt.sp.jedit.syntax.SyntaxDocument;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.omg.CORBA._PolicyStub;

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
	/** Defines indices in the Styles list for the individual styles. */
	interface IStylesListIndices
	{
		int KEYWORDS = 0;
		int DATA_TYPES = 1;
		int FUNCTIONS = 2;
		int TABLES = 3;
		int COLUMNS = 4;
		int COMMENTS = 5;
		int LITERALS = 6;
		int OPERATORS = 7;
		int LABELS = 8;
		int OTHER = 9;

		int LIST_SIZE = 10;
	}

	/** Titles for each style in the styles list. */
	private final static String[] s_styleTitles = new String[]
	{
		"Keywords",
		"Data Types",
		"Functions",
		"Tables",
		"Columns",
		"Comments",
		"Literals",
		"Operators",
		"Labels",
		"Other",
	};

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(JeditPreferencesPanel.class);

	/** Plugin preferences object. */
	private final JeditPreferences _prefs;

	/** Component to display in the preferences dialog. */
	private final MyPanel _myPanel;

	/**
	 * Ctor.
	 *
	 * @param	prefs	The preferences to be maintained.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>prefs</TT> is <TT>null</TT>.
	 */
	public JeditPreferencesPanel(JeditPreferences prefs, JeditPluginResources rsrc)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}
		_prefs = prefs;

		// Create the actual panel that will be displayed in dialog.
		_myPanel = new MyPanel(prefs, rsrc);
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
			String BLINK_CARET = "Blink";
			String BLOCK_CARET = "Block";
			String BRACKET_HIGHLIGHTING = "Highlight matching brackets";
			String EOL_MARKERS = "End of line markers";
			String HIGHLIGHT_CURRENT_LINE = "Highlight current line";
		}

		private final JCheckBox _activeChk = new JCheckBox(i18n.ACTIVE);
		private final JCheckBox _blockCaretEnabledChk = new JCheckBox(i18n.BLOCK_CARET);
		private final JCheckBox _blinkCaretChk = new JCheckBox(i18n.BLINK_CARET);

		private StylesListSelectionListener _listLis;

		private ColorSelector _caretColorSelector;
		private ColorSelector _selectionColorSelector;

		private ColorAttributePanel _eolAttributesPnl;
		private ColorAttributePanel _currentLineAttributesPnl;
		private ColorAttributePanel _bracketAttributesPnl;
		private ColorAttributePanel _lineNumberAttributesPnl;

		private final StylesList _stylesList = new StylesList();

		private StyleMaintenancePanel _styleMaintPnl;

//		private PreviewTextArea _previewtextArea;

		MyPanel(JeditPreferences prefs, JeditPluginResources rsrc)
		{
			super();
			createUserInterface(prefs, rsrc);
		}

		/**
		 * Component has been added to its parent so setup listeners etc.
		 */
		public void addNotify()
		{
			super.addNotify();

			if (_listLis == null)
			{
				_listLis = new StylesListSelectionListener();
				_stylesList.addListSelectionListener(_listLis);
			}
		}

		/**
		 * Component has been removed from its parent so remove listeners etc.
		 */
		public void removeNotify()
		{
			super.removeNotify();
			if (_listLis != null)
			{
				_stylesList.removeListSelectionListener(_listLis);
				_listLis = null;
			}
		}

		void loadData(JeditPreferences prefs)
		{
			_activeChk.setSelected(prefs.getUseJeditTextControl());
			_eolAttributesPnl.setAttributeSelected(prefs.getEOLMarkers());
			_blockCaretEnabledChk.setSelected(prefs.isBlockCaretEnabled());
			_blinkCaretChk.setSelected(prefs.getBlinkCaret());

			_caretColorSelector.setColor(new Color(prefs.getCaretRGB()));
			_selectionColorSelector.setColor(new Color(prefs.getSelectionRGB()));

			_eolAttributesPnl.setAttributeColor(new Color(prefs.getEOLMarkerRGB()));
			_eolAttributesPnl.setAttributeSelected(prefs.getEOLMarkers());
			_bracketAttributesPnl.setAttributeColor(new Color(prefs.getBracketHighlightRGB()));
			_bracketAttributesPnl.setAttributeSelected(prefs.getBracketHighlighting());
			_currentLineAttributesPnl.setAttributeColor(new Color(prefs.getCurrentLineHighlightRGB()));
			_currentLineAttributesPnl.setAttributeSelected(prefs.getCurrentLineHighlighting());
			_lineNumberAttributesPnl.setAttributeColor(new Color(prefs.getLineNumberRGB()));
			_lineNumberAttributesPnl.setAttributeSelected(prefs.getShowLineNumbers());

			_stylesList.loadData(prefs);
			_styleMaintPnl.setStyle(_stylesList.getSelectedSyntaxStyle());

			updateControlStatus();
		}

		void applyChanges(JeditPreferences prefs)
		{
			prefs.setUseJeditTextControl(_activeChk.isSelected());
			prefs.setEOLMarkers(_eolAttributesPnl.isAttributeSelected());
			prefs.setBlockCaretEnabled(_blockCaretEnabledChk.isSelected());
			prefs.setBracketHighlighting(_bracketAttributesPnl.isAttributeSelected());
			prefs.setCurrentLineHighlighting(_currentLineAttributesPnl.isAttributeSelected());
			prefs.setBlinkCaret(_blinkCaretChk.isSelected());
			prefs.setShowLineNumbers(_lineNumberAttributesPnl.isAttributeSelected());

			prefs.setCaretRGB(_caretColorSelector.getColor().getRGB());
			prefs.setSelectionRGB(_selectionColorSelector.getColor().getRGB());
			prefs.setCurrentLineHighlightRGB(_currentLineAttributesPnl.getAttributeColor().getRGB());
			prefs.setEOLMarkerRGB(_eolAttributesPnl.getAttributeColor().getRGB());
			prefs.setBracketHighlightRGB(_bracketAttributesPnl.getAttributeColor().getRGB());
			prefs.setLineNumberRGB(_lineNumberAttributesPnl.getAttributeColor().getRGB());

			prefs.setColumnStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.COLUMNS));
			prefs.setCommentStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.COMMENTS));
			prefs.setKeyword1Style(_stylesList.getSyntaxStyleAt(IStylesListIndices.KEYWORDS));
			prefs.setKeyword2Style(_stylesList.getSyntaxStyleAt(IStylesListIndices.DATA_TYPES));
			prefs.setKeyword3Style(_stylesList.getSyntaxStyleAt(IStylesListIndices.FUNCTIONS));
			prefs.setLabelStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.LABELS));
			prefs.setLiteralStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.LITERALS));
			prefs.setOperatorStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.OPERATORS));
			prefs.setOtherStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.OTHER));
			prefs.setTableStyle(_stylesList.getSyntaxStyleAt(IStylesListIndices.TABLES));
		}

		private void updateControlStatus()
		{
			final boolean useJeditControl = _activeChk.isSelected();

			_caretColorSelector.setEnabled(useJeditControl);
			_selectionColorSelector.setEnabled(useJeditControl);

			_currentLineAttributesPnl.setEnabled(useJeditControl);
			_eolAttributesPnl.setEnabled(useJeditControl);
			_bracketAttributesPnl.setEnabled(useJeditControl);
			_lineNumberAttributesPnl.setEnabled(useJeditControl);

			_blockCaretEnabledChk.setEnabled(useJeditControl);
			_blinkCaretChk.setEnabled(useJeditControl);

			_stylesList.setEnabled(useJeditControl);
			_styleMaintPnl.setEnabled(useJeditControl);
		}

		private void createUserInterface(JeditPreferences prefs,
											JeditPluginResources rsrc)
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

			gbc.fill = gbc.BOTH;

			gbc.gridx = 0;
			++gbc.gridy;
			add(createGeneralPanel(rsrc), gbc);

			++gbc.gridx;
			add(createStylePanel(rsrc), gbc);

//			gbc.gridwidth = 2;
//			gbc.gridx = 0;
//			++gbc.gridy;
//			add(createPreviewPanel(prefs), gbc);
		}

		private JPanel createGeneralPanel(JeditPluginResources rsrc)
		{
			_caretColorSelector = new ColorSelector(rsrc);
			_selectionColorSelector = new ColorSelector(rsrc);

			_eolAttributesPnl = new ColorAttributePanel(i18n.EOL_MARKERS, rsrc);
			_bracketAttributesPnl = new ColorAttributePanel(i18n.BRACKET_HIGHLIGHTING, rsrc);
			_currentLineAttributesPnl = new ColorAttributePanel(i18n.HIGHLIGHT_CURRENT_LINE, rsrc);;
			_lineNumberAttributesPnl = new ColorAttributePanel("Line numbers", rsrc);

			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("General"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(2, 4, 2, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel("Selection:"), gbc);

			gbc.gridx = 3;
			pnl.add(_selectionColorSelector, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new JLabel("Caret:"), gbc);

			++gbc.gridx;
			pnl.add(_blinkCaretChk, gbc);

			++gbc.gridx;
			pnl.add(_blockCaretEnabledChk, gbc);

			++gbc.gridx;
			pnl.add(_caretColorSelector, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = 4;
			pnl.add(_eolAttributesPnl, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = 4;
			pnl.add(_currentLineAttributesPnl, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = 4;
			pnl.add(_bracketAttributesPnl, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(_lineNumberAttributesPnl, gbc);

			return pnl;
		}

		private JPanel createStylePanel(JeditPluginResources rsrc)
		{
			JPanel pnl = new JPanel(new BorderLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Syntax Styles"));

			_styleMaintPnl = new StyleMaintenancePanel(_stylesList, rsrc);
 
			pnl.add(_styleMaintPnl, BorderLayout.NORTH);
			pnl.add(_stylesList, BorderLayout.CENTER);

			return pnl;
		}

//		private JPanel createPreviewPanel(JeditPreferences prefs)
//		{
//			JPanel pnl = new JPanel(new BorderLayout());
//			pnl.setBorder(BorderFactory.createTitledBorder("Preview"));
//			_previewtextArea = new PreviewTextArea(prefs);
//			pnl.add(new JScrollPane(_previewtextArea), BorderLayout.CENTER);
//
//			return pnl;
//		}

		/**
		 * This panel represents an attribute that can be turned off
		 * via a checkbox and a color selector for the attribute that is
		 * only enabled if the checkbox is enabled.
		 */
		private static final class ColorAttributePanel extends JPanel
		{
			private final JCheckBox _chk;
			private final ColorSelector _sel;
			private ActionListener _lis;
			private boolean _attributeSelected;

			ColorAttributePanel(String checkBoxText, JeditPluginResources rsrc)
			{
				super(new GridBagLayout());

				_chk = new JCheckBox(checkBoxText);
				_sel = new ColorSelector(rsrc);

				final GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = gbc.HORIZONTAL;
				gbc.insets = new Insets(2, 0, 2, 0);
	
				gbc.weightx = 1.0;
				gbc.gridx = 0;
				gbc.gridy = 0;
				add(_chk, gbc);

				gbc.weightx = 0.0;
				++gbc.gridx;
				add(_sel, gbc);
			}

			public void addNotify()
			{
				super.addNotify();
	
				if (_lis == null)
				{
					_lis = new CheckBoxListener();
					_chk.addActionListener(_lis);
				}
			}

			public void removeNotify()
			{
				if (_lis != null)
				{
					_chk.removeActionListener(_lis);
					_lis = null;
				}
			}

			public void setEnabled(boolean value)
			{
				super.setEnabled(value);
				_chk.setEnabled(value);
				_sel.setEnabled(value && _attributeSelected);
			}

			Color getAttributeColor()
			{
				return _sel.getColor();
			}

			void setAttributeColor(Color color)
			{
				_sel.setColor(color);
			}

			boolean isAttributeSelected()
			{
				return _attributeSelected;
			}

			void setAttributeSelected(boolean value)
			{
				_attributeSelected = value;
				_chk.setSelected(value);
				_sel.setEnabled(value && _attributeSelected);
			}

			private final class CheckBoxListener implements ActionListener
			{
				public void actionPerformed(ActionEvent evt)
				{
					_attributeSelected = _chk.isSelected();
					_sel.setEnabled(_attributeSelected);
				}
			}
		}

		private static final class ColorSelector extends JPanel
		{
			private final JPanel _colorPnl = new JPanel();
			private ColorButton _btn;
			private ColorButtonListener _lis;

			ColorSelector(JeditPluginResources rsrc)
			{
				super(new GridLayout(1, 0, 4, 4));
				_btn = new ColorButton(_colorPnl, rsrc);
				add(_colorPnl);
				add(_btn);
				_colorPnl.setBorder(BorderFactory.createLoweredBevelBorder());
			}

			/**
			 * Component has been added to its parent so setup listeners etc.
			 */
			public void addNotify()
			{
				super.addNotify();
	
				if (_lis == null)
				{
					_lis = new ColorButtonListener();
					_btn.addActionListener(_lis);
				}
			}
	
			/**
			 * Component has been removed from its parent so remove listeners etc.
			 */
			public void removeNotify()
			{
				if (_lis != null)
				{
					_btn.removeActionListener(_lis);
					_lis = null;
				}
			}

			public void setEnabled(boolean value)
			{
				super.setEnabled(value);
				_btn.setEnabled(value);
//				if (value)
//				{
//					_colorPnl.setBorder(BorderFactory.createLoweredBevelBorder());
//				}
//				else
//				{
//					_colorPnl.setBorder(BorderFactory.createRaisedBevelBorder());
//				}
			}

			Color getColor()
			{
				return _btn.getColor();
			}

			void setColor(Color color)
			{
				_btn.setColor(color);
			}

			private class ColorButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent evt)
				{
					final Color color = JColorChooser.showDialog(null,
														"Select Color",
														getColor());
					if (color != null)
					{
						setColor(color);
					}
				}
			}
		}

		private static class ColorButton extends JButton
		{
			private final JPanel _pnl;

			ColorButton(JPanel pnl, JeditPluginResources rsrc)
			{
				super(rsrc.getIcon(JeditPluginResources.IKeys.COLOR_SELECTOR_IMAGE));
				_pnl = pnl;
				setToolTipText("Select colour");
			}

			public Dimension getPreferredSize()
			{
				Dimension dm = super.getPreferredSize();
				dm.width = dm.height;
				return dm;
			}
			
			Color getColor()
			{
				 return _pnl.getBackground();
			}

			void setColor(Color value)
			{
				_pnl.setBackground(value);
			}
		}

		private static class ColorButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				final ColorButton btn = (ColorButton)evt.getSource();
				final Color color = JColorChooser.showDialog(null,
													"Select Color",
													btn.getBackground());
				if (color != null)
				{
					btn.setColor(color);
				}
			}
		}

		/**
		 * Listener for the Font Color selection button. Show a Color selection
		 * dialog and if the user selects a color update the current style with
		 * that color.
		 */
		private static class FontColorButtonListener implements ActionListener
		{
			private final StylesList _list;
			
			FontColorButtonListener(StylesList list)
			{
				super();
				_list = list;
			}

			public void actionPerformed(ActionEvent evt)
			{
				final SyntaxStyle style = _list.getSelectedSyntaxStyle();
				final Color origColor = style.getTextColor();
				final Color color = JColorChooser.showDialog(null,
													"Select Color", origColor);
				if (color != null)
				{
					style.setTextRGB(color.getRGB());
				}
			}
		}


		/**
		 * Listener for the Background Color selection button. Show a Color
		 * selection dialog and if the user selects a color update the current
		 * style with that color.
		 */
		private static class BackgroundColorButtonListener implements ActionListener
		{
			private final StylesList _list;
			
			BackgroundColorButtonListener(StylesList list)
			{
				super();
				_list = list;
			}

			public void actionPerformed(ActionEvent evt)
			{
				final SyntaxStyle style = _list.getSelectedSyntaxStyle();
				final Color origColor = style.getBackgroundColor();
				final Color color = JColorChooser.showDialog(null,
													"Select Color", origColor);
				if (color != null)
				{
					style.setBackgroundRGB(color.getRGB());
				}
				
			}
		}

		/**
		 * Selection listener for the Styles List. As selection changes in the
		 * list then update the maintenance panel to reflect the current
		 * selected style.
		 */
		private class StylesListSelectionListener implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				_styleMaintPnl.setStyle(((StylesList)evt.getSource()).getSelectedSyntaxStyle());
			}
		}

		/**
		 * This panel allows maintenance of the selected Syntax Style.
		 */
		private static class StyleMaintenancePanel extends JToolBar
		{
			private final StylesList _list;
			private final JToggleButton _boldChk;
			private final JToggleButton _italicChk;
			private final JButton _fontColorBtn;
			private final JButton _backgroundColorBtn;
			private FontColorButtonListener _fontColorBtnLis;
			private BackgroundColorButtonListener _backgroundColorBtnLis;
			private ActionListener _toggleLis;
	
			private SyntaxStyle _style;

			StyleMaintenancePanel(StylesList list, JeditPluginResources rsrc)
			{
				super();
				_list = list;

				this.setFloatable(false);

				_boldChk = new JToggleButton(rsrc.getIcon(JeditPluginResources.IKeys.BOLD_IMAGE));
				_boldChk.setToolTipText("Bold");
				_italicChk = new JToggleButton(rsrc.getIcon(JeditPluginResources.IKeys.ITALIC_IMAGE));
				_italicChk.setToolTipText("Italic");

				_fontColorBtn = new JButton(rsrc.getIcon(JeditPluginResources.IKeys.FOREGROUND_IMAGE));
				_fontColorBtn.setToolTipText("Select font color");
				_backgroundColorBtn = new JButton(rsrc.getIcon(JeditPluginResources.IKeys.BACKGROUND_IMAGE));
				_backgroundColorBtn.setToolTipText("Select background color");

				add(_boldChk);
				add(_italicChk);
				add(_fontColorBtn);
				add(_backgroundColorBtn);
			}

			/**
			 * Component has been added to its parent so setup listeners etc.
			 */
			public void addNotify()
			{
				super.addNotify();
	
				if (_fontColorBtnLis == null)
				{
					_fontColorBtnLis = new FontColorButtonListener(_list);
					_fontColorBtn.addActionListener(_fontColorBtnLis);
					_backgroundColorBtnLis = new BackgroundColorButtonListener(_list);
					_backgroundColorBtn.addActionListener(_backgroundColorBtnLis);
				}

				if (_toggleLis == null)
				{
					_toggleLis = new ToggleButtonListener();
					_boldChk.addActionListener(_toggleLis);
					_italicChk.addActionListener(_toggleLis);
				}
			}
	
			/**
			 * Component has been removed from its parent so remove listeners etc.
			 */
			public void removeNotify()
			{
				if (_fontColorBtnLis != null)
				{
					_fontColorBtn.removeActionListener(_fontColorBtnLis);
					_backgroundColorBtn.removeActionListener(_backgroundColorBtnLis);
					_fontColorBtnLis = null;
					_backgroundColorBtnLis = null;
				}
				if (_toggleLis != null)
				{
					_boldChk.removeActionListener(_toggleLis);
					_italicChk.removeActionListener(_toggleLis);
					_toggleLis = null;
				}
	
				super.removeNotify();
			}

			public void setEnabled(boolean enable)
			{
				_boldChk.setEnabled(enable);
				_italicChk.setEnabled(enable);
				_fontColorBtn.setEnabled(enable);
				_backgroundColorBtn.setEnabled(enable);
			}

			void setStyle(SyntaxStyle style)
			{
				_boldChk.setSelected(style.isBold());
				_italicChk.setSelected(style.isItalic());
				_style = style;
			}

			private final class ToggleButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent evt)
				{
					_style.setBold(_boldChk.isSelected());
					_style.setItalic(_italicChk.isSelected());
					_list.repaint();
				}
			}
		}

		private class StylesList extends JList
		{
			SyntaxStyle[] _styles = new SyntaxStyle[IStylesListIndices.LIST_SIZE];

			StylesList()
			{
				super(new DefaultListModel());
				setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				setCellRenderer(new ListRenderer());
				setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}

			void loadData(JeditPreferences prefs)
			{
				removeAll();

				_styles[IStylesListIndices.KEYWORDS] = new SyntaxStyle(prefs.getKeyword1Style());
				_styles[IStylesListIndices.DATA_TYPES] = new SyntaxStyle(prefs.getKeyword2Style());
				_styles[IStylesListIndices.FUNCTIONS] = new SyntaxStyle(prefs.getKeyword3Style());
				_styles[IStylesListIndices.TABLES] = new SyntaxStyle(prefs.getTableStyle());
				_styles[IStylesListIndices.COLUMNS] = new SyntaxStyle(prefs.getColumnStyle());
				_styles[IStylesListIndices.COMMENTS] = new SyntaxStyle(prefs.getCommentStyle());
				_styles[IStylesListIndices.LITERALS] = new SyntaxStyle(prefs.getLiteralStyle());
				_styles[IStylesListIndices.OPERATORS] = new SyntaxStyle(prefs.getOperatorStyle());
				_styles[IStylesListIndices.LABELS] = new SyntaxStyle(prefs.getLabelStyle());
				_styles[IStylesListIndices.OTHER] = new SyntaxStyle(prefs.getOtherStyle());

				final DefaultListModel model = (DefaultListModel)getModel();
				for (int i = 0; i < _styles.length; ++i)
				{
					model.addElement(_styles[i]);
				}

				setSelectedIndex(0);
			}

			SyntaxStyle getSelectedSyntaxStyle()
			{
				return (SyntaxStyle)getSelectedValue();
			}

			SyntaxStyle getSyntaxStyleAt(int idx)
			{
				return (SyntaxStyle)getModel().getElementAt(idx);
			}
		}

		private static final class ListRenderer extends JLabel
												implements ListCellRenderer
		{
			ListRenderer()
			{
				super();
				setOpaque(true);
			}

			public Component getListCellRendererComponent(JList list,
								Object value, int idx, boolean isSelected,
								boolean cellHasFocus)
			{
				final SyntaxStyle style = (SyntaxStyle)value;
				setForeground(style.getTextColor());
				setBackground(style.getBackgroundColor());

				setFont(style.createStyledFont(getFont()));
				setText(s_styleTitles[idx]);

				if (isSelected)
				{
					setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				}
				else
				{
					setBorder(BorderFactory.createEmptyBorder());
				}

				return this;
			}
		}

	}

	/**
	 * Text area that previews the options selected by the user.
	 */
//	private final static class PreviewTextArea extends JEditTextArea
//	{
//		private static final String[] SQL = new String[]
//		{
//			"select col1, col2, from table1;",
//			"create table (col1 varchar, col2 int)",
//		};
//
//		private final JeditTextAreaDefaults _taDfts;
//		private final JeditPreferences _prefs;
//
//		PreviewTextArea(JeditPreferences prefs)
//		{
//			super();
//			_prefs = prefs;
//
//			setEditorKit(new MoeSyntaxEditorKit(prefs));
//
//			_taDfts = new JeditTextAreaDefaults(this, prefs);
//
//			final SyntaxDocument doc = getSyntaxDocument();
//			doc.setTokenMarker(new JeditSQLTokenMarker());
//			doc.setTextAreaDefaults(_taDfts);
//
//			updateFromPreferences();
//
//			for (int i = 0; i < SQL.length; ++i)
//			{
//				try
//				{
//					doc.insertString(doc.getLength(), SQL[i], null);
//					doc.insertString(doc.getLength(), "\n", null);
//				}
//				catch (BadLocationException ex)
//				{
//					s_log.error(ex);
//				}
//			}
//		}
//
//		private void updateFromPreferences()
//		{
//			_taDfts.updateFromPreferences(_prefs);
//			_taDfts.updateControl(this);
//		}
//	}
}
