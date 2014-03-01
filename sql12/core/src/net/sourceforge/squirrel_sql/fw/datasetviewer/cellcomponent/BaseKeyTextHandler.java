package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
/*
 * Copyright (C) 2006 Rob Manning
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.apache.commons.lang.StringUtils;

public class BaseKeyTextHandler extends KeyAdapter {
    
    /**
     * A flag to keep track of whether or not this is the first time we've seen
     * an empty text value or the first time since previously restoring the 
     * value.
     */
    boolean firstBlankText = true;
    
    
    /**
     * If the field is not allowed to have nulls, we need to let the 
     * user erase the entire contents of the field so that they can enter 
     * a brand-new value from scratch.  While the empty field is not a legal
     * value, we cannot avoid allowing it.  This is the normal editing behavior,
     * so we do not need to add anything special here except for the cyclic
     * re-entering of the original data if user hits delete when field is empty
     * 
     * @param text
     * @param c
     * @param e
     * @param _textComponent
     */
    protected void handleNotNullableField(String text, 
                                          char c,
                                          KeyEvent e,
                                          IRestorableTextComponent _textComponent)
    {
        if (text.length() == 0) {
            
            // We want to detect two empty text values in a row before 
            // we decide to restore the original value.  Since 'text'
            // contains the new value, we want to allow the first empty
            // 'text' so that the user can clear the field, and on the 
            // second one we will set it to the original value.
            if (firstBlankText) {
                firstBlankText = false;
                return;                            
            } else {
                firstBlankText = true;
            }                        
            
            if ( c==KeyEvent.VK_BACK_SPACE 
                    || c == KeyEvent.VK_DELETE) 
            {
                // delete when null => original value
                _textComponent.restoreText();
                e.consume();
            }
        }
    }
    
	/**
	 * Ensure, that a sign character is entered at the right position.
	 * Valid characters for a sign is the <code>-</code>. If the corresponding {@link ColumnDisplayDefinition#isSigned()} is 
	 * <code>false</code>, than a sign is not allowed for this column.
	 * Otherwise, a sign character is allowed at the first position of the text if there is not already a sign char present.
	 * <B>If the entered sign character is not valid, then the keyEvent will be consumed and a beep will be processed.</B>
	 * <p>For example:</p>
	 * Allowed:
	 * <li>42</li>
	 * <li>-42</li>
	 * Not allowed:
	 * <li>-.42</li>
	 * <li>--42</li>
	 * <li>42-42</li>
	 * <li>-42-42</li>
	 * <li>42-</li>
	 * @param keyEvent KeyEvent, which insert a character
	 * @param textComponent Text component, where the character was typed.
	 * @param colDef Display definition of the affected column. This will be used, for finding out, if the data type can be signed.
	 * @param beepHelper Helper for beeping, if a sign char is would be inserted at a wrong position.
	 */
	protected void checkSignCharacter(KeyEvent keyEvent, JTextComponent textComponent, ColumnDisplayDefinition colDef, IToolkitBeepHelper beepHelper) {
		char c = keyEvent.getKeyChar();
		String text = textComponent.getText();
		
		if ( isSignCharacter(c)){
			boolean ok = true;
			if(colDef.isSigned() == false){
				ok = false;
			}else if(!text.equals("<null>") && text.length() != 0){
				int caretPosition = textComponent.getCaretPosition();
				if(caretPosition != 0 || isSignCharacter(text.charAt(0))){
					ok = false;
				}
			}
			if(ok == false){
				/*
				 *  user entered '+' or '-' at a bad place,
				 *  Maybe not at the first position, or there is not a numeric char at the beginning - maybe we have already a sign
				 */
				beepHelper.beep(textComponent);
				keyEvent.consume();
			}
		}
	}
	
	/**
	 * Checks, if the specified character is a <code>-</code>;
	 * @param c Character, which should be checked.
	 * @return true, if the character is a sign character, otherwise false
	 */
	protected boolean isSignCharacter(char c){
		if(c == '-'){
			return true;
		}else{
			return false;
		}
	}
}
