package net.sourceforge.squirrel_sql.plugins.mysql.util;
/*
 * Copyright (C) 2003 Arun Kapilan.P
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

/*
 * FieldDetails.java
 *
 * Created on June 16, 2003, 11:29 AM
 *
 * @author Arun Kapilan.P
 */
public class FieldDetails
{
	private String fieldName = null;
	private String fieldType = null;
	private String fieldLength = null;
	private String fieldDefault = null;
	private boolean isPrimary = false;
	private boolean isIndex = false;
	private boolean isUnique = false;
	private boolean isBinary = false;
	private boolean isNotNull = false;
	private boolean isUnsigned = false;
	private boolean isAutoIncrement = false;
	private boolean isZeroFill = false;

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public void setFieldType(String fieldType)
	{
		this.fieldType = fieldType;
	}

	public void setFieldLength(String fieldLength)
	{
		this.fieldLength = fieldLength;
	}

	public void setDefault(String fieldDefault)
	{
		this.fieldDefault = fieldDefault;
	}
	public void setPrimary(boolean isPrimary)
	{
		this.isPrimary = isPrimary;
	}

	public void setUnique(boolean isUnique)
	{
		this.isUnique = isUnique;
	}

	public void setIndex(boolean isIndex)
	{
		this.isIndex = isIndex;
	}

	public void setBinary(boolean isBinary)
	{
		this.isBinary = isBinary;
	}

	public void setNotNull(boolean isNotNull)
	{
		this.isNotNull = isNotNull;
	}

	public void setUnisigned(boolean isUnsigned)
	{
		this.isUnsigned = isUnsigned;
	}

	public void setAutoIncrement(boolean isAutoIncrement)
	{
		this.isAutoIncrement = isAutoIncrement;
	}

	public void setZeroFill(boolean isZeroFill)
	{
		this.isZeroFill = isZeroFill;
	}

	public String getFieldName()
	{
		return this.fieldName;
	}

	public String getFieldType()
	{
		return this.fieldType;
	}

	public String getFieldLength()
	{
		return this.fieldLength;
	}

	public String getDefault()
	{
		return this.fieldDefault;
	}

	public boolean IsUnique()
	{
		return this.isUnique;
	}

	public boolean IsIndex()
	{
		return this.isIndex;
	}

	public boolean IsPrimary()
	{
		return this.isPrimary;
	}

	public boolean IsBinary()
	{
		return this.isBinary;
	}

	public boolean IsNotNull()
	{
		return this.isNotNull;
	}

	public boolean IsUnisigned()
	{
		return this.isUnsigned;
	}

	public boolean IsAutoIncrement()
	{
		return this.isAutoIncrement;
	}

	public boolean IsZeroFill()
	{
		return this.isZeroFill;
	}

	public String toString()
	{
		return getFieldName();
	}
}
