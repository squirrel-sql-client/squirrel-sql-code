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
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;

public class MockSQLAlias implements ISQLAliasExt {

    public void assignFrom(ISQLAlias rhs) throws ValidationException {
        // TODO Auto-generated method stub

    }

    public int compareTo(Object rhs) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setName(String name) throws ValidationException {
        // TODO Auto-generated method stub

    }

    public IIdentifier getDriverIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDriverIdentifier(IIdentifier data)
            throws ValidationException {
        // TODO Auto-generated method stub

    }

    public String getUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setUrl(String url) throws ValidationException {
        // TODO Auto-generated method stub

    }

    public String getUserName() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setUserName(String userName) throws ValidationException {
        // TODO Auto-generated method stub

    }

    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPassword(String password) throws ValidationException {
        // TODO Auto-generated method stub

    }

    public boolean isAutoLogon() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setAutoLogon(boolean value) {
        // TODO Auto-generated method stub

    }

    public boolean isConnectAtStartup() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setConnectAtStartup(boolean value) {
        // TODO Auto-generated method stub

    }

    public boolean getUseDriverProperties() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setUseDriverProperties(boolean value) {
        // TODO Auto-generated method stub

    }

    public SQLDriverPropertyCollection getDriverProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDriverProperties(SQLDriverPropertyCollection value) {
        // TODO Auto-generated method stub

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub

    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub

    }

    public IIdentifier getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

   public SQLDriverPropertyCollection getDriverPropertiesClone() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SQLAliasSchemaProperties getSchemaProperties()  {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void setSchemaProperties(SQLAliasSchemaProperties schemaProperties)  {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
