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
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ModifiedDefaultListCellRenderer;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class MainTabPanel extends JPanel {
	private SessionScriptPlugin _plugin;
	private ISession _session;
	private ScriptsJList _scriptsList;
	private JTextArea _sqlEntry = new JTextArea();

	MainTabPanel(SessionScriptPlugin plugin, ISession session) {
		super();
		_plugin = plugin;
		_session = session;
	}

	void createUserInterface() {
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		_scriptsList = new ScriptsJList(_plugin, _session);

		_scriptsList.addListSelectionListener(new ListSelectionListener() {
			 public void valueChanged(ListSelectionEvent e)  {
			 	AliasScript script = _scriptsList.getSelectedScript();
			 	_sqlEntry.setText(script != null ? script.getSQL() : "");
				_sqlEntry.setSelectionStart(0);
				_sqlEntry.setSelectionEnd(_sqlEntry.getText().length());
			 }
		});

		_sqlEntry.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JButton upBtn = new JButton("Up");
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_scriptsList) {
					int idx = _scriptsList.getSelectedIndex();
					if (idx > 0) {
						ScriptsJListModel model = _scriptsList.getTypedModel();
						Object obj = model.remove(idx);
						--idx;
						model.insertElementAt(obj, idx);
						_scriptsList.setSelectedIndex(idx);
					}
				}
			}
		});
		
		JButton downBtn = new JButton("Down");
		downBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_scriptsList) {
					int idx = _scriptsList.getSelectedIndex();
					ScriptsJListModel model = _scriptsList.getTypedModel();
					if (idx > -1 && idx < (model.getSize() - 1)) {
						Object obj = model.remove(idx);
						++idx;
						model.insertElementAt(obj, idx);
						_scriptsList.setSelectedIndex(idx);
					}
				}
			}
		});

		JButton newBtn = new JButton("New");
		newBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_scriptsList) {
					ScriptsJListModel model = _scriptsList.getTypedModel();
					model.addScript(new AliasScript("Enter script"));
					_scriptsList.setSelectedIndex(model.getSize() - 1);
				}
			}
		});

		JButton deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_scriptsList) {
					AliasScript script = _scriptsList.getSelectedScript();
					if (script != null) {
						int idx = _scriptsList.getSelectedIndex();
						ScriptsJListModel model = _scriptsList.getTypedModel();
						model.removeScript(script);
						final int size = model.getSize();
						if (idx < size) {
							_scriptsList.setSelectedIndex(idx);
						} else if (size > 0) {
							_scriptsList.setSelectedIndex(size - 1);
						}
					}
				}
			}
		});

		JButton applyBtn = new JButton("Apply Changes");
		applyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				synchronized (_scriptsList) {
					int idx = _scriptsList.getSelectedIndex();
					if (idx > -1) {
						ScriptsJListModel model = _scriptsList.getTypedModel();
						AliasScript script = _scriptsList.getSelectedScript();
						if (script != null) {
							script.setSQL(_sqlEntry.getText());
						}
					}
				}
			}
		});

		final int LIST_CELL_HEIGHT = 7;

		setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridheight = LIST_CELL_HEIGHT;
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

		++gbc.gridy;
		gbc.insets = new Insets(5, 5, 5, 5);
		add(new JSeparator(), gbc);
		gbc.insets = new Insets(4, 4, 4, 4);

		++gbc.gridy;
		add(newBtn, gbc);

		++gbc.gridy;
		add(deleteBtn, gbc);

		gbc.fill = gbc.BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.gridx = 0;
		gbc.gridy = LIST_CELL_HEIGHT;
		add(new JScrollPane(_sqlEntry), gbc);

		gbc.fill = gbc.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		++gbc.gridx;
		add(applyBtn, gbc);
	}

	private static class ScriptsJList extends JList {
		ScriptsJList(SessionScriptPlugin plugin, ISession session) {
			super(new ScriptsJListModel(plugin, session));
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setCellRenderer(new ModifiedDefaultListCellRenderer());
		}

		ScriptsJListModel getTypedModel() {
			return (ScriptsJListModel)getModel();
		}

		AliasScript getSelectedScript() {
			return (AliasScript)getSelectedValue();
		}
	}

	private static class ScriptsJListModel extends DefaultListModel {
		private AliasScriptCollection _coll;

		ScriptsJListModel(SessionScriptPlugin plugin, ISession session) {
			super();
			_coll = plugin.getScriptsCache().get(session.getAlias());
			AliasScript[] scripts = _coll.getScripts();
			for (int i = 0; i < scripts.length; ++i) {
				addElement(scripts[i]);
			}
		}

		AliasScript getScript(int idx) {
			return (AliasScript)get(idx);
		}

		AliasScriptCollection getScriptCollection() {
			return _coll;
		}

		void addScript(AliasScript script) {
			_coll.addScript(script);
			addElement(script);
		}

		void removeScript(AliasScript script) {
			_coll.removeScript(script);
			removeElement(script);
		}
	}
}
