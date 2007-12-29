package net.sourceforge.squirrel_sql.plugins.refactoring.gui.util;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * A document that only holds numeric values.
 */
public class NumberDocument extends PlainDocument implements Document {

    private static final long serialVersionUID = 8245201344439966381L;
    private int maxLength = -1;
    private String valid = "-0123456789";
    private boolean fractional = false;


    /**
     * Create an instance with no maximum length.
     */
    public NumberDocument() {
    }


    /**
     * Create an instance with a maximum <code>length</code>.
     *
     * @param length maximum length
     */
    public NumberDocument(int length) {
        this.maxLength = length;
    }


    /**
     * Create an instance with a maximum <code>length</code> and special characters that are allowed.
     *
     * @param length       maximum length
     * @param specialValid special characters that are allowed
     */
    public NumberDocument(int length, String specialValid) {
        this.maxLength = length;
        valid += specialValid;
    }


    /**
     * Create an instance with a maximum <code>length</code> that allowes floating point numbers.
     *
     * @param length     maximum length
     * @param fractional true enables floating point numbers, false allowes only integers.
     */
    public NumberDocument(int length, boolean fractional) {
        this.maxLength = length;
        this.fractional = fractional;
        if (this.fractional) valid += ".";
    }


    /**
     * Inserts some content into the document. Inserting content causes a write lock to be held while the actual
     * changes are taking place, followed by notification to the observers on the thread that grabbed the write lock.
     * <p/>
     * This override checks if <code>string</code> contains only valid characters. Default: numbers 0-9
     *
     * @param i            the starting offset >= 0
     * @param string       the string to insert; does nothing with null/empty strings
     * @param attributeSet the attributes for the inserted content
     * @throws javax.swing.text.BadLocationException
     *          the given insert position is not a valid position within the document
     */
    public void insertString(int i, String string, AttributeSet attributeSet) throws BadLocationException {
        if (maxLength > -1 && getLength() == maxLength) return;
        for (char c : string.toCharArray()) {
            if (valid.indexOf(c) == -1 || (fractional && c == '.' && getText(0, getLength()).indexOf(c) != -1)) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }
        super.insertString(i, string, attributeSet);
    }
}


