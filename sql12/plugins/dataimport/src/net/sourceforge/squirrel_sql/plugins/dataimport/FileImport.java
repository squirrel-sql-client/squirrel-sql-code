package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2001 Like Gao
 * lgao@gmu.edu
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
import java.awt.*;
import java.awt.event.*;
//import java.beans.*; Caused a compilation error in JDK1.4 (?) because Statement is in both java.sql and java.beans.
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

/**
 * Main form
 * Import csv file into database.
 *
 * 11/1/2001
 * @author Like Gao
 */


//import com.bigfoot.colbell.squirrel.sessionsheet.*;



public class FileImport extends JPanel implements ActionListener {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FileImport.class);

	 private ISession _session;
	 public static JFrame frame;


	 JButton showButton;
	 JButton readButton;
	 JButton createButton;


	 //    JTextField fileNameTxt;
	 JTextField tableNameTxt;
	 Label tableNameLbl;



	 public SortableTable mytable;
	 public CombTable settingTable;

	 JPanel buttonPanel;


	 public final static Dimension hpad10 = new Dimension(10,1);
	 public final static Dimension vpad10 = new Dimension(1,10);
	 public final static Insets insets = new Insets(5,10, 0, 10);
	 JFileChooser chooser;
	 public String[] names;
	 public Object[][] colAttribute;
	 public Object[][] data;

	 public JPanel tablePanel;
	 public JSplitPane jsp;
	 public JPanel infoPnl;


	 public FileImport(ISession session) {
	 _session = session;
	 setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	 chooser = new JFileChooser();


	 tableNameTxt = new JTextField();
	 tableNameTxt.setText("                              ");
	 tableNameTxt.setPreferredSize( new Dimension( 10,50) );

	 //  tableNameTxt.setSize(20,30);
	 //  tableNameTxt.setVisible(false);
	 // i18n[dataimport.tableName=Table Name]
	 tableNameLbl= new Label(s_stringMgr.getString("dataimport.tableName"));
	 //  tableNameTxt.setVisible(false);
	 infoPnl=new JPanel();
	 infoPnl.setLayout(new BoxLayout(infoPnl, BoxLayout.X_AXIS));
	 infoPnl.add(tableNameLbl);
	 infoPnl.add(tableNameTxt);
	 infoPnl.setVisible(false);





	 // Create show button
	 // i18n[dataimport.chooseFile=Choose File]
	 showButton = new JButton(s_stringMgr.getString("dataimport.chooseFile"));
	 showButton.addActionListener(this);
		  showButton.setMnemonic('s');

	 // i18n[dataimport.readFile=Read  File]
	 readButton = new JButton(s_stringMgr.getString("dataimport.readFile"));
	 readButton.addActionListener(this);
	 readButton.setMnemonic('R');
	 readButton.setEnabled(false);


	 // i18n[dataimport.createTable=Create Table]
	 createButton = new JButton(s_stringMgr.getString("dataimport.createTable"));
	 createButton.addActionListener(this);
	 createButton.setMnemonic('T');
	 createButton.setEnabled(false);



	 //Buttons
	 JPanel buttonPanel = new InsetPanel(insets);
	 buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	 buttonPanel.add(Box.createRigidArea(hpad10));
	 //  buttonPanel.add(new Label("Table Name"));
	 buttonPanel.add(infoPnl);
	 buttonPanel.add(Box.createRigidArea(hpad10));
	 buttonPanel.add(showButton);
	 buttonPanel.add(Box.createRigidArea(hpad10));
	 buttonPanel.add(readButton);
	 buttonPanel.add(Box.createRigidArea(hpad10));
	 buttonPanel.add(createButton);
	 buttonPanel.add(Box.createRigidArea(hpad10));



	 // ********************************************************
	 // ****************   Table            ********************
	 // ********************************************************


	 tablePanel = new JPanel();
	 //  tablePanel.setSize(600,300);
	 tablePanel.add(Box.createRigidArea(hpad10));
	 tablePanel.add(Box.createRigidArea(hpad10));
	 jsp = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT);
	 jsp.setContinuousLayout(true);
	 jsp.setOneTouchExpandable(true);


	 tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
	 tablePanel.add(jsp);
	 tablePanel.setVisible(false);



	 // ********************************************************
	 // ****************** Wrap 'em all up *********************
	 // ********************************************************
	  JPanel wrapper = new JPanel();
	 wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
	 add(Box.createRigidArea(vpad10));
	 wrapper.add(Box.createRigidArea(hpad10));
	 wrapper.add(buttonPanel);
	 wrapper.add(Box.createRigidArea(vpad10));
	 add(wrapper);


	 add(tablePanel);
	 }


	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == showButton)
		{ //click "choose file"
			// i18n[dataimport.import=Import]
			chooser.setApproveButtonText(s_stringMgr.getString("dataimport.import"));
			int retval = chooser.showDialog(frame, null);
			if (retval == JFileChooser.APPROVE_OPTION)
			{
				File theFile = chooser.getSelectedFile();
				if (theFile != null)
				{
					File [] files = chooser.getSelectedFiles();

					String msg;
					// i18n[dataimport.choseFile=You chose this file: {0}]
					msg = s_stringMgr.getString("dataimport.choseFile", chooser.getSelectedFile().getPath());
					JOptionPane.showMessageDialog(frame, msg);
					String filename = chooser.getSelectedFile().getPath();


					// i18n[dataimport.fileOpened=File:\n\"{0}\" opened]
					msg = s_stringMgr.getString("dataimport.fileOpened", filename);
					_session.getMessageHandler().showMessage(msg);

					//          fileNameTxt.setText(filename);
					filename = chooser.getSelectedFile().getName();
					tableNameTxt.setText(filename);
					readButton.setEnabled(true);
                    if (mytable != null) {
                        mytable.removeAll();
                        settingTable.removeAll();
                    }
					repaint();
					return;
				}
			}
			else if (retval == JFileChooser.CANCEL_OPTION)
			{
				// i18n[dataimport.cancelled=User cancelled operation. No file was chosen.]
				JOptionPane.showMessageDialog(frame, s_stringMgr.getString("dataimport.cancelled"));
			}
			else if (retval == JFileChooser.ERROR_OPTION)
			{
				// i18n[dataimport.fileError=An error occured. No file was chosen.]
				JOptionPane.showMessageDialog(frame, s_stringMgr.getString("dataimport.fileError"));
			}
			else
			{
				// i18n[dataimport.unknownOp=Unknown operation occured.]
				JOptionPane.showMessageDialog(frame, s_stringMgr.getString("dataimport.unknownOp"));
			}
		}
		else if (e.getSource() == readButton)
		{ //read file
			if (read_file())
			{
				tablePanel.setVisible(true);
				infoPnl.setVisible(true);
				createButton.setEnabled(true);
			}
			else
			{
				createButton.setEnabled(false);
				infoPnl.setVisible(false);
				tablePanel.setVisible(false);
			}
		}
		else
		{ //create and insert data
			if (createTable())
			{
				if (insertData())
				{
					// i18n[dataimport.insertsDone=All insertions are done!]
					_session.getMessageHandler().showMessage(s_stringMgr.getString("dataimport.insertsDone"));
				}
			}
		}
	}


	boolean createTable(){
	 String _createStr= new String();
	 try {
		  _createStr = "Create Table ";
		  _createStr = _createStr + tableNameTxt.getText() + "(INO INTEGER ,";
		  for(int i = 1; i< mytable.names.length; i++){
		  _createStr = _createStr +settingTable.data[i-1][1]+ " ";
		  _createStr = _createStr +settingTable.data[i-1][2]+ " , ";
		  }
		  _createStr =_createStr + "Primary key (INO))";

		  final Statement stmt = _session.getSQLConnection().createStatement();
		  try {
		  stmt.execute(_createStr);
		  _session.getMessageHandler().showMessage(_createStr);
		  _session.getMessageHandler().showMessage("Done!");
		  return true;
		  }
		  catch (Exception ee)
		  {_session.getMessageHandler().showErrorMessage(ee);
		  JOptionPane.showMessageDialog(frame,"Create table error:" +_createStr  );
		  return false;
		  }
	 }
	 catch (Exception ex) {
		  _session.getMessageHandler().showErrorMessage(ex);
		  return false;
	 }
}


