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

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
//??import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * @author gwg
 *
 * This class provides the display components for handling Integer data types,
 * specifically SQL types INTEGER, SMALLINT, and TINYINT.
 * The display components are for:
 * <UL>
 * <LI> read-only display within a table cell
 * <LI> editing within a table cell
 * <LI> read-only or editing display within a separate window
 * </UL>
 * The class also contains 
 * <UL>
 * <LI> a function to compare two display values
 * to see if they are equal.  This is needed because the display format
 * may not be the same as the internal format, and all internal object
 * types may not provide an appropriate equals() function.
 * <LI> a function to return a printable text form of the cell contents,
 * which is used in the text version of the table.
 * </UL>
 * <P>
 * The components returned from this class extend RestorableJTextField
 * and RestorableJTextArea for use in editing table cells that
 * contain Integer values.  It provides the special behavior for null
 * handling and resetting the cell to the original value.
 */

public class DataTypeInteger
	implements IDataTypeComponent
{
	/* the whole column definition */
	private ColumnDisplayDefinition _colDef;

	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* whether number is signed or unsigned */
	private boolean _isSigned;

	/* the number of decimal digits allowed in the number */
	private int _scale;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;
	
	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;
	
	/* The CellRenderer used for this data type */
	//??? For now, use the same renderer as everyone else.
	//??
	//?? IN FUTURE: change this to use a new instance of renederer
	//?? for this data type.
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeInteger(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_isSigned = colDef.isSigned();
		_scale = colDef.getScale();
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "java.lang.Integer";
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
	public boolean isEditableInCell() {
		return true;	
	}
	
	/**
	 * Return a JTextField usable in a CellEditor.
	 */
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();
		
		// special handling of operations while editing Integers
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
						(RestorableJTextField)DataTypeInteger.this._textComponent,
						evt, DataTypeInteger.this._table);
					CellDataPopup.showDialog(DataTypeInteger.this._table,
						DataTypeInteger.this._colDef, tableEvt);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}

	/**
	 * Implement the interface for validating and converting to Integer object.
	 * Null is a valid successful return, so errors are indicated only by
	 * existance or not of a message in the messageBuffer.
	 */
	public Object validateAndConvert(String value, StringBuffer messageBuffer) {
		// handle null, which is shown as the special string "<null>"
		if (value.equals("<null>"))
			return null;

		// Do the conversion into the object in a safe manner
		try {
			Object obj = new Integer(value);
			return obj;
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			//?? do we need the message also, or is it automatically part of the toString()?
			//messageBuffer.append(e.getMessage());
			return null;
		}
	}

	/*
	 * Now the functions for the Popup-related operations.
	 */
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup() {
		return true;
	}

	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
		
		// value is a simple string representation of the integer,
		// the same one used in Text and in-cell operations.
		((RestorableJTextArea)_textComponent).setText(renderObject(value));
		
		// special handling of operations while editing Integers
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());
		
		return (RestorableJTextArea)_textComponent;
	 }

	/**
	 * Validating and converting in Popup is identical to cell-related operation.
	 */
	public Object validateAndConvertInPopup(String value, StringBuffer messageBuffer) {
		return validateAndConvert(value, messageBuffer);
	}

	/*
	 * The following is used in both cell and popup operations.
	 */	
	
	/*
	 * Internal class for handling key events during editing
	 * of both JTextField and JTextArea.
	 */
	 private class KeyTextHandler extends KeyAdapter {
	 	public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				
				// as a coding convenience, create a reference to the text component
				// that is typecast to JTextComponent.  this is not essential, as we
				// could typecast every reference, but this makes the code cleaner
				JTextComponent _theComponent = (JTextComponent)DataTypeInteger.this._textComponent;
				String text = _theComponent.getText();
	
				// look for illegal chars
				if ( ! DataTypeInteger.this._isSigned && c == '-') {
					// cannot use '-' when unsigned
					_theComponent.getToolkit().beep();
					e.consume();
				}

				if (c == '-' && ! (text.equals("<null>") || text.length() == 0)) {
					// user entered '-' at a bad place
					_theComponent.getToolkit().beep();
					e.consume();
				}

				if ( ! ( Character.isDigit(c) ||
					(c == '-') ||
					(c == KeyEvent.VK_BACK_SPACE) ||
					(c == KeyEvent.VK_DELETE) ) ) {
					_theComponent.getToolkit().beep();
					e.consume();
				}

				// check for max size reached (only works when DB provides non-zero scale info
				if (DataTypeInteger.this._scale > 0 &&
					text.length() == DataTypeInteger.this._scale &&
					c != KeyEvent.VK_BACK_SPACE &&
					c != KeyEvent.VK_DELETE) {
					// max size reached
					e.consume();
					_theComponent.getToolkit().beep();
				}

				// handle cases of null
				// The processing is different when nulls are allowed and when they are not.
				//

				if ( DataTypeInteger.this._isNullable) {

					// user enters something when field is null
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							// delete when null => original value
							DataTypeInteger.this._textComponent.restoreText();
							e.consume();
						}
						else {
							// non-delete when null => clear field and add text
							DataTypeInteger.this._textComponent.updateText("");
							// fall through to normal processing of this key stroke
						}
					}
					else {
						// check for user deletes last thing in field
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								// about to delete last thing in field, so replace with null
								DataTypeInteger.this._textComponent.updateText("<null>");
								e.consume();
							}
						}
					}
				}
				else {
					// field is not nullable
					//
					// if the field is not allowed to have nulls, we need to let the
					// user erase the entire contents of the field so that they can enter
					// a brand-new value from scratch.  While the empty field is not a legal
					// value, we cannot avoid allowing it.  This is the normal editing behavior,
					// so we do not need to add anything special here except for the cyclic
					// re-entering of the original data if user hits delete when field is empty
					if (text.length() == 0 &&
						(c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						// delete when null => original value
						DataTypeInteger.this._textComponent.restoreText();
						e.consume();
					}
				}
			}
		}


}
