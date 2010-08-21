package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;
/*
 * Copyright (C) 2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * View of a <tt>BestRowIdentifier</tt> object to be displayed.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BestRowIdentifierAdapter
{
	/** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BestRowIdentifierAdapter.class);

	/**
	 * Property names for this bean.
	 */
	public interface IPropertyNames
	{
		String COLUMN_NAME = "columnName";
		String PRECISION = "precision";
		String PSEUDO = "pseudoColumn";
		String SCALE = "scale";
		String SCOPE = "scope";
		String SQL_DATA_TYPE = "sqlDataType";
		String TYPE_NAME = "typeName";
	}

	private final BestRowIdentifier _viewObj;

	public BestRowIdentifierAdapter(BestRowIdentifier obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("BestRowIdentifier == null");
		}
		_viewObj = obj;
	}

	public String getColumnName()
	{
		return _viewObj.getColumnName();
	}

	public short getSQLDataType()
	{
		return _viewObj.getSQLDataType();
	}

	public String getTypeName()
	{
		return _viewObj.getTypeName();
	}

	public int getPrecision()
	{
		return _viewObj.getPrecision();
	}

	public short getScale()
	{
		return _viewObj.getScale();
	}

	public String getScope()
	{
		final int scope = _viewObj.getScope();
		switch (scope)
		{
			case DatabaseMetaData.bestRowTemporary:
				return s_stringMgr.getString("BestRowIdentifierAdapter.temporary");
			case DatabaseMetaData.bestRowTransaction:
				return s_stringMgr.getString("BestRowIdentifierAdapter.transaction");
			case DatabaseMetaData.bestRowSession:
				return s_stringMgr.getString("BestRowIdentifierAdapter.session");
			default:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknown");
		}
	}

	public String getPseudoColumn()
	{
		final short value = _viewObj.getPseudoColumn();
		switch (value)
		{
			case DatabaseMetaData.bestRowPseudo:
				return s_stringMgr.getString("BestRowIdentifierAdapter.pseudo");
			case DatabaseMetaData.bestRowNotPseudo:
				return s_stringMgr.getString("BestRowIdentifierAdapter.notPseudo");
			case DatabaseMetaData.bestRowUnknown:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknownPseudo");
			default:
				return s_stringMgr.getString("BestRowIdentifierAdapter.unknown");
		}
	}

}
