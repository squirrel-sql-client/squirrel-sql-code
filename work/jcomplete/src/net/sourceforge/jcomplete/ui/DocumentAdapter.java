/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 13.09.2002 22:37:51
 */
package net.sourceforge.jcomplete.ui;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import javax.swing.text.BadLocationException;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.TextProvider;

/**
 * This class mediates between the document and the parser through the
 * DocumentListener interface
 */
public class DocumentAdapter implements DocumentListener, TextProvider
{
    private CompletionHandler m_handler;
    private Document m_document;

    public DocumentAdapter(CompletionHandler handler, Document document)
    {
        m_handler = handler;
        m_document = document;

        m_handler.begin(this, document.getLength());
    }

    public void insertUpdate(DocumentEvent e)
    {
        m_handler.textInserted(e.getOffset(), e.getLength());
        //updateParser(e, e.getOffset());
    }

    public void removeUpdate(DocumentEvent e)
    {
        m_handler.textRemoved(e.getOffset(), e.getLength());
        //updateParser(e, e.getOffset()-1);
    }

    public void changedUpdate(DocumentEvent e)
    {
        //attribute change. We are not interested in these
    }

    public Segment getChars(int offset, int length)
    {
        Segment seg = new Segment();
        if(length > 0) {
            try {
                m_document.getText(offset, length, seg);
                return seg;
            }
            catch (BadLocationException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return seg;
    }

    public Segment getChars(int offset)
    {
        return getChars(offset, m_document.getLength()-offset);
    }

    public boolean atEnd(int offset)
    {
        return offset == m_document.getLength() - 1;
    }
}
