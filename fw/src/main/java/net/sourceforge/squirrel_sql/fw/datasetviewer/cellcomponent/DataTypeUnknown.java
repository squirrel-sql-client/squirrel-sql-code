package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.BorderFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;

/**
 * @author gwg
 *
 * This class provides the display components for handling unknown,
 * non-standard, or not-handled-by-SQuirreL data types.
 * <P>
 * The default SQuirreL code can handle only JDBC-standard defined data types.
 * Since this data type represents DBMS-specific enhancements or
 * user-defined data types, we cannot do anything intelligent with the data.
 * We allow the user to select one of two modes of operation:
 * <DL>
 * <LI>
 * we can try to get the contents of the DB element and print it as a string, or,
 * <LI>
 * we will display an appropriately internationalized version of "<Unknown>".
 * </DL>
 * In either case, the data will be stored and processed as a String.
 * <P>
 * The user may not edit the contents of this field in either the cell or popup
 * because we do not understand the structure or limitations of the contents,
 * and therefore cannot validate it or put it back into the DB.
 * The field is not used in the WHERE clause because we do not know whether
 * or not it might contain binary data, and because we do not know how to
 * format the data for SQL operations.
 * <P>
 * To handle these data types more intelligently and allow editing on them,
 * DBMS-specific plug-ins will need to be developed to register handlers
 * for instances of this type.
 */

