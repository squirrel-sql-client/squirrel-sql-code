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

/**
 * This class mediates between the document and the parser by implementing the
 * DocumentListener interface
 */
public class ParserAdapter implements DocumentListener
{
    private CompletionHandler m_handler;
    private int m_lastPosition;

    public ParserAdapter(CompletionHandler handler, Document document)
    {
        m_handler = handler;
        m_lastPosition = -1;
        if(document.getLength() > 0) {
            Segment seg = new Segment();
            //seg.setPartialReturn(true); 1.4 only
            try {
                document.getText(0, document.getLength(), seg);
                m_handler.begin(seg);
            }
            catch (BadLocationException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else
            m_handler.begin(null);
    }

    public void insertUpdate(DocumentEvent e)
    {
        updateParser(e, e.getOffset());
    }

    public void removeUpdate(DocumentEvent e)
    {
        updateParser(e, e.getOffset()-1);
    }

    public void changedUpdate(DocumentEvent e)
    {
        //attribute change. We are not interested in these
    }

    protected void updateParser(DocumentEvent e, int offset)
    {
        boolean forward = offset > m_lastPosition;
        m_lastPosition = offset;

        boolean doIncrement = forward ?
              (m_handler.getIncrementType() & CompletionHandler.INCR_FWD) > 0:
              (m_handler.getIncrementType() & CompletionHandler.INCR_BACK) > 0;

        Segment seg = new Segment();
        //seg.setPartialReturn(true); 1.4 only
        Document doc = e.getDocument();
        try {
            if(doIncrement) {
                doc.getText(offset, doc.getLength()-offset, seg);
            }
            else {
                doc.getText(0, doc.getLength(), seg);
            }
        }
        catch (BadLocationException e1) {
            throw new RuntimeException(e1.getMessage());
        }
        m_handler.invalidate(seg, forward);
    }
}
