package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2003 Colin Bell
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
/**
 * This panel allows the user to tailor object tree settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionObjectTreePropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Application API. */
	private final IApplication _app;

	/** The actual GUI panel that allows user to do the maintenance. */
	private final ObjectTreepropsPanel _myPanel;

	/** Session properties object being maintained. */
	private SessionProperties _props;

	/**
	 * ctor specifying the Application API.
	 *
	 * @param	app	Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public SessionObjectTreePropertiesPanel(IApplication app)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new ObjectTreepropsPanel(app);
	}

	/**
	 * Initialize this panel with the "New Session" settings.
	 *
	 * @param	app	Application API.
	 */
	public void initialize(IApplication app)
	{
		_props = _app.getSquirrelPreferences().getSessionProperties();
		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_props = session.getProperties();
		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _myPanel;
	}

	public String getTitle()
	{
		return ObjectTreepropsPanel.i18n.OBJECT_TREE;
	}

	public String getHint()
	{
		return ObjectTreepropsPanel.i18n.OBJECT_TREE;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

	private static final class ObjectTreepropsPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String CATALOG_PREFIX = "Limit Catalog Objects using these comma-delimited prefixes:";
			String LIMIT_ROWS_CONTENTS = "Contents - Limit rows";
			String SCHEMA_PREFIX = "Limit Schema Objects using these comma-delimited prefixes:";
			String SHOW_ROW_COUNT = "Show Row Count for Tables (can slow application)";
			String OBJECT_TREE = "Object Tree";
		}

		private IntegerField _contentsNbrRowsToShowField = new IntegerField(5);
		private JCheckBox _contentsLimitRowsChk = new JCheckBox(i18n.LIMIT_ROWS_CONTENTS);
		private JCheckBox _showRowCountChk = new JCheckBox(i18n.SHOW_ROW_COUNT);
		private JTextField _schemaPrefixField = new JTextField(20);
		private JTextField _catalogPrefixField = new JTextField(20);
		private JCheckBox _loadSchemasCatalogsChk = new JCheckBox("Load Schemas/Catalogs into object tree");

		/**
		 * This object will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final ControlMediator _controlMediator = new ControlMediator();

		ObjectTreepropsPanel(IApplication app)
		{
			super();
			createGUI();
		}

		void loadData(SessionProperties props)
		{
			_contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
			_contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
			_showRowCountChk.setSelected(props.getShowRowCount());
			_schemaPrefixField.setText(props.getSchemaPrefixList());
			_catalogPrefixField.setText(props.getCatalogPrefixList());
			_loadSchemasCatalogsChk.setSelected(props.getLoadSchemasCatalogs());

			updateControlStatus();
		}

		void applyChanges(SessionProperties props)
		{
			props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
			props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());
			props.setShowRowCount(_showRowCountChk.isSelected());
			props.setSchemaPrefixList(_schemaPrefixField.getText());
			props.setCatalogPrefixList(_catalogPrefixField.getText());
			props.setLoadSchemasCatalogs(_loadSchemasCatalogsChk.isSelected());
		}

		private void updateControlStatus()
		{
			_contentsNbrRowsToShowField.setEnabled(_contentsLimitRowsChk.isSelected());
		}

		private void createGUI()
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createObjectTreePanel(), gbc);

			++gbc.gridy;
			add(createFilterPanel(), gbc);
		}

		private JPanel createObjectTreePanel()
		{
			final JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder(i18n.OBJECT_TREE));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.CENTER;

			_contentsLimitRowsChk.addChangeListener(_controlMediator);

			_contentsNbrRowsToShowField.setColumns(5);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_loadSchemasCatalogsChk, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			pnl.add(_showRowCountChk, gbc);

			++gbc.gridy; // new line
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			pnl.add(_contentsLimitRowsChk, gbc);
			gbc.gridwidth = 1;
			gbc.gridx+=2;
			pnl.add(_contentsNbrRowsToShowField, gbc);
			++gbc.gridx;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new JLabel("rows"), gbc);

			return pnl;
		}
		private JPanel createFilterPanel()
		{
			final JPanel pnl = new JPanel(new GridBagLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Filters"));

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.weightx = 1.0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(i18n.SCHEMA_PREFIX, SwingConstants.RIGHT), gbc);
			++gbc.gridy;
			pnl.add(_schemaPrefixField, gbc);

			++gbc.gridy;
			pnl.add(new JLabel(i18n.CATALOG_PREFIX, SwingConstants.RIGHT), gbc);
			++gbc.gridy;
			pnl.add(_catalogPrefixField, gbc);

			return pnl;
		}

		/**
		 * This class will update the status of the GUI controls as the user
		 * makes changes.
		 */
		private final class ControlMediator implements ChangeListener,
															ActionListener
		{
			public void stateChanged(ChangeEvent evt)
			{
				updateControlStatus();
			}

			public void actionPerformed(ActionEvent evt)
			{
				updateControlStatus();
			}
		}
	}
}
