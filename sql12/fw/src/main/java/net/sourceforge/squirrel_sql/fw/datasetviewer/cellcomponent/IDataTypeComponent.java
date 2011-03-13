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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author gwg
 * 
 * These are the calls needed to support the various ways of displaying and
 * editing data for each data type.
 * 
 * 
 */
public interface IDataTypeComponent {
    /**
     * Return the name of the Java class used to store this data type in the
     * application.
     */
    public String getClassName();

    /**
     * Determine if two objects of this data type contain the same value.
     * Neither of the objects is null.
     */
    public boolean areEqual(Object obj1, Object obj2);

    /*
     * Cell related methods come next.
     */

    /**
     * Convert the given object into its printable String value for use in Text
     * output and the in-cell representations (CellRenderer and CellEditor).
     */
    public String renderObject(Object object);

    /**
     * Returns true if data type may be edited within a table cell, false if
     * not.
     */
    public boolean isEditableInCell(Object originalValue);

    /**
     * See if a value in a column has been limited in some way and needs to be
     * re-read before being used for editing. For read-only tables this may
     * actually return true since we want to be able to view the entire contents
     * of the cell even if it was not completely loaded during the initial table
     * setup.
     */
    public boolean needToReRead(Object originalValue);

    /**
     * Get the JTextField component for this data type to be used in a
     * CellEditor. The value of the text field is set by the JTable mechanism
     * using the same mechanism as the renderer. The Assumption here is that the
     * CellEditor uses the same string representation as the CellRenderer.
     */
    public JTextField getJTextField();

    /**
     * Validate that the contents of a cell is in the right form for this data
     * type and convert that text into an object of the correct (Java) type for
     * the column Ideally this should be a static function, but the mechanics of
     * using CellComponentFactory and the constraints of the Java language make
     * that difficult.
     */
    public Object validateAndConvert(String value, Object originalValue,
            StringBuffer messageBuffer);

    /**
     * If true, this tells the PopupEditableIOPanel to use the binary editing
     * panel rather than a pure text panel. The binary editing panel assumes the
     * data is an array of bytes, converts it into text form, allows the user to
     * change how that data is displayed (e.g. Hex, Decimal, etc.), and converts
     * the data back from text to bytes when the user editing is completed. If
     * this returns false, this DataType class must convert the internal data
     * into a text string that can be displayed (and edited, if allowed) in a
     * TextField or TextArea, and must handle all user key strokes related to
     * editing of that data.
     */
    public boolean useBinaryEditingPanel();

    /*
     * Now the Popup-related methods. These are not quite symmetric with the
     * in-cell calls because the conversion of the object into the text to
     * display in the popup is handled internally to the DataType object inside
     * getJTextArea(), so we do not need a "renderObjectInPopup" function
     * visible to the rest of the world.
     */

    /**
     * Returns true if data type may be edited in the popup, false if not.
     */
    public boolean isEditableInPopup(Object originalValue);

    /**
     * Get the JTextArea component for this data type to be used in the
     * CellPopupDialog and fill in the initial value in the appropriate
     * representation. That representation may be the same as is renderObject(),
     * or it may be different (e.g. a BLOB may have renderObject=>"<BLOB>" but
     * fill in the actual value in the Popup TextArea).
     */
    public JTextArea getJTextArea(Object value);

    /**
     * Validate that the contents of a cell is in the right form for this data
     * type and convert that text into an object of the correct (Java) type for
     * the column Ideally this should be a static function, but the mechanics of
     * using CellComponentFactory and the constraints of the Java language make
     * that difficult.
     */
    public Object validateAndConvertInPopup(String value, Object originalValue,
            StringBuffer messageBuffer);

    /*
     * DataBase-related functions
     */

    /**
     * On input from the DB, read the data from the ResultSet into the
     * appropriate type of object to be stored in the table cell.
     */
    public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
            throws java.sql.SQLException;

    /**
     * When updating the database, generate a string form of this object value
     * that can be used in the WHERE clause to match the value in the database.
     * A return value of null means that this column cannot be used in the WHERE
     * clause, while a return of "null" (or "is null", etc) means that the
     * column can be used in the WHERE clause and the value is actually a null
     * value. This function must also include the column label so that its
     * output is of the form: "columnName = value" or "columnName is null" or
     * whatever is appropriate for this column in the database.
     * @see IWhereClausePart
     */
    public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md);

    /**
     * When updating the database, insert the appropriate datatype into the
     * prepared statement at the given variable position.
     */
    public void setPreparedStatementValue(PreparedStatement pstmt,
            Object value, int position) throws java.sql.SQLException;

    /**
     * Get a default value for the table used to input data for a new row to be
     * inserted into the DB.
     */
    public Object getDefaultValue(String dbDefaultValue);

    /*
     * File IO related functions
     */

    /**
     * Say whether or not object can be exported to and imported from a file. We
     * put both export and import together in one test on the assumption that
     * all conversions can be done both ways.
     */
    public boolean canDoFileIO();

    /**
     * Read a file and construct a valid object from its contents. Errors are
     * returned by throwing an IOException containing the cause of the problem
     * as its message.
     * <P>
     * DataType is responsible for validating that the imported data can be
     * converted to an object, and then must return a text string that can be
     * used in the Popup window text area. This object-to-text conversion is the
     * same as is done by the DataType object internally in the getJTextArea()
     * method.
     */
    public String importObject(FileInputStream inStream) throws IOException;

    /**
     * Read a file and construct a valid object from its contents. Errors are
     * returned by throwing an IOException containing the cause of the problem
     * as its message.
     * <P>
     * DataType is responsible for validating that the given text text from a
     * Popup JTextArea can be converted to an object. This text-to-object
     * conversion is the same as validateAndConvertInPopup, which may be used
     * internally by the object to do the validation.
     * <P>
     * The DataType object must flush and close the output stream before
     * returning. Typically it will create another object (e.g. an
     * OutputWriter), and that is the object that must be flushed and closed.
     */
    public void exportObject(FileOutputStream outStream, String text)
            throws IOException;

    /**
     * Sets the display definition of the Column being operated upon.
     * 
     * @param def the ColumnDisplayDefinition that describes the column in the 
     *            db table.
     */
    public void setColumnDisplayDefinition(ColumnDisplayDefinition def);

    /**
     * Sets the JTable of which holds data rendered by this DataTypeComponent.
     * 
     * @param table a JTable component
     */
    public void setTable(JTable table);
    
    /**
     * Sets the utility that allows the component to notify the user audibly when there is a 
     * problem with input data.
     * 
     * @param helper
     */
    public void setBeepHelper(IToolkitBeepHelper helper);
}
