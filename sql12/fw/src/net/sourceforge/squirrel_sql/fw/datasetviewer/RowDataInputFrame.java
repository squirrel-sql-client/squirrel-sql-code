package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Modifications copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * @author gwg
 *
 * This is the frame that gets data from the user for creating
 * a new row in a table.
 */
public class RowDataInputFrame extends JInternalFrame
	implements ActionListener {
	
	// object that called us and that we want to return data to when done
	DataSetViewerEditableTablePanel _caller;
	
	/**
	 * ctor.
	 */
	public RowDataInputFrame(ColumnDisplayDefinition[] colDefs,
		DataSetViewerEditableTablePanel caller) {
		
		super("Input New Row Data", true, true, false, true);
		
		// get the ConentPane into a variable for convenience
		Container pane = getContentPane();
		
		// save data passed in to us	
		_caller = caller;
		
		// set layout
		pane.setLayout(new BorderLayout());
		
		// create the JTable for input and put in the top of window
//?????????	
		
		// create the buttons for input done and cancel
		JPanel buttonPanel = new JPanel();
		
		JButton insertButton = new JButton("Insert");
		buttonPanel.add(insertButton);
		insertButton.setActionCommand("insert");
		insertButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		pane.add(buttonPanel, BorderLayout.SOUTH);

		// this frame should really go away when done
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		// display the frame
		pack();
		show();
		
	}

	/**
	 * Handle actions on the buttons 
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("cancel")) {
//??? nullify JTable
			setVisible(false);
			dispose();
			return;
		}
		else if ( ! e.getActionCommand().equals("insert")) {
			return;	// do not recognize this button request
		}
		
		// user said to insert, so collect all the data from the
		// JTable and send it to the DataSetViewer for insertion
		// into DB and on-screen tables
		
//???? collect the data - maybe by using getRow() on the JTable?
//?? Need to do validation first?

//?? TEMP CODE?????????????????
Object[] rowData = new Object[4];
rowData[0] = new Integer(100);
rowData[1] = new Byte("10");
rowData[2] = new Short("11");
rowData[3] = new Long(12);

		// put the data into the DB and the on-screen JTable.
		// If there was a failure, do not make this form
		// go away since the user may be able to fix the problem
		// by changing the data.
		if (_caller.insertRow(rowData) == null) {
			// the insert worked, so make this input form go away
			setVisible(false);
			dispose();
		}
	}

}
