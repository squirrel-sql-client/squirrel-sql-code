package net.sourceforge.squirrel_sql.client.session.sqlfilter;
/*
 * Copyright (C) 2003 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * Adapted from SessionSQLPropertiesPanel.java by Colin Bell.
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This panel allows the user to change the where clause for a Contents tab query.
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class OrderByClausePanel implements ISQLFilterPanel
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(OrderByClausePanel.class);
    
	/** A class containing the information about the SQL filters. */
	private SQLFilterClauses _sqlFilterClauses;

	/** The actual GUI panel that allows user to do the maintenance. */
	private OrderByClauseSubPanel _myPanel;

	/**
	 * Create a new instance of an OrderByClausePanel
	 *
	 * @param	columnList	A list of column names for the database table.
	 * @param	tableName	The name of the database table that the filter
	 * 						information will apply to.
	 */
	public OrderByClausePanel(SortedSet<String> columnList, String tableName)
	{
		_myPanel = new OrderByClauseSubPanel(columnList, tableName);
	}

	/**
	 * Initialize the components of the OrderByClausePanel.
	 *
	 * @param	sqlFilterClauses	An instance of a class containing information
	 * 								about SQL filters already in place for the table.
	 *
	 * @throws IllegalArgumentException Thrown if an invalid argument is passed.
	 *
	 */
	public void initialize(SQLFilterClauses sqlFilterClauses)
		throws IllegalArgumentException
	{
		if (sqlFilterClauses == null)
		{
			throw new IllegalArgumentException("Null sqlFilterClauses passed");
		}
		_sqlFilterClauses = sqlFilterClauses;
		_myPanel.loadData(_sqlFilterClauses);
	}

	/**
	 * Returns the panel created by the class.
	 *
	 * @return Return an instance of a WhereClauseSubPanel.
	 *
	 */
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	/**
	 * Get the title of the panel.
	 *
	 * @return Return a string containing the title of the panl.
	 */
	public String getTitle()
	{
		return OrderByClauseSubPanel.OrderByClausePanelI18n.ORDER_BY_CLAUSE;
	}

	/**
	 * Get the hint text associated with the panel.
	 *
	 * @return	A String value containing the hint text associated with the
	 * 			panel.
	 *
	 */
	public String getHint()
	{
		return OrderByClauseSubPanel.OrderByClausePanelI18n.HINT;
	}

	/**
	 * Update the current session with any changes to the SQL filter
	 * information.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges(_sqlFilterClauses);
	}

	/** A private class that makes up the bulk of the GUI for the panel. */
	private static final class OrderByClauseSubPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;

        /**
		 * This interface defines locale specific strings. This should be replaced with a property file.
		 */
		interface OrderByClausePanelI18n
		{

            // These must not be internationalized since SQL tokens must be in 
            // English
		    String ASC = "ASC";   //No I18N
		    String DESC = "DESC"; //No I18N

		    //[i18n[OrderByClausePanel.addLabel=Add]
		    String ADD = s_stringMgr.getString("OrderByClausePanel.addLabel");
		    //[i18n[OrderByClausePanel.columnsLabel=Columns]            
		    String COLUMNS = 
                s_stringMgr.getString("OrderByClausePanel.columnsLabel");
		    //[i18n[OrderByClausePanel.orderDirectionLabel=Order Direction]
		    String ORDER_DIRECTION = 
                s_stringMgr.getString("OrderByClausePanel.orderDirectionLabel");
		    //[i18n[OrderByClausePanel.orderByClauseLabel=Order By Clause]
		    String ORDER_BY_CLAUSE = 
                s_stringMgr.getString("OrderByClausePanel.orderByClauseLabel");
		    //[i18n[OrderByClausePanel.hint=Order by clause for the selected table]            
		    String HINT = s_stringMgr.getString("OrderByClausePanel.hint");
		}

		/** A label to identify the column combo box. */
		private JLabel _columnLabel =
			new JLabel(OrderByClausePanelI18n.COLUMNS);
		/** A JComboBox component containing a list of the names of the columns for the
		 * current table.
		 */
		private JComboBox _columnCombo;
		/** A label to identify the sort orders combo box. */
		private JLabel _orderLabel =
			new JLabel(OrderByClausePanelI18n.ORDER_DIRECTION);
		/** A JComboBox containing a list of the valid sort orders.
		 */
		private OrderCombo _orderCombo = new OrderCombo();
		/** A button used to add information from the combo boxes and text fields into the
		 * Where clause text area.
		 */
		private JButton _addButton = new JButton(OrderByClausePanelI18n.ADD);
		/** A text area used to contain all of the information for the Order By clause. */
		private JTextArea _orderClauseArea = new JTextArea(10, 40);
		/** The name of the database table the Where clause applies to. */
		private String _tableName;

		/** A JPanel used for a bulk of the GUI elements of the panel.
		 * @param columnList A list of the column names for the table.
		 * @param tableName The name of the database table.
		 *
		 */
		OrderByClauseSubPanel(SortedSet<String> columnList, String tableName)
		{
			_columnCombo = new JComboBox(columnList.toArray());
			_tableName = tableName;
			createUserInterface();
		}

		/** Load existing clause information into the panel.
		 * @param sqlFilterClauses An instance of a class containing SQL Filter information for the current table.
		 *
		 */
		void loadData(SQLFilterClauses sqlFilterClauses)
		{
			_orderClauseArea.setText(
				sqlFilterClauses.get(getClauseIdentifier(), _tableName));
		}

		/** Update the current SQuirreL session with any changes to the SQL filter
		 * information.
		 * @param sqlFilterClauses An instance of a class containing SQL Filter information for the current table.
		 *
		 */
		void applyChanges(SQLFilterClauses sqlFilterClauses)
		{
			sqlFilterClauses.put(
				getClauseIdentifier(),
				_tableName,
				_orderClauseArea.getText());
		}

		/** Create the GUI elements for the panel */
		private void createUserInterface()
		{
         setLayout(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         add(createControlsPanel(), gbc);

         _orderClauseArea.setLineWrap(true);
         JScrollPane sp = new JScrollPane(_orderClauseArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,	JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

         gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         add(sp, gbc);
		}

		/** Create a JPanel with GUI components.
		 * @return Returns a JPanel
		 *
		 */

      private JPanel createControlsPanel()
      {
         JPanel ret = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_columnLabel, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_columnCombo, gbc);


         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_orderLabel, gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_orderCombo, gbc);


         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_addButton, gbc);


         _addButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               addTextToClause();
            }
         });

         return ret;
      }

      /** Combine the information entered in the combo boxes and add it
		 * to the Order By clause information.
		 */
		private void addTextToClause()
		{
			String column = (String)_columnCombo.getSelectedItem();
			String order = (String)_orderCombo.getSelectedItem();
			if (_orderClauseArea.getText().length() > 0)
			{
				_orderClauseArea.append(",\n");
			}
			_orderClauseArea.append(column + " " + order);
		}

		/** Erase all information for the current filter. */
		public void clearFilter()
		{
			_orderClauseArea.setText("");
		}
	}

	private static final class OrderCombo extends JComboBox
	{
        private static final long serialVersionUID = 1L;

        OrderCombo()
		{
			super();
			addItem(OrderByClauseSubPanel.OrderByClausePanelI18n.ASC);
			addItem(OrderByClauseSubPanel.OrderByClausePanelI18n.DESC);
		}
	}

	/** Erase all information for the current filter. */
	public void clearFilter()
	{
		_myPanel.clearFilter();
	}

	/** Get a value that uniquely identifies this SQL filter clause.
	 * @return Return a String value containing an identifing value.
	 *
	 */
	public static String getClauseIdentifier()
	{
		return OrderByClauseSubPanel.OrderByClausePanelI18n.ORDER_BY_CLAUSE;
	}
}
