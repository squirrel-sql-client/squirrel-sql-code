package net.sourceforge.squirrel_sql.client.session.properties;
/*
 *
 * Adapted from WhereClausePanel.java by Maury Hammel.
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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


/**
 * This panel allows the user to select specific columns from a specific table for use in
 * the WHERE clause when editing a cell in a table.  This is useful if the table has a large number
 * of columns and the WHERE clause generated using all the columns exceeds the DBMS limit.
 */
@SuppressWarnings("serial")
public class EditWhereColsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(EditWhereColsPanel.class);

    /** Logger for this class. */
    private static final ILogger s_log =
        LoggerController.createLogger(EditWhereColsPanel.class);    

	/** The name of the database table the Where clause applies to. */
	private String _tableName;
	
	/** The name of the table including the URL **/
	private String _unambiguousTableName;
	
	/** The list of all possible columns in the table **/
	private SortedSet<String> _columnList;
	
	/** The list of "to use" column names as seen by the user **/
	private JList useColsList;
	
	/** The list of "to NOT use" column names as seen by the user **/
	private JList notUseColsList;
	
	/** The list of column names to use as calculated when window is created **/
	private Object[] initalUseColsArray;
	
	/** The list of column names to NOT use as calculated when window is created **/
	private Object[] initalNotUseColsArray;
    
	private ISession _session = null;
    
    private PrimaryKeyInfo[] primaryKeyInfos = null; 
    
    EditWhereCols _editWhereCols = new EditWhereCols();
    
	/**
	 * ?? this should be changed to use the I18N file mechanism.
	 */
	interface EditWhereColsPanelI18N {
		// i18n[editWhereColsPanel.limitColsInCell=Limit Columns in Cell Edit]
		String TITLE = s_stringMgr.getString("editWhereColsPanel.limitColsInCell");
		// i18n[editWhereColsPanel.limitColsInCellHint=Limit columns used in WHERE clause when editing table]
		String HINT = s_stringMgr.getString("editWhereColsPanel.limitColsInCellHint");
        // i18n[editWhereColsPanel.usePKLabel=Use PK]
        String USE_PK = s_stringMgr.getString("editWhereColsPanel.usePKLabel");

	}
	
	/**
	 * Create a new instance of a WhereClausePanel.
	 *
	 * @param	columnList	A list of column names for the database table.
	 * @param	tableName	The name of the database table that the filter
	 * 						information will apply to.
	 * @param unambiguousTableName The name of the table including the URL 
	 * 				to the specific DBMS
	 *
	 * @throws	IllegalArgumentException
	 *			The exception thrown if invalid arguments are passed.
	 */
	public EditWhereColsPanel(ISession session,
                              ITableInfo ti,                   
                              SortedSet<String> columnList, 
                              String unambiguousTableName)
		throws IllegalArgumentException
	{
		super();
		_session = session;
        _editWhereCols.setApplication(session.getApplication());
        getPrimaryKey(ti);
		// save the input for use later
		_columnList = columnList;
		_tableName = ti.getQualifiedName();
		_unambiguousTableName = unambiguousTableName;
		
		// look up the table in the EditWhereCols list
		HashMap<String, String> colsTable = EditWhereCols.get(unambiguousTableName);
		
		if (colsTable == null) {
			// use all of the columns
			initalUseColsArray = _columnList.toArray();
			initalNotUseColsArray = new Object[0];
		}
		else {
			// use just the columns listed in the table, and set the not-used cols to the ones
			// that are not mentioned in the table
			SortedSet<Object> initialUseColsSet = new TreeSet<Object>( );
			SortedSet<Object> initialNotUseColsList = new TreeSet<Object>();
			
			Iterator<String> it = _columnList.iterator();
			while (it.hasNext()) {
				Object colName = it.next();
				if (colsTable.get(colName) != null)
					initialUseColsSet.add(colName);
				else initialNotUseColsList.add(colName);
			}
			initalUseColsArray = initialUseColsSet.toArray();
			initalNotUseColsArray = initialNotUseColsList.toArray();
		}

		// create all of the gui objects now
		createGUI();
	}

	private void getPrimaryKey(ITableInfo ti) {
        try {
            primaryKeyInfos = _session.getMetaData().getPrimaryKey(ti);
        } catch (SQLException e) {
            s_log.error(
               "Unexpected exception while attempting to get primary key info" +
               " for table "+ti.getQualifiedName()+": "+e.getMessage(), e);
        }
    }
    
	/**
	 * Get the title of the panel.
	 *
	 * @return	Return a string containing the title of the panl.
	 */
	public String getTitle()
	{
		return EditWhereColsPanelI18N.TITLE;
	}

	/**
	 * Get the hint text associated with the panel.
	 *
	 * @return A String value containing the hint text associated with the panel.
	 */
	public String getHint()
	{
		return EditWhereColsPanelI18N.HINT;
	}

	/**
	 * Reset the panel to the contents at the time we started editing
	 * (as set in initialize).
	 * 
	 */
	public void reset() {	
		useColsList.setListData(initalUseColsArray);
		notUseColsList.setListData(initalNotUseColsArray);
	}
	
	/**
	 * Put the current data into the EditWhereCols table.
	 */
	public boolean ok() {
        
		// if all cols are in the "to use" side, delete from EditWhereCols
		if (notUseColsList.getModel().getSize() == 0) {
			_editWhereCols.put(_unambiguousTableName, null);
		}
		else {
			// some cols are not to be used
			ListModel useColsModel = useColsList.getModel();
			
			// do not let user remove everything from the list
			if (useColsModel.getSize() == 0) {
				JOptionPane.showMessageDialog(this,
					// i18n[editWhereColsPanel.cannotRemoveAllCols=You cannot remove all of the fields from the 'use columns' list.]
					s_stringMgr.getString("editWhereColsPanel.cannotRemoveAllCols"));
				return false;
			}
			
			// create the HashMap of names to use and put it in EditWhereCols
			HashMap<String, String> useColsMap = 
                new HashMap<String, String>(useColsModel.getSize());
			
			for (int i=0; i< useColsModel.getSize(); i++) {
				useColsMap.put((String)useColsModel.getElementAt(i), 
                               (String)useColsModel.getElementAt(i));
			}
			
			_editWhereCols.put(_unambiguousTableName, useColsMap);
		}
		return true;
	}
	
	/**
	 * Move selected fields from "used" to "not used"
	 */
	private void moveToNotUsed() {
		
		// get the values from the "not use" list and convert to sorted set
		ListModel notUseColsModel = notUseColsList.getModel();
		SortedSet<String> notUseColsSet = new TreeSet<String>();
		for (int i=0; i<notUseColsModel.getSize(); i++)
			notUseColsSet.add((String)notUseColsModel.getElementAt(i));
		
		// get the values from the "use" list
		ListModel useColsModel = useColsList.getModel();
		
		// create an empty set for the "use" list
		SortedSet<Object> useColsSet = new TreeSet<Object>();

		// for each element in the "use" set, if selected then add to "not use",
		// otherwise add to new "use" set
		for (int i=0; i<useColsModel.getSize(); i++) {
			String colName = (String)useColsModel.getElementAt(i);
			if (useColsList.isSelectedIndex(i))
				notUseColsSet.add(colName);
			else useColsSet.add(colName);
		}
		
		useColsList.setListData(useColsSet.toArray());
		notUseColsList.setListData(notUseColsSet.toArray());
	}
	
	/**
	 * Move selected fields from "not used" to "used"
	 */
	private void moveToUsed() {
		// get the values from the "use" list and convert to sorted set
		ListModel useColsModel = useColsList.getModel();
		SortedSet<String> useColsSet = new TreeSet<String>();
		for (int i=0; i<useColsModel.getSize(); i++)
			useColsSet.add((String)useColsModel.getElementAt(i));
		
		// get the values from the "not use" list
		ListModel notUseColsModel = notUseColsList.getModel();
		
		// create an empty set for the "not use" list
		SortedSet<Object> notUseColsSet = new TreeSet<Object>();

		// for each element in the "not use" set, if selected then add to "use",
		// otherwise add to new "not use" set
		for (int i=0; i<notUseColsModel.getSize(); i++) {
			String colName = (String)notUseColsModel.getElementAt(i);
			if (notUseColsList.isSelectedIndex(i))
				useColsSet.add(colName);
			else notUseColsSet.add(colName);
		}
		
		useColsList.setListData(useColsSet.toArray());
		notUseColsList.setListData(notUseColsSet.toArray());
	}
	
    private void usePK() {
        if (primaryKeyInfos == null || primaryKeyInfos.length <= 0) {
            // i18n[editWhereColsPanel.noPK=The table ''{0}'' doesn't have a primary key.]
            String msg = 
                s_stringMgr.getString("editWhereColsPanel.noPK", _tableName);
            JOptionPane.showMessageDialog(this,msg);
            
            return;
        }
        HashSet<String> pkCols = new HashSet<String>();
        for (int i = 0; i < primaryKeyInfos.length; i++) {
            PrimaryKeyInfo pkInfo = primaryKeyInfos[i];
            pkCols.add(pkInfo.getColumnName());
        }
        
        ArrayList<String> newNotUseList = new ArrayList<String>();
        ListModel useColsModel = useColsList.getModel();
        ListModel notUseColsModel = notUseColsList.getModel();
        
        for (int i=0; i<useColsModel.getSize(); i++) {
            Object colName = useColsModel.getElementAt(i);
            if (!pkCols.contains(colName)) {
                newNotUseList.add(colName.toString());
            }
        }        
        
        for (int i=0; i<notUseColsModel.getSize(); i++) {
            Object colName = notUseColsModel.getElementAt(i);
            if (!pkCols.contains(colName)) {
                newNotUseList.add(colName.toString());
            }
        }
        
        useColsList.setListData(pkCols.toArray());
        notUseColsList.setListData(newNotUseList.toArray());
        
        
    }
	
	/**
	 * Create the GUI elements for the panel.
	 */
	private void createGUI()
	{

		JPanel useColsPanel = new JPanel(new BorderLayout());
		// i18n[editWhereColsPanel.useColumns=Use Columns]
		useColsPanel.add(new JLabel(s_stringMgr.getString("editWhereColsPanel.useColumns")), BorderLayout.NORTH);
		useColsList = new JList(initalUseColsArray);
		JScrollPane scrollPane = new JScrollPane(useColsList);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		useColsPanel.add(scrollPane, BorderLayout.SOUTH);
		add(useColsPanel);

		JPanel moveButtonsPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(3,1));
