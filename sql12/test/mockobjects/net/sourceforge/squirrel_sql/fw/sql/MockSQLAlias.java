/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;

public class MockSQLAlias implements ISQLAliasExt
{

	public void assignFrom(ISQLAlias rhs) throws ValidationException
	{

	}

	public int compareTo(Object rhs)
	{

		return 0;
	}

	public String getName()
	{

		return null;
	}

	public void setName(String name) throws ValidationException
	{

	}

	public IIdentifier getDriverIdentifier()
	{

		return null;
	}

	public void setDriverIdentifier(IIdentifier data) throws ValidationException
	{

	}

	public String getUrl()
	{

		return null;
	}

	public void setUrl(String url) throws ValidationException
	{

	}

	public String getUserName()
	{

		return null;
	}

	public void setUserName(String userName) throws ValidationException
	{

	}

	public String getPassword()
	{

		return null;
	}

	public void setPassword(String password) throws ValidationException
	{

	}

	public boolean isAutoLogon()
	{

		return false;
	}

	public void setAutoLogon(boolean value)
	{

	}

	public boolean isConnectAtStartup()
	{

		return false;
	}

	public void setConnectAtStartup(boolean value)
	{

	}

	public boolean getUseDriverProperties()
	{

		return false;
	}

	public void setUseDriverProperties(boolean value)
	{

	}

	public SQLDriverPropertyCollection getDriverProperties()
	{

		return null;
	}

	public void setDriverProperties(SQLDriverPropertyCollection value)
	{

	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{

	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{

	}

	public IIdentifier getIdentifier()
	{

		return null;
	}

	public boolean isValid()
	{

		return false;
	}

	public SQLDriverPropertyCollection getDriverPropertiesClone()
	{
		return null;
	}

	public SQLAliasSchemaProperties getSchemaProperties()
	{
		return null;
	}

	public void setSchemaProperties(SQLAliasSchemaProperties schemaProperties)
	{

	}

	@Override
	public SQLAliasColorProperties getColorProperties()
	{
		return new SQLAliasColorProperties();
	}

	@Override
	public void setColorProperties(SQLAliasColorProperties colorProperties)
	{
		// TODO Auto-generated method stub
		
	}
}
