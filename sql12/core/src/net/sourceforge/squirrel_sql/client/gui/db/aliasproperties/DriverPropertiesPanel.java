package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;
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

import net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases.SQLAliasPropI18nEnum;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
/**
 * This panel allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesPanel extends JPanel 
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DriverPropertiesPanel.class);

   // i18n[DriverPropertiesPanel.useDriverProperties=Use driver properties]
   JCheckBox chkUseDriverProperties = new JCheckBox(SQLAliasPropI18nEnum.useDriverProperties.getString());

   private interface i18n
   {
      String INSTRUCTIONS = s_stringMgr.getString("DriverPropertiesPanel.instructions");
   }

	DriverPropertiesTable tblDriverProperties;

	/**
	 * Display the description for the currently selected property in this
	 * control.
	 */
	private final MultipleLineLabel _descriptionLbl = new MultipleLineLabel();

	public DriverPropertiesPanel(SQLDriverPropertyCollection props)
	{
		super(new GridBagLayout());
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	/**
	 * Retrieve the database properties.
	 *
	 * @return		the database properties.
	 */
	public SQLDriverPropertyCollection getSQLDriverProperties()
	{
      TableCellEditor cellEditor = tblDriverProperties.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.stopCellEditing();
      }
		return tblDriverProperties.getTypedModel().getSQLDriverProperties();
	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		tblDriverProperties = new DriverPropertiesTable(props);

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0;
      gbc.weighty = 0;
      add(chkUseDriverProperties, gbc);



      gbc.fill = GridBagConstraints.BOTH;
      gbc.weighty = 1.0;
      gbc.weightx = 1.0;
      ++gbc.gridy;
		JScrollPane sp = new JScrollPane(tblDriverProperties);
		add(sp, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		++gbc.gridy;
		add(createInfoPanel(), gbc);

		tblDriverProperties.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				updateDescription(tblDriverProperties.getSelectedRow());
			}
		});

		if (tblDriverProperties.getRowCount() > 0)
		{
			tblDriverProperties.setRowSelectionInterval(0, 0);
		}
	}

	private void updateDescription(int idx)
	{
		if (idx != -1)
		{
			String desc = (String) tblDriverProperties.getValueAt(idx, DriverPropertiesTableModel.IColumnIndexes.IDX_DESCRIPTION);
			_descriptionLbl.setText(desc);
		}
		else
		{
			_descriptionLbl.setText(" ");
		}
	}

	private Box createInfoPanel()
	{
		final Box pnl = Box.createVerticalBox();
		pnl.add(new JSeparator());
		pnl.add(_descriptionLbl);
		pnl.add(new JSeparator());
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));

		return pnl;
	}
}

