/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * A DiffPresentation that uses components from the JMeld project to render a comparison of the content of two
 * files in a JFrame.
 */
public class JMeldDiffPresentation extends AbstractSideBySideDiffPresentation
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);

	/**
	 * @see AbstractSideBySideDiffPresentation#executeDiff(java.lang.String, java.lang.String)
	 */
	public void executeDiff(String leftFilename, String rightFilename)
	{
		new JMeldCore().executeDiff(leftFilename, rightFilename, s_stringMgr.getString("JMeldDiffPresentation.table.diff"));
	}
}
