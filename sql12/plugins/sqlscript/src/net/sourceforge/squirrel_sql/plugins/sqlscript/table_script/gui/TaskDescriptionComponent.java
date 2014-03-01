/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.gui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.FlowLayout;

/**
 * @author Stefan Willinger
 * 
 */
public class TaskDescriptionComponent extends JComponent {
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(TaskDescriptionComponent.class);

	interface i18n{
		String TITEL_INFORMATION = s_stringMgr.getString("TaskDescriptionComponent.titelInformation");
		String LABEL_FILE = s_stringMgr.getString("TaskDescriptionComponent.labelFile");
		String LABEL_SQL = s_stringMgr.getString("TaskDescriptionComponent.labelSql");
		
	}
	
	
	
	private JTextField tfTargetFile;
	private JTextArea taSql;

	public TaskDescriptionComponent(String targetFile, String sql) {
		this();
		setTargetFile(targetFile);
		setSql(sql);
	}

	/**
	 * @param sql
	 *            SQL to show.
	 * 
	 */
	public void setSql(String sql) {
		taSql.setText(sql);
		taSql.setCaretPosition(0);
	}

	/**
	 * @param targetFile
	 *            File to show
	 * 
	 */
	public void setTargetFile(String targetFile) {
		tfTargetFile.setText(targetFile);
	}

	public TaskDescriptionComponent() {
		init();
	}

	private void init() {
		setBorder(BorderFactory.createTitledBorder(i18n.TITEL_INFORMATION));
		setLayout(new BorderLayout());

		JPanel viewPanel = new JPanel();
		viewPanel.setMinimumSize(new Dimension(400, 200));
		viewPanel.setPreferredSize(new Dimension(400, 200));
		GridBagLayout gbl_viewPanel = new GridBagLayout();
		viewPanel.setLayout(gbl_viewPanel);

		add(viewPanel,BorderLayout.CENTER);
		
		JLabel jlTargetFile = new JLabel();
		jlTargetFile.setHorizontalAlignment(SwingConstants.RIGHT);
		jlTargetFile.setText(i18n.LABEL_FILE);
		GridBagConstraints gbc_jlTargetFile = new GridBagConstraints();
		gbc_jlTargetFile.insets = new Insets(0, 0, 5, 5);
		gbc_jlTargetFile.anchor = GridBagConstraints.EAST;
		gbc_jlTargetFile.gridx = 0;
		gbc_jlTargetFile.gridy = 0;
		viewPanel.add(jlTargetFile, gbc_jlTargetFile);

		tfTargetFile = new JTextField();
		tfTargetFile.setEditable(false);
		GridBagConstraints gbc_tfTargetFile = new GridBagConstraints();
		gbc_tfTargetFile.weightx = 1.0;
		gbc_tfTargetFile.insets = new Insets(0, 0, 5, 0);
		gbc_tfTargetFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTargetFile.gridx = 1;
		gbc_tfTargetFile.gridy = 0;
		viewPanel.add(tfTargetFile, gbc_tfTargetFile);
		tfTargetFile.setColumns(10);

		JLabel lblSql = new JLabel();
		lblSql.setText(i18n.LABEL_SQL);
		GridBagConstraints gbc_lblSql = new GridBagConstraints();
		gbc_lblSql.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblSql.insets = new Insets(0, 0, 5, 5);
		gbc_lblSql.gridx = 0;
		gbc_lblSql.gridy = 1;
		viewPanel.add(lblSql, gbc_lblSql);

		taSql = new JTextArea();
		taSql.setEditable(false);
//		taSql.setPreferredSize(new Dimension(400, 150));

		JScrollPane spSql = new JScrollPane(taSql);
		GridBagConstraints gbc_spSql = new GridBagConstraints();
		gbc_spSql.weighty = 1.0;
		gbc_spSql.weightx = 1.0;
		gbc_spSql.insets = new Insets(0, 0, 5, 0);
		gbc_spSql.fill = GridBagConstraints.BOTH;
		gbc_spSql.gridx = 1;
		gbc_spSql.gridy = 1;
		viewPanel.add(spSql, gbc_spSql);

	}
}
