package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class MainTabPanel extends JPanel {
	private SessionScriptPlugin _plugin;
	private ScriptsJList _scriptsList;
	private JTextArea _sqlEntry = new JTextArea();

	MainTabPanel(SessionScriptPlugin plugin) {
		super();
		_plugin = plugin;
	}

	void createUserInterface() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		_scriptsList = new ScriptsJList();

		_scriptsList.addListSelectionListener(new ListSelectionListener() {
			 public void valueChanged(ListSelectionEvent e)  {
			 	String sql = (String)_scriptsList.getSelectedValue();
			 	if (sql != null) {
			 		_sqlEntry.setText(sql);
			 	}
			 }
		});

		JButton upBtn = new JButton("Up");
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int idx = _scriptsList.getSelectedIndex();
				if (idx > 0) {
					ScriptsJListModel model = _scriptsList.getTypedModel();
					Object obj = model.remove(idx);
					--idx;
					model.insertElementAt(obj, idx);
					_scriptsList.setSelectedIndex(idx);
				}
			}
		});
		
		JButton downBtn = new JButton("Down");
		downBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int idx = _scriptsList.getSelectedIndex();
				ScriptsJListModel model = _scriptsList.getTypedModel();
				if (idx > -1 && idx < (model.getSize() - 1)) {
					Object obj = model.remove(idx);
					++idx;
					model.insertElementAt(obj, idx);
					_scriptsList.setSelectedIndex(idx);
				}
			}
		});
//		GUIUtils.setJButtonSizesTheSame(new JButton[] {upBtn, downBtn});

		setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridheight = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = gbc.BOTH;
		add(new JScrollPane(_scriptsList), gbc);
		
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = gbc.HORIZONTAL;
		++gbc.gridx;
		add(upBtn, gbc);
		++gbc.gridy;
		add(downBtn, gbc);

		gbc.fill = gbc.BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JScrollPane(_sqlEntry), gbc);
	}

	private static class ScriptsJList extends JList {
		ScriptsJList() {
			super(new ScriptsJListModel());
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		ScriptsJListModel getTypedModel() {
			return (ScriptsJListModel)getModel();
		}
	}

	private static class ScriptsJListModel extends DefaultListModel {
		ScriptsJListModel() {
			super();
			addElement("select * from issuer1");
			addElement("select * from issuer2");
			addElement("select * from issuer3");
			addElement("select * from issuer4");
			addElement("select * from issuer5");
		}
	}
}
