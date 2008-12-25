package net.sourceforge.squirrel_sql.plugins.sqlparam.gui;
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


/**
 * The dialog to ask the user for a value.
 * 
 * @author Thorsten Mürell
 */
public class AskParamValueDialog extends DialogWidget
{
	private static final long serialVersionUID = 3470927611018381204L;


	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(AskParamValueDialog.class);
	
	private OkClosePanel btnsPnl = new OkClosePanel();
	private JTextField value = new JTextField();
	private JCheckBox quote = new JCheckBox();
	private String parameter = null;
	private String oldValue = null;
	
	private boolean done = false;
	private boolean cancelled = false;
	
	/**
	 * Creates the dialog.
	 * 
	 * @param parameter The name of the parameter to replace.
    * @param oldValue The old value of the parameter to provide as a default.
    * @param application
    */
	public AskParamValueDialog(String parameter, String oldValue, IApplication application) {
        //i18n[sqlparam.inputParameterValues=Please input the parameter values]
		super(stringMgr.getString("sqlparam.inputParameterValues"), true, application);
		this.parameter = parameter;
		this.oldValue = oldValue;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		makeToolWindow(true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
		btnsPnl.makeOKButtonDefault();
		btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());
        pack();
	}
	
	/**
	 * @return <code>true</code> if the dialog is done
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * If the user doesn't want to input a value, he hits the close
	 * button. The this method returns true.
	 * 
	 * @return <code>true</code> if the dialog was cancelled by the user.
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Sets the value of the dialog.
	 * 
	 * @param defaultValue the value to set as a default in this dialog.
	 */
	public void setValue(String defaultValue) {
		value.setText(defaultValue);
	}
	
	/**
	 * Gets the value of the input field in this dialog.
	 * 
	 * @return The value for the parameter.
	 */
	public String getValue() {
		return value.getText();
	}
	
	/**
	 * Returns if quotes around the value are needed.
	 * 
	 * @return <code>true</code> if quoting is needed.
	 */
	public boolean isQuotingNeeded() {
		return quote.isSelected();
	}
	
	private void updateCheckbox() {
		boolean isNumber = false;
		
		try {
			Float.parseFloat(value.getText());
			isNumber = true;
		} catch (NumberFormatException nfe) { 
			isNumber = false;
		}
		
		if (isNumber) {
			quote.setSelected(false);
			quote.setEnabled(true);
		} else {
			quote.setSelected(true);
			quote.setEnabled(false);
		}
	}
	
	private Component createMainPanel()
	{
		value.setColumns(20);
		value.setText(oldValue);
		value.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
			public void insertUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
			public void removeUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
		});
		updateCheckbox();
		btnsPnl.addListener(new MyOkClosePanelListener());

		final FormLayout layout = new FormLayout(
			// Columns
			"right:pref, 8dlu, left:min(100dlu;pref):grow",
			// Rows
			"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 3dlu, pref, 3dlu, pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int y = 1;
		builder.addSeparator(getTitle(), cc.xywh(1, y, 3, 1));

		y += 2;
		//i18n[sqlparam.valueFor=Value for {0}]
		builder.addLabel(stringMgr.getString("sqlparam.valueFor", parameter), cc.xy(1, y));
		builder.add(value, cc.xywh(3, y, 1, 1));

		y += 2;
		//i18n[sqlparam.quoteValues=Quote Values]
		builder.addLabel(stringMgr.getString("sqlparam.quoteValues"), cc.xy(1, y));
		builder.add(quote, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addSeparator("", cc.xywh(1, y, 3, 1));

		y += 2;
		builder.add(btnsPnl, cc.xywh(1, y, 3, 1));

		return builder.getPanel();
	}
	
	/**
	 * Cancels the dialog.
	 */
	public void cancel() {
		done = true;
		cancelled = true;
		dispose();
	}

	/**
	 * Confirms the dialog.
	 */
	public void ok() {
		done = true;
		dispose();
	}


	
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		/**
		 * Callback for the ok key.
		 * 
		 * @param evt the event
		 */
		public void okPressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.ok();
		}

		/**
		 * Callback for the close key.
		 * 
		 * @param evt the event
		 */
		public void closePressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.cancel();
		}

		/**
		 * Callback for the cancel key.
		 * 
		 * @param evt the event
		 */
		public void cancelPressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.cancel();
		}
	}
	
}
