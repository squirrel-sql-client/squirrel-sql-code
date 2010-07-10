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
 * created by cse, 13.09.2002 22:39:46
 */
package net.sourceforge.jcomplete;

import java.text.CharacterIterator;

import net.sourceforge.jcomplete.Completion;

/**
 * the completion handler maintains the infrastructure to supply completion
 * suggestions upon request from the UI
 */
public interface CompletionHandler
{
    public interface ErrorListener
    {
        void errorDetected(String message, int line, int column);
    }

    /**
     * return a completion object which wraps available completion options
     * @param textOffset the offset into the parsed text at which completion is requested
     * @return the completion available at the given text position
     */
    Completion getCompletion(int textOffset);

    void begin(TextProvider provider, int length);
    void textInserted(int offset, int length);
    void textRemoved(int offset, int length);
}