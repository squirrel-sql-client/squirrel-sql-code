package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2003-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
/**
 * THis panel allows filtering of the objects displayed in the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeFilterPanel extends JPanel
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	interface i18n
	{
		String LIMIT_ROWS_CONTENTS = "Contents - Limit rows";
		String SCHEMA_PREFIX = "Limit Schema Objects using these comma-delimited prefixes:";
		String SHOW_ROW_COUNT = "Show Row Count for Tables (can slow application)";
		String OBJECT_TREE = "Object Tree";
	}

	private final IObjectTreeAPI _tree;

	private final JTextField _schemaPrefixField = new JTextField(20);

	public ObjectTreeFilterPanel(IObjectTreeAPI tree)
	{
		super(new GridBagLayout());
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}

		_tree = tree;

		createGUI();
		loadData();
	}

	private void loadData()
	{
//		DefaultFormBuilder
//		StringBuffer buf = new StringBuffer();
//		for (int i = 0; i < dboTypes.length; ++i)
//		{
//			buf.append(dboTypes[i].toString());
//			if (i < (dboTypes.length - 1))
//			{
//				buf.append(", ");
//			}
//		}
//		_schemaPrefixField.setText(buf.toString());
	}

	private void createGUI()
	{
		final DatabaseObjectType[] dboTypes = _tree.getDatabaseObjectTypes();
		setBorder(BorderFactory.createTitledBorder("Filters"));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.weightx = 1.0;

		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel(i18n.SCHEMA_PREFIX, SwingConstants.RIGHT));
		++gbc.gridy;
		add(_schemaPrefixField, gbc);
	}
}
