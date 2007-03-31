package net.sourceforge.squirrel_sql.client.preferences;
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This panel allows the user to tailor DataType-specific settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author gwg
 */
public class DataTypePreferencesPanel implements  IGlobalPreferencesPanel
{

	/** The actual GUI panel that allows user to do the maintenance. */
	private final DataTypePropertiesPanel _myPanel;
    
    private JScrollPane _myscrolledPanel;
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DataTypePreferencesPanel.class);

   /**
    * ctor specifying the Application API.
    *
    * @param	app		Application API.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
    * 			passed.
    */
   public DataTypePreferencesPanel()
   {
      super();

      _myPanel = new DataTypePropertiesPanel();
      _myscrolledPanel = new JScrollPane(_myPanel);
      _myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
      _myscrolledPanel.setPreferredSize(new Dimension(600, 450));      
   }


	public void initialize(IApplication app)
	{
		// We need this method to satisfy one of the Interfaces we implement,
		// but since we have moved all operations to the DataType sub-panels
		// which initialize their own data during creation,
		// there is nothing for us to do here
	}

   public void uninitialize(IApplication app)
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
		return _myscrolledPanel;
	}

	public String getTitle()
	{
        // i18n[DataTypePreferencesPanel.propsPanel.title=Data Type Controls]
		return s_stringMgr.getString("DataTypePreferencesPanel.propsPanel.title");
	}

	public String getHint()
	{
        // i18n[DataTypePreferencesPanel.propsPanel.hint=Set options for specific Data Types]
		return s_stringMgr.getString("DataTypePreferencesPanel.propsPanel.hint");
	}

	public void applyChanges()
	{
		_myPanel.applyChanges();
	}

	private static final class DataTypePropertiesPanel extends JPanel
	{
		
		/** List of OkJPanels containing controls for specific DataType info */
		OkJPanel[] dataTypePanels;

		DataTypePropertiesPanel()
		{
			super();
			createGUI();
		}
		

		void applyChanges()
		{		
			for (int i=0; i< dataTypePanels.length; i++)
				dataTypePanels[i].ok();
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

//			JScrollPane sp = new JScrollPane(createDataTypesPanel());
//			sp.setPreferredSize(new Dimension(600, 450));
//			add(sp, gbc);
         add(createDataTypesPanel(), gbc);

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
