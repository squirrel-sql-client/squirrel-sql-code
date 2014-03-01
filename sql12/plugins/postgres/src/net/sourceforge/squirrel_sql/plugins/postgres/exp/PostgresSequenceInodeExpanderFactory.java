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
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SequenceParentExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

public class PostgresSequenceInodeExpanderFactory implements INodeExpanderFactory 
{

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory#createExpander(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
	 */
	public INodeExpander createExpander(DatabaseObjectType type)
	{
		SequenceParentExpander result = new SequenceParentExpander();
		result.setExtractor(new PostgresSequenceParentExtractorImpl());
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory#getParentLabelForType(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
	 */
	public String getParentLabelForType(DatabaseObjectType type)
	{
		return "SEQUENCE";
	}

}
