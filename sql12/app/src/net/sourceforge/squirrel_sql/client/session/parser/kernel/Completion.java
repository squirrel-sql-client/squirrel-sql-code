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
 * created by cse, 24.09.2002 11:20:31
 *
 * @version $Id: Completion.java,v 1.1 2004-04-04 10:36:31 colbell Exp $
 */
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

/**
 * requirements of a completion item
 */
public interface Completion 
{
    /**
     * Find a completion item for the given text position. This method can be overridden by
     * subclasses to implement the composite pattern. If the object is not a composite, it
     * should return itself. Otherwise, it can delegate the lookup to its children.
     * @param position the caret position at which the completion is requested
     * @return an appropriate completion object, or <em>null</em> of none available
     */
    Completion getCompletion(int position);

    /**
     * return completion text if the completion is fully defined
     * @param position the caret position at which the text should be inserted
     * @return return the completion text to be inserted into the underlying document
     */
    String getText(int position);

    /**
     * return completion text which is defined from this object and the derived option
     * @param position the caret position at which the text should be inserted
     * @param option an option string, which was earlier derived from this object
     * @return return the completion text to be inserted into the underlying document
     */
    String getText(int position, String option);

    /**
     * @return whether this completion is assigned to a specific position within the
     * underlying document
     */
    boolean hasTextPosition();

    /**
     * @return whether this completion can be used to generate lists of items, e.g. columns
     * in a SQL select clause
     */
    boolean isRepeatable();

    /**
     * @return the length of the text currently occupied by this completion
     */
    int getLength();

    /**
     * @return the starting text position
     */
    int getStart();

    /**
     * @param position the position at which the status should be determined
     * @return whether the text between the start position and <em>position</em> must be replaced
     */
    boolean mustReplace(int position);
}
