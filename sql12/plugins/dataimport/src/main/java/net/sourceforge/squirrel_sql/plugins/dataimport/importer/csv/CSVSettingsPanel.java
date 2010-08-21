package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv;
/*
 * Copyright (C) 2007 Thorsten Mürell
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.charset.Charset;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class contains the panel for the CSV settings.
 * 
 * @author Thorsten Mürell
 */
public class CSVSettingsPanel extends JPanel {
	private static final long serialVersionUID = 5532627733371852354L;
	
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(CSVSettingsPanel.class);
	
	private CSVSettingsBean settings = null;
	
	private JTextField seperatorChar = null;
	private JTextField dateFormat = null;
	private JRadioButton useChar = null;
	private JRadioButton useTab = null;
	private JComboBox encoding = null;
	
	/**
	 * Standard constructor
	 * 
	 * @param settings
	 */
	public CSVSettingsPanel(CSVSettingsBean settings) {
		this.settings = settings;
		init();
		loadSettings();
	}
	
	private void init() {
		// TODO: Tooltips
		ActionListener stateChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CSVSettingsPanel.this.stateChanged();
			}
		};
		KeyListener keyStateChangedListener = new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) { CSVSettingsPanel.this.stateChanged(); }
		};
		seperatorChar = new JTextField(1);
		seperatorChar.addActionListener(stateChangedListener);
		seperatorChar.addKeyListener(keyStateChangedListener);
		//i18n[CSVSettingsPanel.seperatorCharToolTip=Specify the character that is used to seperate the columns in this file (e.g. ',' or ';')]
		seperatorChar.setToolTipText(stringMgr.getString("CSVSettingsPanel.seperatorCharToolTip"));
		dateFormat = new JTextField(20);
		dateFormat.addActionListener(stateChangedListener);
		dateFormat.addKeyListener(keyStateChangedListener);
		//i18n[CSVSettingsPanel.useTab=Tab seperated]
		useTab = new JRadioButton(stringMgr.getString("CSVSettingsPanel.useTab"));
		//i18n[CSVSettingsPanel.useChar=Seperated by character:]
		useChar = new JRadioButton(stringMgr.getString("CSVSettingsPanel.useChar"));
		useChar.setSelected(true);
		useTab.addActionListener(stateChangedListener);
		useChar.addActionListener(stateChangedListener);
		encoding = new JComboBox();
		for (String c : Charset.availableCharsets().keySet()) {
			encoding.addItem(c);
		}
		encoding.addActionListener(stateChangedListener);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(useTab);
		bg.add(useChar);
		
		
		final FormLayout layout = new FormLayout(
				// Columns
				"pref, 6dlu, pref, 12dlu, pref:grow",
				// Rows
				"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		int y = 1;
		//i18n[CSVSettingsPanel.csvSettings=CSV settings]
		builder.addSeparator(stringMgr.getString("CSVSettingsPanel.csvSettings"), cc.xywh(1, y, 5, 1));
		
		y += 2;
		builder.add(useChar, cc.xy(1, y));
		builder.add(seperatorChar, cc.xy(3,y));
		builder.add(useTab, cc.xy(5, y));

		y += 2;
		//i18n[CSVSettingsPanel.inputFileEncoding=Input file encoding]
		builder.add(new JLabel(stringMgr.getString("CSVSettingsPanel.inputFileEncoding")), cc.xywh(1, y, 3, 1));
		builder.add(encoding, cc.xy(5, y));
		
		y += 2;
		//i18n[CSVSettingsPanel.dateFormat=Date format]
		builder.add(new JLabel(stringMgr.getString("CSVSettingsPanel.dateFormat")), cc.xywh(1, y, 3, 1));
		builder.add(dateFormat, cc.xy(5, y));
		
		add(builder.getPanel());
	}
	
	private void applySettings() {
		if (useTab.isSelected()) {
			settings.setSeperator('\t');
		} else {
			if (seperatorChar.getText().length() > 0) {
				settings.setSeperator(seperatorChar.getText().charAt(0));
			} else {
				settings.setSeperator(';');
			}
		}
		settings.setImportCharset(Charset.forName(encoding.getSelectedItem().toString()));
		settings.setDateFormat(dateFormat.getText());
	}
	
	private void loadSettings() {
		if (settings.getSeperator() == '\t') {
			useTab.setSelected(true);
		} else {
			useChar.setSelected(true);
			seperatorChar.setText(Character.toString(settings.getSeperator()));
		}
		dateFormat.setText(settings.getDateFormat());
		encoding.setSelectedItem(settings.getImportCharset().name());
	}
	
	private void stateChanged() {
		if (seperatorChar.getText().length() > 1) {
			try {
				seperatorChar.setText(seperatorChar.getText(0, 1));
			} catch (Exception e) { /* Ignore that */ }
		}
		if (useTab.isSelected()) {
			seperatorChar.setEnabled(false);
		} else {
			seperatorChar.setEnabled(true);
		}
		applySettings();
	}
}