boolean insertData(){
	 String _insertStr = new String();
	 String sourceStr = new String();
	 try {
		  _insertStr = "INSERT INTO " + tableNameTxt.getText() + " values (?";
		  for(int i = 1; i< mytable.names.length; i++)
		  _insertStr =  _insertStr + ",?";
		  _insertStr = _insertStr + ")";
		  final PreparedStatement pstmt = _session.getSQLConnection().prepareStatement(_insertStr);


		  for(int i = 0; i < mytable.data.length; i++){
		  int result_cp;
		  try {
				pstmt.setInt(1, i+1);                        //Index or INO
				for(int j = 1; j < mytable.names.length; j++){
				sourceStr = settingTable.data[j-1][2].toString();
				result_cp = sourceStr.compareTo("INTEGER");
				if (result_cp <0){
					 float ii = Float.parseFloat(mytable.data[i][j].toString());
					 pstmt.setFloat(j+1, ii);
				}
				else if (result_cp == 0){
					 int ii=  Integer.parseInt(mytable.data[i][j].toString());
					 pstmt.setInt( j+1, ii);
				}
				else{
					 pstmt.setString(j+1, (String)mytable.data[i][j]);
				}
				}
				pstmt.executeUpdate();
				_session.getMessageHandler().showMessage(Integer.toString(i+1)+" Done!");
		  }
		  catch (Exception ee){
				_session.getMessageHandler().showErrorMessage(ee);

			  // i18n[dataimport.insertError=Insert error at line: {0}]
				JOptionPane.showMessageDialog(frame,s_stringMgr.getString("dataimport.insertError", Integer.toString(i)));
				return false;
		  }
		  }
		  return true;
	 }
	 catch (Exception ex) {
		  _session.getMessageHandler().showErrorMessage(ex);
		  return false;
	 }
 }








	 boolean read_file(){
		  Vector    rowField = new Vector();
		  Vector    rowData = new Vector();
		  Vector    tempType = new Vector();
		  int noRow = 0;
		  int noColumn =0 ;

		  try
		  {StreamTokenizer stoken =  new StreamTokenizer(new FileReader(chooser.getSelectedFile().getPath()));
		  stoken.eolIsSignificant(true);   //line
		  rowField.add(Integer.toString(noRow));
		  tempType.add("1");       //0:number;or "String"
		  rowData.clear();
		  while (true){
				stoken.nextToken();
				if (stoken.ttype == StreamTokenizer.TT_EOF)
				break;
				else if (stoken.ttype ==StreamTokenizer.TT_EOL){
				rowData.add(rowField.clone());
				noRow++;
				rowField.clear();
				tempType.add("1");
				rowField.add(Integer.toString(noRow));
				}
				else if (stoken.ttype == StreamTokenizer.TT_WORD){ //String
				rowField.add(stoken.sval);
				tempType.add("String");
				}
				else if (stoken.ttype == StreamTokenizer.TT_NUMBER){ //number
				rowField.add(Double.toString(stoken.nval));
				tempType.add("1");
				}
				else if (stoken.sval != null){  //treat others as string
				rowField.add(stoken.sval);
				tempType.add("String");
				}
		  }
		  rowField =(Vector)rowData.get(0);
		  names = new String[rowField.size()];
		  data=new Object[rowData.size()-1][rowField.size()];
		  colAttribute = new Object[rowField.size()-1][3];

		  //Coulmn names
		  names[0]="Index";
		  for (int j = 1 ; j < rowField.size();j++  )
				{
				names[j] = (String)rowField.get(j);
				}
		  //get setting table
		  for (int j = 1 ; j < rowField.size();j++  )
				{
				colAttribute[j-1][0] = names[j];
				colAttribute[j-1][1] = names[j];
				int kk = j+rowField.size();
				try{
					int ss = Integer.parseInt((String)tempType.get(kk));
					colAttribute[j-1][2] = "FLOAT";
				}
				catch (Exception ss){
							colAttribute[j-1][2] = "VARCHAR(50)";
				}
				}
		  //Row Data
		  for ( int i = 1 ; i < rowData.size() ; i++ )
				{
				rowField =(Vector)rowData.get(i);
				for (int j = 0 ; j < rowField.size();j++  )
					 {
					 data[i-1][j] =(String)rowField.get(j);
					 }
				}

		  //try{
				mytable = new SortableTable(names,data);

				final String[] setnames =
					{
						// i18n[dataimport.origName=Original Name]
						s_stringMgr.getString("dataimport.origName"),
						// i18n[dataimport.newName=New Name]
						s_stringMgr.getString("dataimport.newName"),
						// i18n[dataimport.dataType=Data Type]
						s_stringMgr.getString("dataimport.dataType")
					};

				settingTable = new CombTable(setnames,colAttribute);
				jsp.setLeftComponent(settingTable);
				jsp.setRightComponent(mytable);
				repaint();
				return true;
		  }
		  catch (Exception ecp){
			  _session.getMessageHandler().showErrorMessage(ecp);
		  repaint();
		  return false;
		  }
		  //      repaint();
}



	 public class InsetPanel extends JPanel {
	 Insets i;
	 InsetPanel(Insets i) {
		  this.i = i;
	 }
	 public Insets getInsets() {
		  return i;
	 }
	 }
}
