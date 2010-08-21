package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.CancelStatementThread;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;


/**
 * The dialog to ask the user to wait.
 * 
 * @author Thorsten Mürell
 */
public class PleaseWaitDialog extends DialogWidget implements ActionListener {
	private static final long serialVersionUID = 8870277695490954084L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(PleaseWaitDialog.class);
	
	private JButton cancelButton;
	private IMessageHandler messageHandler;
	private Statement stmt;
	
	/**
	 * Creates the dialog.
	 * 
	 * @param stmt The statement that is currently executed
    * @param app The message handler to produce the log output to
    */
	public PleaseWaitDialog(Statement stmt, IApplication app) {
        //i18n[PleaseWaitDialog.queryExecuting=Query is executing]
		super(stringMgr.getString("PleaseWaitDialog.queryExecuting"), true, app);
		this.messageHandler = app.getMessageHandler();
		this.stmt = stmt;

		makeToolWindow(true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
        pack();
	}
	
	private Component createMainPanel()
	{

		final FormLayout layout = new FormLayout(
			// Columns
			"center:pref",
			// Rows
			"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int y = 1;
		builder.addSeparator(getTitle(), cc.xywh(1, y, 1, 1));

		y += 2;
		//i18n[PleaseWaitDialog.pleaseWait=Please wait while the query is executed]
		builder.addLabel(stringMgr.getString("PleaseWaitDialog.pleaseWait"), cc.xy(1, y));

		y += 2;
		builder.addSeparator("", cc.xywh(1, y, 1, 1));

		//i18n[PleaseWaitDialog.cancel=Cancel]
		cancelButton = new JButton(stringMgr.getString("PleaseWaitDialog.cancel"));
		cancelButton.addActionListener(this);
		
		y += 2;
		builder.add(cancelButton, cc.xywh(1, y, 1, 1));

		return builder.getPanel();
	}
	
	public void actionPerformed(ActionEvent e) {
	      if (stmt != null) {
	         CancelStatementThread cst = new CancelStatementThread(stmt, messageHandler);
	         cst.tryCancel();
	      }
	}
	
	/**
	 * Shows the dialog in front of all windows and centered.
	 *  
	 * @param app The application to show the window in
	 */
	public void showDialog(IApplication app) {
        app.getMainFrame().addWidget(this);
        moveToFront();
        setLayer(JLayeredPane.MODAL_LAYER);
        DialogWidget.centerWithinDesktop(this);
        this.setVisible(true);
	}
}
