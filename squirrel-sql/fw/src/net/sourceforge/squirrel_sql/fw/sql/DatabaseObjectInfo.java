package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
public class DatabaseObjectInfo implements IDatabaseObjectInfo
{
	/** Catalog name. Can be <CODE>null</CODE> */
	private final String _catalog;

	/** Schema name. Can be <CODE>null</CODE> */
	private final String _schema;

	/** Simple object name. */
	private final String _simpleName;

	/** Fully qualified name for this object. */
	private final String _qualifiedName;

	/** Object type. @see IDatabaseObjectType.*/
	private int _dboType = IDatabaseObjectTypes.GENERIC_LEAF;

	public DatabaseObjectInfo(String catalog, String schema, String simpleName,
								int dboType, SQLConnection conn)
	{
		super();
//		if (simpleName == null)
//		{
//			throw new IllegalArgumentException("Null simpleName passed");
//		}
		if (conn == null)
		{
			throw new IllegalArgumentException("Null SQLConnection passed");
		}

		_catalog = catalog;
		_schema = schema;
		_simpleName = simpleName;
		_qualifiedName = generateQualifiedName(conn);
		_dboType = dboType;
	}

	public String toString()
	{
		return getSimpleName();
	}

	public String getCatalogName()
	{
		return _catalog;
	}

	public String getSchemaName()
	{
		return _schema;
	}

	public String getSimpleName()
	{
		return _simpleName;
	}

	public String getQualifiedName()
	{
		return _qualifiedName;
	}

	public int getDatabaseObjectType()
	{
		return _dboType;
	}

	protected String generateQualifiedName(SQLConnection conn)
	{
		String catSep = null;
		String identifierQuoteString = null;
		boolean supportsSchemasInDataManipulation = false;
		boolean supportsCatalogsInDataManipulation = false;
		try
		{
			supportsSchemasInDataManipulation =
				conn.supportsSchemasInDataManipulation();
		}
		catch (BaseSQLException ex)
		{
		}
		try
		{
			supportsCatalogsInDataManipulation =
				conn.supportsCatalogsInDataManipulation();
		}
		catch (BaseSQLException ex)
		{
		}
		try
		{
			if (supportsCatalogsInDataManipulation)
			{
				catSep = conn.getCatalogSeparator();
			}
		}
		catch (BaseSQLException ex)
		{
		}
		try
		{
			identifierQuoteString = conn.getIdentifierQuoteString();
			if (identifierQuoteString != null
				&& identifierQuoteString.equals(" "))
			{
				identifierQuoteString = null;
			}
		}
		catch (BaseSQLException ex)
		{
		}

		StringBuffer buf = new StringBuffer();
		if (catSep != null
			&& catSep.length() > 0
			&& _catalog != null
			&& _catalog.length() > 0)
		{
			if (identifierQuoteString != null)
			{
				buf.append(identifierQuoteString);
			}
			buf.append(_catalog);
			if (identifierQuoteString != null)
			{
				buf.append(identifierQuoteString);
			}
			buf.append(catSep);
		}

		if (supportsSchemasInDataManipulation && _schema != null
			&& _schema.length() > 0)
		{
			if (identifierQuoteString != null)
			{
				buf.append(identifierQuoteString);
			}
			buf.append(_schema);
			if (identifierQuoteString != null)
			{
				buf.append(identifierQuoteString);
			}
			buf.append(".");
		}

		if (identifierQuoteString != null)
		{
			buf.append(identifierQuoteString);
		}
		buf.append(_simpleName);
		if (identifierQuoteString != null)
		{
			buf.append(identifierQuoteString);
		}
		return buf.toString();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof DatabaseObjectInfo)
		{
			DatabaseObjectInfo info = (DatabaseObjectInfo) obj;
			if ((info._catalog == null && _catalog == null)
				|| ((info._catalog != null && _catalog != null)
					&& info._catalog.equals(_catalog)))
			{
				if ((info._qualifiedName == null && _qualifiedName == null)
					|| ((info._qualifiedName != null && _qualifiedName != null)
						&& info._qualifiedName.equals(_qualifiedName)))
				{
					if ((info._schema == null && _schema == null)
						|| ((info._schema != null && _schema != null)
							&& info._schema.equals(_schema)))
					{
						return (
							(info._simpleName == null && _simpleName == null)
								|| ((info._simpleName != null
									&& _simpleName != null)
									&& info._simpleName.equals(_simpleName)));
					}

				}
			}
		}
		return false;
	}

	public int compareTo(Object o)
	{
		DatabaseObjectInfo other = (DatabaseObjectInfo) o;
		return _qualifiedName.compareTo(other._qualifiedName);
	}
}
