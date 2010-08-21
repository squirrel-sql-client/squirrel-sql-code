/*
 * Copyright (C) 2008 Rob Manning
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

import javax.swing.text.JTextComponent;

/**
 * Class for handling key events during editing of both JTextField and JTextArea.  This can be used by plugins
 * that provide custom string types, in order to take advantage of the &lt;null&gt; pattern.  This class came
 * from the DataTypeString default IDataTypeComponent supplied by SQuirreL for editing VARCHAR-like fields.  
 */
public class StringFieldKeyTextHandler extends BaseKeyTextHandler
{
	/** The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;
	
	/** the number of characters allowed in this field */
	private int _columnSize;
	
	/** whether nulls are allowed or not */
	private boolean _isNullable;
	
   /** Service for subclasses to use to notify the user audibly of a mistake */
   private IToolkitBeepHelper _beepHelper = null;

		
	public StringFieldKeyTextHandler(IRestorableTextComponent component, int columnSize, boolean isNullable, IToolkitBeepHelper beepHelper) {
		_textComponent = component;
		_columnSize = columnSize;
		_isNullable = isNullable;
		_beepHelper = beepHelper;
	}
	
	// special handling of operations while editing Strings
	public void keyTyped(KeyEvent e)
	{
		char c = e.getKeyChar();

		// as a coding convenience, create a reference to the text component
		// that is typecast to JTextComponent. this is not essential, as we
		// could typecast every reference, but this makes the code cleaner
		JTextComponent _theComponent = (JTextComponent) _textComponent;
		String text = _theComponent.getText();

		// ?? Is there any way to check for invalid input? Valid input includes
		// ?? at least any printable character, but could it also include unprintable
		// ?? characters?

		// check for max size reached (only works when DB provides non-zero scale info
		if (_columnSize > 0 && text.length() >= _columnSize 
				&& c != KeyEvent.VK_BACK_SPACE
				&& c != KeyEvent.VK_DELETE)
		{
			// max size reached
			e.consume();
			_beepHelper.beep(_theComponent);

			// Note: tabs and newlines are allowed in string fields, even though they are unusual.
		}

		// handle cases of null
		// The processing is different when nulls are allowed and when they are not.
		//

		if (_isNullable)
		{

			// user enters something when field is null
			if (text.equals(BaseDataTypeComponent.NULL_VALUE_PATTERN))
			{
				if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
				{
					// delete when null => original value
					_textComponent.restoreText();
					e.consume();
				}
				else
				{
					// non-delete when null => clear field and add text
					_textComponent.updateText("");
					// fall through to normal processing of this key stroke
				}
			}
			else
			{
				// for strings, a "blank" field is allowed, so only
				// switch to null when there is nothing left in the field
				// and user does delete
				if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
				{
					if (text.length() == 0)
					{
						// about to delete last thing in field, so replace with null
						_textComponent.updateText(BaseDataTypeComponent.NULL_VALUE_PATTERN);
						e.consume();
					}
				}
			}
		}
		else
		{
			// field is not nullable
			//
			handleNotNullableField(text, c, e, _textComponent);
		}
	}
}
