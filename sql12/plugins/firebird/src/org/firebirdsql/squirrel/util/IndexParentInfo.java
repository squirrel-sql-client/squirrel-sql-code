package org.firebirdsql.squirrel.util;
/*
 * Copyright (C) 2004 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.firebirdsql.squirrel.IObjectTypes;
/**
 * This class stores information about a Index parent. This just
 * stores info about the object that the index relates to.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IndexParentInfo extends RelatedObjectInfo
{
   private static final long serialVersionUID = 1L;

	public IndexParentInfo(IDatabaseObjectInfo relatedObjInfo,
                                SQLDatabaseMetaData md)
    {
        super(relatedObjInfo, "INDEX", IObjectTypes.INDEX_PARENT, md);
    }
}
