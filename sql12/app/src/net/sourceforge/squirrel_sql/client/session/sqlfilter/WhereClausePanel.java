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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This panel allows the user to change the where clause for a Contents tab query.
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class WhereClausePanel implements ISQLFilterPanel
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(WhereClausePanel.class);    
    
	/** A class containing the information about the SQL filters. */
	private SQLFilterClauses _sqlFilterClauses = new SQLFilterClauses();

	/** The actual GUI panel that allows user to do the maintenance. */
	private WhereClauseSubPanel _myPanel;

	/**
	 * Create a new instance of a WhereClausePanel.
	 *
	 * @param	columnList	A list of column names for the database table.
	 * @param	textColumns	A collection of column names that are "text"
	 * 						columns.
	 * @param	tableName	The name of the database table that the filter
	 * 						information will apply to.
	 *
	 * @throws	IllegalArgumentException
	 *			The exception thrown if invalid arguments are passed.
	 */
	public WhereClausePanel(SortedSet<String> columnList, 
	                        Map<String, Boolean> textColumns, 
							String tableName)
		throws IllegalArgumentException
	{
		super();
		_myPanel = new WhereClauseSubPanel(columnList, textColumns, tableName);
	}

	/**
	 * Initialize the components of the WhereClausePanel.
	 *
	 * @param	sqlFilterClauses	An instance of a class containing information
	 *								about SQL filters already in place for the table.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if an invalid argument is passed.
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
	 */
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	/**
	 * Get the title of the panel.
	 *
	 * @return	Return a string containing the title of the panl.
	 */
	public String getTitle()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.WHERE_CLAUSE;
	}

	/**
	 * Get the hint text associated with the panel.
	 *
	 * @return A String value containing the hint text associated with the panel.
	 */
	public String getHint()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.HINT;
	}

	/**
	 * Update the current session with any changes to the SQL filter
	 * information.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges(_sqlFilterClauses);
	}

	/**
	 * A private class that makes up the bulk of the GUI for the panel.
	 */
	private static final class WhereClauseSubPanel extends JPanel
	{
        private static final long serialVersionUID = 1L;

        /**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface WhereClauseSubPanelI18n
		{
		    //i18n[WhereClausePanel.columnLabel=Columns]
		    String COLUMNS = 
		        s_stringMgr.getString("WhereClausePanel.columnLabel");
		    //i18n[WhereClausePanel.operatorsLabel=Operators]
		    String OPERATORS = 
		        s_stringMgr.getString("WhereClausePanel.operatorsLabel");
		    //i18n[WhereClausePanel.valueLabel=Value]            
		    String VALUE = s_stringMgr.getString("WhereClausePanel.valueLabel");
		    //i18n[WhereClausePanel.whereClauseLabel=Where Clause]            
		    String WHERE_CLAUSE = 
		        s_stringMgr.getString("WhereClausePanel.whereClauseLabel");
		    //i18n[WhereClausePanel.hint=Where clause for the selected table]            
		    String HINT = s_stringMgr.getString("WhereClausePanel.hint");
		    //i18n[WhereClausePanel.addLabel=Add]            
		    String ADD = s_stringMgr.getString("WhereClausePanel.addLabel");
            // The following strings are SQL tokens and should therefore *not*
            // be internationalized
		    String AND = "AND";                 // No I18N
		    String OR = "OR";                   // No I18N            
		    String LIKE = "LIKE";               // No I18N            
		    String IN = "IN";                   // No I18N            
		    String IS_NULL = "IS NULL";         // No I18N             
		    String IS_NOT_NULL = "IS NOT NULL"; // No I18N
		}

		/**
		 * A JComboBox component containing a list of the names of the
		 * columns for the current table.
		 */
		private JComboBox _columnCombo;

		/** A label to identify the column combo box. */
		private JLabel _columnLabel = new JLabel(WhereClauseSubPanelI18n.COLUMNS);

		/**
		 * A JComboBox containing a list of valid operators used in SQL Where clause
		 * expressions.
		 */
		private OperatorTypeCombo _operatorCombo = new OperatorTypeCombo();

		/** A label to identify the operator combo box. */
		private JLabel _operatorLabel = new JLabel(WhereClauseSubPanelI18n.OPERATORS);

		/** A field used to enter the right-hand side of a WhereClause expression. */
		private JTextField _valueField = new JTextField(10);

		/** A label to identify the valueField text area. */
		private JLabel _valueLabel = new JLabel(WhereClauseSubPanelI18n.VALUE);

		/** A JComboBox used to list Where clause connectors. */
		private AndOrCombo _andOrCombo = new AndOrCombo();

		/** A label to identify the andor combo box. */
		private JLabel _andOrLabel = new JLabel(" ");

		/** A text area used to contain all of the information for the Where clause. */
		private JTextArea _whereClauseArea = new JTextArea(10, 40);

		/**
		 * A button used to add information from the combo boxes and text fields into the
		 * Where clause text area.
		 */
		private JButton _addTextButton = new JButton(WhereClauseSubPanelI18n.ADD);

		/** The name of the database table the Where clause applies to. */
		private String _tableName;

		/** A List containing the names of the text columns */
		private Map<String, Boolean> _textColumns;

		/**
		 * A JPanel used for a bulk of the GUI elements of the panel.
		 *
		 * @param	columnList	A list of the column names for the table.
		 * @param	tableName	The name of the database table.
		 */
		WhereClauseSubPanel(SortedSet<String> columnList, 
		                    Map<String, Boolean> textColumns,
						    String tableName)
		{
			_tableName = tableName;
			_columnCombo = new JComboBox(columnList.toArray());
			_textColumns = textColumns;
			createGUI();
		}

		/**
		 * Load existing clause information into the panel.
		 *
		 * @param	sqlFilterClauses	An instance of a class containing
		 * 								SQL Filter information for the current table.
		 *
		 */
		void loadData(SQLFilterClauses sqlFilterClauses)
		{
			_whereClauseArea.setText(
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
				_whereClauseArea.getText());
		}

		/**
		 * Create the GUI elements for the panel.
		 */
		private void createGUI()
		{
         setLayout(new GridBagLayout());


         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         add(createControlsPanel(), gbc);

         _whereClauseArea.setBorder(BorderFactory.createEtchedBorder());
         _whereClauseArea.setLineWrap(true);
         JScrollPane sp = new JScrollPane(_whereClauseArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


         gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         add(sp, gbc);
		}


      private JPanel createControlsPanel()
      {

         JPanel ret = new JPanel(new GridBagLayout());
         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_andOrLabel, gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_andOrCombo, gbc);



         gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_columnLabel, gbc);

         gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_columnCombo, gbc);



         gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_operatorLabel, gbc);

         gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4,4,4,4),0,0);
         ret.add(_operatorCombo, gbc);



         gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_valueLabel, gbc);

         gbc = new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4,4,4,4),0,0);
         ret.add(_valueField, gbc);



         gbc = new GridBagConstraints(4,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4,4,4,4),0,0);
         ret.add(_addTextButton, gbc);


         _addTextButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent evt)
            {
               addTextToClause();
            }
         });

         return ret;
      }

      private static final class OperatorTypeCombo extends JComboBox
		{
            private static final long serialVersionUID = 1L;

            OperatorTypeCombo()
			{
				addItem("=");
				addItem("<>");
				addItem(">");
				addItem("<");
				addItem(">=");
				addItem("<=");
				addItem(WhereClauseSubPanelI18n.IN);
				addItem(WhereClauseSubPanelI18n.LIKE);
				addItem(WhereClauseSubPanelI18n.IS_NULL);
				addItem(WhereClauseSubPanelI18n.IS_NOT_NULL);
			}
		}

		private static final class AndOrCombo extends JComboBox
		{
            private static final long serialVersionUID = 1L;

            AndOrCombo()
			{
				addItem(WhereClauseSubPanelI18n.AND);
				addItem(WhereClauseSubPanelI18n.OR);
			}
		}

		/**
		 * Combine the information entered in the combo boxes
		 * and the text field and add it to the Where clause information.
		 */
		private void addTextToClause()
		{
			String value = _valueField.getText();
			String operator = (String)_operatorCombo.getSelectedItem();
			if (((value != null) && (value.length() > 0))
					|| ((operator.equals(WhereClauseSubPanelI18n.IS_NULL))
					|| 	(operator.equals(WhereClauseSubPanelI18n.IS_NOT_NULL))))
			{
				String andOr = (String)_andOrCombo.getSelectedItem();
				String column = (String)_columnCombo.getSelectedItem();

				// Put the 'AND' or the 'OR' in front of the clause if
				// there are already values in the text area.
				if (_whereClauseArea.getText().length() > 0)
				{
					_whereClauseArea.append("\n" + andOr + " ");
				}

				// If the operator is 'IN' and there are no parenthesis
				// around the value, put them there.
				if (operator.equals(WhereClauseSubPanelI18n.IN)
					&& (!value.trim().startsWith("(")))
				{
					value = "(" + value + ")";
				}

				// If the column is a text column, and there aren't single quotes around the value, put them there.

				else if ((value != null) && (value.length() > 0)) 
				{
					if (_textColumns.containsKey(column)
							&& (!value.trim().startsWith("'")))
					{
						value = "'" + value + "'";
					}
				}
				_whereClauseArea.append(column + " " + operator);

				if ((value != null) && (value.length() > 0)) 
				{
					_whereClauseArea.append(" " + value);
				}
			}
			_valueField.setText("");
		}

		/**
		 * Erase all information for the current filter.
		 */
		public void clearFilter()
		{
			_whereClauseArea.setText("");
		}
	}

	/**
	 * Erase any information for the appropriate filter.
	 */
	public void clearFilter()
	{
		_myPanel.clearFilter();
	}

	/**
	 * Get a value that uniquely identifies this SQL filter clause.
	 *
	 * @return Return a String value containing an identifing value.
	 */
	public static String getClauseIdentifier()
	{
		return WhereClauseSubPanel.WhereClauseSubPanelI18n.WHERE_CLAUSE;
	}
}
