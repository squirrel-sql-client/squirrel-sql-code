/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A base class for DataTypeComponents with common behavior.
 * 
 * @author manningr
 */
public abstract class BaseDataTypeComponent implements IDataTypeComponent {

    /** Logger for this class. */
    private static ILogger s_log = 
        LoggerController.createLogger(BaseDataTypeComponent.class);    
    
    /** the whole column definition */
    protected ColumnDisplayDefinition _colDef;

    /** table of which we are part (needed for creating popup dialog) */
    protected JTable _table;
    
    /** A default renderer */
    protected DefaultColumnRenderer _renderer = 
        DefaultColumnRenderer.getInstance();
    
    /** The JTextComponent that is being used for editing */
    protected RestorableJTextField _textField;

    /** The JTextComponent that is being used for editing */
    protected RestorableJTextArea _textArea;
    
    /** Service for subclasses to use to notify the user audibly of a mistake */
    protected IToolkitBeepHelper _beepHelper = new ToolkitBeepHelper();
    
    /** The text value that is placed in the cell to indicate a null value */ 
    public static final String NULL_VALUE_PATTERN = "<null>";
    
    /**
     * Sets the display definition of the Column being operated upon.
     * 
     * @param def the ColumnDisplayDefinition that describes the column in the 
     *            db table.
     */
    public void setColumnDisplayDefinition(ColumnDisplayDefinition def) {
        this._colDef = def;
    }
    
    /**
     * Sets the JTable of which holds data rendered by this DataTypeComponent.
     * 
     * @param table a JTable component
     */
    public void setTable(JTable table) {
        _table = table;
    }
    
    /**
     * Return a JTextArea usable in the CellPopupDialog. This will use the
     * renderer to render the value as text and set this as the text for the
     * JTextArea that is returned.
     * 
     * @param value
     *        the value to set as text in the JTextArea.
     */
    public JTextArea getJTextArea(Object value) {
        _textArea = new RestorableJTextArea();
        _textArea.setText((String) _renderer.renderObject(value));

        // special handling of operations while editing this data type
        KeyListener keyListener = getKeyListener(_textArea);
        if (keyListener != null) {
            _textArea.addKeyListener(keyListener);
        }

        return _textArea;
    }
    
    /**
     * Return a JTextField usable in a CellEditor.
     */
    public JTextField getJTextField() {
        _textField = new RestorableJTextField();

        KeyListener keyListener = getKeyListener(_textField);
        if (keyListener != null) {
            // special handling of operations while editing this data type
            _textField.addKeyListener(keyListener);
        }
        
        // handle mouse events for double-click creation of popup dialog.
        // This happens only in the JTextField, not the JTextArea, so we can
        // make this an inner class within this method rather than a separate
        // inner class as is done with the KeyTextHandler class.
        //
        _textField.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                if (evt.getClickCount() == 2)
                {
                    MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
                        _textField,
                        evt, _table);
                    CellDataPopup.showDialog(_table, _colDef, tableEvt, true);
                }
            }
        }); // end of mouse listener
        
        
        return _textField;
    }    
    
    /**
     * Render a value into text for this DataType.
     */
    public String renderObject(final Object value) {
        String text = (String)_renderer.renderObject(value);
        return text;
    }    
    
    /**
     * Implement the interface for validating and converting to internal object.
     * Null is a valid successful return, so errors are indicated only by
     * existance or not of a message in the messageBuffer.
     */
    public Object validateAndConvert(final String value, 
                                     final Object originalValue, 
                                     final StringBuffer messageBuffer) {
        // handle null, which is shown as the special string "<null>"
        if (value.equals(NULL_VALUE_PATTERN)) {
            return null;
        }

        // Special case: the input is exactly the output
        return value;   
    }    
    
    /**
     * Validating and converting in Popup is identical to cell-related operation.
     */
    public Object validateAndConvertInPopup(String value, 
                                            Object originalValue, 
                                            StringBuffer messageBuffer) 
    {
        return validateAndConvert(value, originalValue, messageBuffer);
    }    
    
   /**
	 * If any custom key handling behavior is required, this can be set by sub-class implementations
	 * 
	 * @param component
	 *           the restorable text component that this key listener will be interacting with 
	 */
	protected KeyListener getKeyListener(IRestorableTextComponent component)
	{
		return null;
	}

    /**
		 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#exportObject(java.io.FileOutputStream,
		 *      java.lang.String)
		 */
    public void exportObject(FileOutputStream outStream, String text)
    throws IOException {
        OutputStreamWriter outWriter = null;
        try {
            outWriter = new OutputStreamWriter(outStream);
            outWriter.write(text);
            outWriter.flush();
            outWriter.close();
        } finally {
            if (outWriter != null) {
                try {
                    outWriter.close();
                } catch (IOException e) {
                    s_log.error("exportObject: Unexpected exception: "+e, e);
                }
            }
        }
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

        InputStreamReader inReader = new InputStreamReader(inStream);

        int fileSize = inStream.available();

        char charBuf[] = new char[fileSize];

        int count = inReader.read(charBuf, 0, fileSize);

        if (count != fileSize)
            throw new IOException(
                "Could read only "+ count +
                " chars from a total file size of " + fileSize +
                ". Import failed.");

        // convert file text into a string
        // Special case: some systems tack a newline at the end of
        // the text read.  Assume that if last char is a newline that
        // we want everything else in the line.
        String fileText;
        if (charBuf[count-1] == KeyEvent.VK_ENTER)
            fileText = new String(charBuf, 0, count-1);
        else fileText = new String(charBuf);

        // test that the string is valid by converting it into an
        // object of this data type
        StringBuffer messageBuffer = new StringBuffer();
        validateAndConvertInPopup(fileText, null, messageBuffer);
        if (messageBuffer.length() > 0) {
            // convert number conversion issue into IO issue for consistancy
            throw new IOException(
                "Text does not represent data of type "+getClassName()+
                ".  Text was:\n"+fileText);
        }

        // return the text from the file since it does
        // represent a valid data value
        return fileText;
   }

   /**
    * Sub-classes should override this if the data they handle cannot be 
    * represented as a String.
    * 
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getClassName()
    */
   public String getClassName() {
       return "java.lang.String";
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#setBeepHelper(net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IToolkitBeepHelper)
    */
   public void setBeepHelper(IToolkitBeepHelper helper) {
   	this._beepHelper = helper;
   }
   
   
   /**
    * This default implementation only uses the column in a where clause when it's value is null, which is 
    * believed to be safe for all types in all databases.
    *  
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#getWhereClauseValue(java.lang.Object, net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData)
    */
   public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
      if (value == null || value.toString() == null ) {
    	  return new IsNullWhereClausePart(_colDef);
     } else {
         return new EmptyWhereClausePart();
     }
   }
 
   /**
    * Default implementation which uses the equals() implementation in the specified objects.  This 
    * implementaion is safe for nulls.
    * 
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#areEqual(java.lang.Object, java.lang.Object)
    */
	public boolean areEqual(Object obj1, Object obj2) {
		if (obj1 != null) {
			return obj1.equals(obj2);
		}
		if (obj2 != null) {
			return obj2.equals(obj1);
		}
		// They are both null
		return true;
	}
}
