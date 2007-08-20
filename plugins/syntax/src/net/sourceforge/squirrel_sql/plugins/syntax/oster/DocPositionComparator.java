package net.sourceforge.squirrel_sql.plugins.syntax.oster;
/*
 * This is based on the text editor demonstration class that comes with
 * the Ostermiller Syntax Highlighter Copyright (C) 2001 Stephen Ostermiller 
 * http://ostermiller.org/contact.pl?regarding=Syntax+Highlighting

 * Modifications copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 * 
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
import java.util.Comparator;
/**
 * A wrapper for a position in a document appropriate for storing
 * in a collection.
 */
class DocPositionComparator implements Comparator<DocPosition>
{
	/**
	 * Does this Comparator equal another?
	 * Since all DocPositionComparators are the same, they
	 * are all equal.
	 *
	 * @return true for DocPositionComparators, false otherwise.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof DocPositionComparator)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Compare two DocPositions
	 *
	 * @param o1 first DocPosition
	 * @param o2 second DocPosition
	 * @return negative if first < second, 0 if equal, positive if first > second
	 */
	public int compare(DocPosition d1, DocPosition d2)
	{
	    return (d1.getPosition() - d2.getPosition());
	}
}
