package net.sourceforge.squirrel_sql.client.db;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
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
import java.sql.DatabaseMetaData;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
// TODO: move to fw
public class Utils
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Utils.class);

	private Utils()
	{
		super();
	}

	public static String getNullableDescription(int type)
	{
		switch (type)
		{
			case DatabaseMetaData.typeNoNulls :
				return s_stringMgr.getString("Utils.no");
			case DatabaseMetaData.typeNullable :
				return s_stringMgr.getString("Utils.yes");
			default :
				return s_stringMgr.getString("Utils.unknown");
		}
	}

	public static String getSearchableDescription(int type)
	{
		switch (type)
		{
			case DatabaseMetaData.typePredNone :
				return s_stringMgr.getString("Utils.no");
			case DatabaseMetaData.typePredChar :
				return s_stringMgr.getString("Utils.onlywherelike");
			case DatabaseMetaData.typePredBasic :
				return s_stringMgr.getString("Utils.notwherelike");
			case DatabaseMetaData.typeSearchable :
				return s_stringMgr.getString("Utils.yes");
			default :
				return s_stringMgr.getString("Utils.unknown", Integer.valueOf(type));
		}
	}
}