public class DataTypeUnknown extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;
	
	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;
	
	/** Internationalized strings for this class, shared/copied from ResultSetReader. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeUnknown.class);
	
	/* The CellRenderer used for this data type */
	//??? For now, use the same renderer as everyone else.
	//??
	//?? IN FUTURE: change this to use a new instance of renederer
	//?? for this data type.
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();



	/**
	 * Name of this class, which is needed because the class name is needed
	 * by the static method getControlPanel, so we cannot use something
	 * like getClass() to find this name.
	 */
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeUnknown";

	/*
	 * Properties settable by the user
	 */
	 // flag for whether we have already loaded the properties or not
	 private static boolean propertiesAlreadyLoaded = false;
	 
		 
	/** Read the contents of Other from Result sets when first loading the tables. */
	private static boolean _readUnknown = false;
	
	

	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeUnknown(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		loadProperties();
	}
	
	/** Internal function to get the user-settable properties from the DTProperties,
	 * if they exist, and to ensure that defaults are set if the properties have
	 * not yet been created.
	 * <P>
	 * This method may be called from different places depending on whether
	 * an instance of this class is created before the user brings up the Session
	 * Properties window.  In either case, the data is static and is set only
	 * the first time we are called.
	 */
	private static void loadProperties() {
		
		//set the property values
		// Note: this may have already been done by another instance of
		// this DataType created to handle a different column.
		if (propertiesAlreadyLoaded == false) {
			// get parameters previously set by user, or set default values
			_readUnknown = false;	// set to the default
			String readUnknownString = DTProperties.get(
				thisClassName, "readUnknown");
			if (readUnknownString != null && readUnknownString.equals("true"))
				_readUnknown = true;

			propertiesAlreadyLoaded = true;
		}
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
	 * For Unknown, this will always be a string.
	 */
	public String getClassName() {
		return "java.lang.String";
	}

	/*
	 * First we have the methods for in-cell and Text-table operations
	 */
	 
	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}
	
	/**
	 * This Data Type can be edited in a table cell.
	 */
	public boolean isEditableInCell(Object originalValue) {
		return false;	
	}

	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(Object originalValue) {
		// this DataType does not limit the data read during the initial load of the table,
		// so there is no need to re-read the complete data later
		return false;
	}
	
	/**
	 * Return a JTextField usable in a CellEditor.
	 */
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();
		
		// special handling of operations while editing this data type
		((RestorableJTextField)_textComponent).addKeyListener(new KeyTextHandler());
				
		//
		// handle mouse events for double-click creation of popup dialog.
		// This happens only in the JTextField, not the JTextArea, so we can
		// make this an inner class within this method rather than a separate
		// inner class as is done with the KeyTextHandler class.
		//
		((RestorableJTextField)_textComponent).addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
						(RestorableJTextField)DataTypeUnknown.this._textComponent,
						evt, DataTypeUnknown.this._table);
					CellDataPopup.showDialog(DataTypeUnknown.this._table,
						DataTypeUnknown.this._colDef, tableEvt, true);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}

	/**
	 * Implement the interface for validating and converting to internal object.
	 * Since we do not know how to convert Unknown objects,
	 * just return null with no error in the messageBuffer
	 */
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		return null;
	}

	/**
	 * If true, this tells the PopupEditableIOPanel to use the
	 * binary editing panel rather than a pure text panel.
	 * The binary editing panel assumes the data is an array of bytes,
	 * converts it into text form, allows the user to change how that
	 * data is displayed (e.g. Hex, Decimal, etc.), and converts
	 * the data back from text to bytes when the user editing is completed.
	 * If this returns false, this DataType class must
	 * convert the internal data into a text string that
	 * can be displayed (and edited, if allowed) in a TextField
	 * or TextArea, and must handle all
	 * user key strokes related to editing of that data.
	 */
	public boolean useBinaryEditingPanel() {
		return false;
	}
	 

	/*
	 * Now the functions for the Popup-related operations.
	 */
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup(Object originalValue) {
		return false;
	}

	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
		
		// value is a simple string representation of the data,
		// the same one used in Text and in-cell operations.
		((RestorableJTextArea)_textComponent).setText(renderObject(value));
		
		// special handling of operations while editing this data type
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());
		
		return (RestorableJTextArea)_textComponent;
	 }

	/**
	 * Validating and converting in Popup is identical to cell-related operation.
	 */
	public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer) {
		return validateAndConvert(value, originalValue, messageBuffer);
	}

	/*
	 * The following is used in both cell and popup operations.
	 */	
	
	/*
	 * Internal class for handling key events during editing
	 * of both JTextField and JTextArea.
	 * Since the Unknown data type is not editable either in the Cell or in the Popup,
	 * we should never get here, but we have provided appropriate code just in case.
	 */
	 private class KeyTextHandler extends KeyAdapter {
	 	public void keyTyped(KeyEvent e) {
				// as a coding convenience, create a reference to the text component
				// that is typecast to JTextComponent.  this is not essential, as we
				// could typecast every reference, but this makes the code cleaner
				JTextComponent _theComponent = (JTextComponent)DataTypeUnknown.this._textComponent;
				_beepHelper.beep(_theComponent);
				e.consume();
			}
		}


	
	
	/*
	 * DataBase-related functions
	 */
	 
	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		String data = null;
		if (_readUnknown)
		{
			// Running getObject on a java class attempts
			// to load the class in memory which we don't want.
			// getString() just gets the value without loading
			// the class (at least under PostgreSQL).
			//row[i] = _rs.getObject(index);
			data = rs.getString(index);
		}
		else
		{
			data = s_stringMgr.getString("DataTypeUnknown.unknown",
			                             _colDef.getSqlType() );
		}
		
		if (rs.wasNull())
			return null;
		else return data;
	}

	/**
	 * When updating the database, generate a string form of this object value
	 * that can be used in the WHERE clause to match the value in the database.
	 * A return value of null means that this column cannot be used in the WHERE
	 * clause, while a return of "null" (or "is null", etc) means that the column
	 * can be used in the WHERE clause and the value is actually a null value.
	 * This function must also include the column label so that its output
	 * is of the form:
	 * 	"columnName = value"
	 * or
	 * 	"columnName is null"
	 * or whatever is appropriate for this column in the database.
	 */
	public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return new IsNullWhereClausePart(_colDef);
		else
			return new EmptyWhereClausePart();
	}
	
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at variable position 1.
	 * This function should never be called for type Unknown
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {

		throw new java.sql.SQLException("Can not update data of type OTHER");
	}
	
	/**
	 * Get a default value for the table used to input data for a new row
	 * to be inserted into the DB.
	 */
	public Object getDefaultValue(String dbDefaultValue) {
		if (dbDefaultValue != null) {
			// try to use the DB default value
			StringBuffer mbuf = new StringBuffer();
			Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);
			
			// if there was a problem with converting, then just fall through
			// and continue as if there was no default given in the DB.
			// Otherwise, use the converted object
			if (mbuf.length() == 0)
				return newObject;
		}
		
		// no default in DB.  If nullable, use null.
		if (_isNullable)
			return null;
		
		// field is not nullable, so create a reasonable default value
		// cannot create default value for unknown data type
		return null;
	}
	
	
	/*
	 * File IO related functions
	 */
	 
	 
	 /**
	  * Say whether or not object can be exported to and imported from
	  * a file.  We put both export and import together in one test
	  * on the assumption that all conversions can be done both ways.
	  * Since we do not understand data of type Unknown, we cannot safely
	  * export or import it.
	  */
	 public boolean canDoFileIO() {
	 	return false;
	 }
	 
	 /**
	  * Read a file and construct a valid object from its contents.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the imported
	  * data can be converted to an object, and then must return
	  * a text string that can be used in the Popup window text area.
	  * This object-to-text conversion is the same as is done by
	  * the DataType object internally in the getJTextArea() method.
	  * 
	  * <P>
	  * File is assumed to be and ASCII string of digits
	  * representing a value of this data type.
	  */
	public String importObject(FileInputStream inStream)
	 	throws IOException {
	 	
		throw new IOException("Can not import data type OTHER");
	}

	 	 
	 /**
	  * Construct an appropriate external representation of the object
	  * and write it to a file.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the given text
	  * text from a Popup JTextArea can be converted to an object.
	  * This text-to-object conversion is the same as validateAndConvertInPopup,
	  * which may be used internally by the object to do the validation.
	  * <P>
	  * The DataType object must flush and close the output stream before returning.
	  * Typically it will create another object (e.g. an OutputWriter), and
	  * that is the object that must be flushed and closed.
	  * 
	  * <P>
	  * File is assumed to be and ASCII string of digits
	  * representing a value of this data type.
	  */
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException {
	 	
		throw new IOException("Can not export data type OTHER");
	 }



	/*
	 * Property change control panel
	 */	  
	 
	 /**
	  * Generate a JPanel containing controls that allow the user
	  * to adjust the properties for this DataType.
	  * All properties are static accross all instances of this DataType. 
	  * However, the class may choose to apply the information differentially,
	  * such as keeping a list (also entered by the user) of table/column names
	  * for which certain properties should be used.
	  * <P>
	  * This is called ONLY if there is at least one property entered into the DTProperties
	  * for this class.
	  * <P>
	  * Since this method is called by reflection on the Method object derived from this class,
	  * it does not need to be included in the Interface.
	  * It would be nice to include this in the Interface for consistancy, documentation, etc,
	  * but the Interface does not seem to like static methods.
	  */
	 public static OkJPanel getControlPanel() {
	 	
		/*
		 * If you add this method to one of the standard DataTypes in the
		 * fw/datasetviewer/cellcomponent directory, you must also add the name
		 * of that DataType class to the list in CellComponentFactory, method
		 * getControlPanels, variable named initialClassNameList.
		 * If the class is being registered with the factory using registerDataType,
		 * then you should not include the class name in the list (it will be found
		 * automatically), but if the DataType is part of the case statement in the
		 * factory method getDataTypeObject, then it does need to be explicitly listed
		 * in the getControlPanels method also.
		 */
		 
		 // if this panel is called before any instances of the class have been
		 // created, we need to load the properties from the DTProperties.
		 loadProperties();
		 
		return new UnknownOkJPanel();
	 }
	 
	 
	 
	 /**
	  * Inner class that extends OkJPanel so that we can call the ok()
	  * method to save the data when the user is happy with it.
	  */
	 private static class UnknownOkJPanel extends OkJPanel {
        private static final long serialVersionUID = 1L;
        /*
		 * GUI components - need to be here because they need to be
		 * accessible from the event handlers to alter each other's state.
		 */
		// check box for whether to read contents during table load or not
		private JCheckBox _showUnknownChk = new JCheckBox(
			// i18n[dataTypeUnknown.readContentsOnLoad=Read contents when table is first loaded and display as string]
			s_stringMgr.getString("dataTypeUnknown.readContentsOnLoad"));


		public UnknownOkJPanel() {
		 	 
			/* set up the controls */
			// checkbox for read/not-read on table load
			_showUnknownChk.setSelected(_readUnknown);

			/*
			 * Create the panel and add the GUI items to it
			 */
 		
			setBorder(BorderFactory.createTitledBorder(
				// i18n[dataTypeUnknown.unknownTypes=Unknown DataTypes   (non-standard SQL type codes)]
				s_stringMgr.getString("dataTypeUnknown.unknownTypes")));

			add(_showUnknownChk);

		} // end of constructor for inner class
	 
	 
		/**
		 * User has clicked OK in the surrounding JPanel,
		 * so save the current state of all variables
		 */
		public void ok() {
			// get the values from the controls and set them in the static properties
			_readUnknown = _showUnknownChk.isSelected();
			DTProperties.put(
				thisClassName,
				"readUnknown", Boolean.valueOf(_readUnknown).toString());
		}
	 
	 } // end of inner class
}
