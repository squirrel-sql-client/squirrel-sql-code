package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.gui.FontChooser;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class GeneralSessionPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	interface GeneralSessionPropertiesPanelI18n
	{
		String META_DATA = "Meta Data:";
		String SQL_RESULTS = "SQL Results:";
		String TITLE = "General";
		String HINT = "General settings for the current session";
		String SHOW_TOOLBAR = "Show toolbar";
		String TABLE = "Table";
		String TEXT = "Text";
	}

	private IApplication _app;
	private SessionProperties _props;

	private MyPanel _myPanel = new MyPanel();

	public GeneralSessionPropertiesPanel()
	{
		super();
	}

	public void initialize(IApplication app) throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_props = app.getSquirrelPreferences().getSessionProperties();

		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_app = app;
		_props = session.getProperties();

		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _myPanel;
	}

	public String getTitle()
	{
		return GeneralSessionPropertiesPanelI18n.TITLE;
	}

	public String getHint()
	{
		return GeneralSessionPropertiesPanelI18n.HINT;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class MyPanel extends JPanel
	{

		private JCheckBox _showToolBar = new JCheckBox(GeneralSessionPropertiesPanelI18n.SHOW_TOOLBAR);
		private OutputTypeCombo _metaDataCmb = new OutputTypeCombo();
		private OutputTypeCombo _sqlResultsCmb = new OutputTypeCombo();

		/** Label displaying the selected font. */
		private JLabel _fontLbl = new JLabel();

		/** Button to select font. */
		private FontButton _fontBtn = new FontButton("Font", _fontLbl);

		MyPanel()
		{
			super(new GridBagLayout());
			createUserInterface();
		}

		void loadData(SessionProperties props)
		{
			_showToolBar.setSelected(props.getShowToolBar());
			_metaDataCmb.selectClassName(props.getMetaDataOutputClassName());
			_sqlResultsCmb.selectClassName(props.getSQLResultsOutputClassName());

			FontInfo fi = props.getFontInfo();
			if (fi == null)
			{
				fi = new FontInfo(UIManager.getFont("TextArea.font"));
			}
			_fontLbl.setText(fi.toString());
			_fontBtn.setSelectedFont(fi.createFont());
		}

		void applyChanges(SessionProperties props)
		{
			props.setShowToolBar(_showToolBar.isSelected());
			props.setMetaDataOutputClassName(_metaDataCmb.getSelectedClassName());
			props.setSQLResultsOutputClassName(_sqlResultsCmb.getSelectedClassName());
			props.setFontInfo(_fontBtn.getFontInfo());
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createGeneralPanel(), gbc);

			++gbc.gridy;
			add(createOutputPanel(), gbc);

			++gbc.gridy;
			add(createFontPanel(), gbc);
		}

		private JPanel createGeneralPanel()
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("General"));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.anchor = gbc.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.weightx = 1.0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_showToolBar, gbc);

			return pnl;
		}

		private JPanel createOutputPanel()
		{
			JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Output"));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.META_DATA, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_metaDataCmb, gbc);

			++gbc.gridx;
			pnl.add(new JLabel(GeneralSessionPropertiesPanelI18n.SQL_RESULTS, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			pnl.add(_sqlResultsCmb, gbc);

			return pnl;
		}

		private JPanel createFontPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL Entry Area"));
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			_fontBtn.addActionListener(new FontButtonListener());

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_fontBtn, gbc);

			++gbc.gridx;
			gbc.fill = gbc.HORIZONTAL;
			gbc.weightx = 1.0;
			pnl.add(_fontLbl, gbc);

			return pnl;
		}
	}

	private final static class OutputType
	{
		static final OutputType TEXT =
				new OutputType(GeneralSessionPropertiesPanelI18n.TEXT,
								DataSetViewerTextPanel.class.getName());
		static final OutputType TABLE =
				new OutputType(GeneralSessionPropertiesPanelI18n.TABLE,
								DataSetViewerTablePanel.class.getName());
		private final String _name;
		private final String _className;

		OutputType(String name, String className)
		{
			super();
			_name = name;
			_className = className;
		}

		public String toString()
		{
			return _name;
		}

		String getPanelClassName()
		{
			return _className;
		}
	}

	private static final class OutputTypeCombo extends JComboBox
	{
		OutputTypeCombo()
		{
			super();
			addItem(OutputType.TABLE);
			addItem(OutputType.TEXT);
		}

		void selectClassName(String className)
		{
			if (className.equals(DataSetViewerTablePanel.class.getName()))
			{
				setSelectedItem(OutputType.TABLE);
			}
			else
			{
				setSelectedItem(OutputType.TEXT);
			}
		}

		String getSelectedClassName()
		{
			return ((OutputType) getSelectedItem()).getPanelClassName();
		}
	}

	private static final class FontButton extends JButton
	{
		private FontInfo _fi;
		private JLabel _lbl;
		private Font _font;

		FontButton(String text, JLabel lbl)
		{
			super(text);
			_lbl = lbl;
		}

		FontInfo getFontInfo()
		{
			return _fi;
		}

		Font getSelectedFont()
		{
			return _font;
		}

		void setSelectedFont(Font font)
		{
			_font = font;
			if (_fi == null)
			{
				_fi = new FontInfo(font);
			}
			else
			{
				_fi.setFont(font);
			}
		}
	}

	private static final class FontButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() instanceof FontButton)
			{
				FontButton btn = (FontButton) evt.getSource();
				FontInfo fi = btn.getFontInfo();
				Font font = null;
				if (fi != null)
				{
					font = fi.createFont();
				}
				font = new FontChooser().showDialog(font);
				if (font != null)
				{
					btn.setSelectedFont(font);
					btn._lbl.setText(new FontInfo(font).toString());
				}
			}
		}
	}
}