//????? if desired, get fancy and use icons in buttons instead of text ?????????
		JButton moveToNotUsedButton = new JButton("=>");
		moveToNotUsedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				moveToNotUsed();
			}
 		});
		buttonPanel.add(moveToNotUsedButton);
		JButton moveToUsedButton = new JButton("<=");
		moveToUsedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				moveToUsed();
			}
			});
		buttonPanel.add(moveToUsedButton);

        JButton usePKButton = new JButton(EditWhereColsPanelI18N.USE_PK);
        usePKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                usePK();
            }
        });
        buttonPanel.add(usePKButton);
        
		moveButtonsPanel.add(buttonPanel, BorderLayout.CENTER);
		add(moveButtonsPanel);
	  
		JPanel notUseColsPanel = new JPanel(new BorderLayout());
		// i18n[editWhereColsPanel.notUseColumns=Not Use Columns]
		notUseColsPanel.add(new JLabel(s_stringMgr.getString("editWhereColsPanel.notUseColumns")), BorderLayout.NORTH);
		notUseColsList = new JList(initalNotUseColsArray);
 		JScrollPane notUseScrollPane = new JScrollPane(notUseColsList);
		notUseScrollPane.setPreferredSize(new Dimension(200, 200));
		notUseColsPanel.add(notUseScrollPane, BorderLayout.SOUTH);
		add(notUseColsPanel);
	}
}
