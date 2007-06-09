package net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel;
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
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxl.Workbook;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This panel holds the excel specific settings for the importer.
 * 
 * @author Thorsten Mürell
 */
public class ExcelSettingsPanel extends JPanel {
	private static final long serialVersionUID = -649828189112224370L;
	
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ExcelSettingsPanel.class);
	
	private ExcelSettingsBean settings = null;
	private Workbook wb = null;
	
	private JComboBox sheetName = null;
	
	/**
	 * The standard constructor
	 * 
	 * @param settings The settings holder
	 * @param f The import file.
	 */
	public ExcelSettingsPanel(ExcelSettingsBean settings, File f) {
		this.settings = settings;
		try {
			this.wb = Workbook.getWorkbook(f);
		} catch (Exception e) {
			this.wb = null;
		}
		init();
		loadSettings();
	}
	
	private void init() {
		ActionListener stateChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExcelSettingsPanel.this.stateChanged();
			}
		};
		sheetName = new JComboBox();
		if (wb != null) {
			for (String name : wb.getSheetNames()) {
				sheetName.addItem(name);
			}
		}
		sheetName.addActionListener(stateChangedListener);
		
		
		final FormLayout layout = new FormLayout(
				// Columns
				"pref, 6dlu, pref:grow",
				// Rows
				"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		int y = 1;
		//i18n[ExcelSettingsPanel.xlsSettings=Excel import settings]
		builder.addSeparator(stringMgr.getString("ExcelSettingsPanel.xlsSettings"), cc.xywh(1, y, 3, 1));
		
		y += 2;
		//i18n[ExcelSettingsPanel.sheetName=Sheet name]
		builder.add(new JLabel(stringMgr.getString("ExcelSettingsPanel.sheetName")), cc.xy(1, y));
		builder.add(sheetName, cc.xy(3, y));
		
		add(builder.getPanel());
	}
	
	
	private void applySettings() {
		if (sheetName.getSelectedItem() != null) {
			settings.setSheetName(sheetName.getSelectedItem().toString());
		}
	}
	
	private void loadSettings() {
		if (settings.getSheetName() == null) {
			sheetName.setSelectedIndex(0);
		} else {
			sheetName.setSelectedItem(settings.getSheetName());
		}
	}
	
	private void stateChanged() {
		applySettings();
	}
	
}
