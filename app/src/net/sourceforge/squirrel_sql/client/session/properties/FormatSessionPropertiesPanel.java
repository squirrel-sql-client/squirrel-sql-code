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
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This panel allows the user to tailor DataType-specific settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author gwg
 */
public class FormatSessionPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Application API. */
	private final IApplication _app;

	/** The actual GUI panel that allows user to do the maintenance. */
	private final FormatPropertiesPanel _myPanel;


	/**
	 * ctor specifying the Application API.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public FormatSessionPropertiesPanel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		_myPanel = new FormatPropertiesPanel(app);
	}


	public void initialize(IApplication app)
	{
		// We need this method to satisfy one of the Interfaces we implement,
		// but since we have moved all operations to the DataType sub-panels
		// which initialize their own data during creation,
		// there is nothing for us to do here
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		// We need this method to satisfy one of the Interfaces we implement,
		// but since we have moved all operations to the DataType sub-panels
		// which initialize their own data during creation,
		// there is nothing for us to do here
	}

	public Component getPanelComponent()
	{
		return _myPanel;
	}

	public String getTitle()
	{
		return FormatPropertiesPanel.i18n.TITLE;
	}

	public String getHint()
	{
		return FormatPropertiesPanel.i18n.HINT;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	private static final class FormatPropertiesPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String HINT = "Set options for specific Data Types";
			String TITLE = "Data Type Controls";
		}
		
		/** List of OkJPanels containing controls for specific DataType info */
		OkJPanel[] dataTypePanels;

		FormatPropertiesPanel(IApplication app)
		{
			super();
			createGUI(app);
		}
		

		void applyChanges()
		{		
			for (int i=0; i< dataTypePanels.length; i++)
				dataTypePanels[i].ok();
		}


		private void createGUI(IApplication app)
		{

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;

			JScrollPane sp = new JScrollPane(createDataTypesPanel());
			sp.setPreferredSize(new Dimension(600, 450));
			add(sp, gbc);

		}

		private JPanel createDataTypesPanel()
		{

			JPanel pnl = new JPanel(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			// add each of the panels created by the DataType objects for
			// editing their own properties
			dataTypePanels = CellComponentFactory.getControlPanels();
			for (int i=0; i<dataTypePanels.length; i++) {
				gbc.gridx=0;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				++gbc.gridy;
				pnl.add(dataTypePanels[i], gbc);
			}
			
			return pnl;
		}

		private static final class RightLabel extends JLabel
		{
			RightLabel(String title)
			{
				super(title, SwingConstants.RIGHT);
			}
		}

	}

}
