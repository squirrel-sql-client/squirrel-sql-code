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
 * created by cse, 02.10.2002 12:57:34
 */
package net.sourceforge.jcomplete.ui;

import java.util.Iterator;

import net.sourceforge.jcomplete.Completion;

/**
 * interface for communication between different completion UIs (dialog, popup list)
 * and the completion adaptor
 */
public interface CompletionListener
{
    public abstract class Event
    {
        public Completion completion;
        public Event(Completion completion)
        {
            this.completion = completion;
        }
        public abstract boolean hasNext();
        public abstract Completion next();
        public abstract boolean needsSeparator();
    }
    void completionAborted();
    Completion completionRequested(Event event);
    Completion completionRequested(Completion completion, Object[] selectedOptions);
}

