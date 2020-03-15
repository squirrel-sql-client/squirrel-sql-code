package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANT_Y; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * This represents a Database alias which is a description of the means
 * required to connect to a JDBC complient database.<P>
 * This class is a <CODE>JavaBean</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
public class SQLAlias implements Cloneable, Serializable, ISQLAliasExt, Comparable<Object>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLAlias.class);

   private interface IStrings
   {
      String ERR_BLANK_NAME = s_stringMgr.getString("SQLAlias.error.blankname");
      String ERR_BLANK_DRIVER = s_stringMgr.getString("SQLAlias.error.blankdriver");
      String ERR_BLANK_URL = s_stringMgr.getString("SQLAlias.error.blankurl");
   }

   /** The <CODE>IIdentifier</CODE> that uniquely identifies this object. */
   private IIdentifier _id;

   /** The name of this alias. */
   private String _name;

   /**
    * The <CODE>IIdentifier</CODE> that identifies the <CODE>ISQLDriver</CODE>
    * that this <CODE>ISQLAlias</CODE> uses.
    */
   private IIdentifier _driverId;

   /** The URL required to access the database. */
   private String _url;

   /** Name of user for connection. */
   private String _userName;

   /** Password of user for connection. */
   private String _password;

   private boolean _encryptPassword; // Renamed from PasswordEncrypted because of bug #1409

   /** <TT>true</TT> if this alias should be logged on automatically. */
   private boolean _autoLogon;

   /** Should this alias be connected when the application is started up. */
   private boolean _connectAtStartup;

   /** If <TT>true</TT> then use drver properties. */
   private boolean _useDriverProperties = false;

   /** Collection of <TT>SQLDriverProperty</TT> objects for this alias. */
   private SQLDriverPropertyCollection _driverProps = new SQLDriverPropertyCollection();

   private SQLAliasSchemaProperties _schemaProperties = new SQLAliasSchemaProperties();

   private SQLAliasColorProperties _colorProperties = new SQLAliasColorProperties();
   
   private SQLAliasConnectionProperties _connectionProperties = new SQLAliasConnectionProperties();

   private SQLAliasVersioner _versioner = new SQLAliasVersioner(this);

   private long _aliasVersionTimeMills = 0;

   public SQLAlias()
   {
      distributeVersioner();
   }

   /**
    * Ctor specifying the identifier.
    *
    * @param	id	Uniquely identifies this object.
    */
   public SQLAlias(IIdentifier id)
   {
      this();
      _id = id;
      _name = "";
      _driverId = null;
      _url = "";
      _userName = "";
      _password = "";
   }

   private void distributeVersioner()
   {
      // Left out for now. When needed should probably best be triggered by DriverPropertiesController
      //_driverProps.acceptAliasVersioner(_versioner);
      _schemaProperties.acceptAliasVersioner(_versioner);
      _colorProperties.acceptAliasVersioner(_versioner);
      _connectionProperties.acceptAliasVersioner(_versioner);
   }

   /**
    * Assign data from the passed <CODE>ISQLAlias</CODE> to this one.
    *
    * This Alias becomes a clone of sqlAlias.
    *
    * @param	sqlAlias	 <CODE>ISQLAlias</CODE> to copy data from.
    *
    * @exception	ValidationException
    *				Thrown if an error occurs assigning data from
    *				<CODE>sqlAlias</CODE>.
    */
   public synchronized void assignFrom(SQLAlias sqlAlias, boolean withIdentifier)
   {
      try(Java8CloseableFix java8Dum = _versioner.switchOff())
      {
         if(withIdentifier)
         {
            setIdentifier(sqlAlias.getIdentifier());
         }

         setName(sqlAlias.getName());
         setDriverIdentifier(sqlAlias.getDriverIdentifier());
         setUrl(sqlAlias.getUrl());
         setUserName(sqlAlias.getUserName());
         setEncryptPassword(sqlAlias.isEncryptPassword());
         setPassword(sqlAlias.getPassword()); // Copying of SQL Alias, no need for AliasPasswordHandler.setPassword(...) here.
         setAutoLogon(sqlAlias.isAutoLogon());
         setUseDriverProperties(sqlAlias.getUseDriverProperties());
         setDriverProperties(sqlAlias.getDriverPropertiesClone());
         setAliasVersionTimeMills(sqlAlias.getAliasVersionTimeMills());
         _schemaProperties = Utilities.cloneObject(sqlAlias._schemaProperties, getClass().getClassLoader());
         _schemaProperties.acceptAliasVersioner(_versioner);
         _colorProperties = Utilities.cloneObject(sqlAlias._colorProperties, getClass().getClassLoader());
         _colorProperties.acceptAliasVersioner(_versioner);

         _connectionProperties = new SQLAliasConnectionProperties();
         _colorProperties.acceptAliasVersioner(_versioner);
         SQLAliasConnectionProperties rhsConnProps = sqlAlias.getConnectionProperties();
         _connectionProperties.setEnableConnectionKeepAlive(rhsConnProps.isEnableConnectionKeepAlive());
         _connectionProperties.setKeepAliveSleepTimeSeconds(rhsConnProps.getKeepAliveSleepTimeSeconds());
         _connectionProperties.setKeepAliveSqlStatement(rhsConnProps.getKeepAliveSqlStatement());
      }
      catch (ValidationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   /**
    * Returns <TT>true</TT> if this objects is equal to the passed one. Two
    * <TT>ISQLAlias</TT> objects are considered equal if they have the same
    * identifier.
    */
   public boolean equals(Object rhs)
   {
      boolean rc = false;
      if (rhs != null && rhs.getClass().equals(getClass()))
      {
         rc = ((ISQLAlias)rhs).getIdentifier().equals(getIdentifier());
      }
      return rc;
   }

   /**
    * Returns a hash code value for this object.
    */
   public synchronized int hashCode()
   {
      return getIdentifier().hashCode();
   }

   /**
    * Returns the name of this <TT>ISQLAlias</TT>.
    */
   public String toString()
   {
      return getName();
   }

   /**
    * Compare this <TT>SQLAlias</TT> to another object. If the passed object
    * is a <TT>SQLAlias</TT>, then the <TT>getName()</TT> functions of the two
    * <TT>SQLAlias</TT> objects are used to compare them. Otherwise, it throws a
    * ClassCastException (as <TT>SQLAlias</TT> objects are comparable only to
    * other <TT>SQLAlias</TT> objects).
    */
   public int compareTo(Object rhs)
   {
      return _name.compareTo(((ISQLAlias)rhs).getName());
   }

   /**
    * Returns <CODE>true</CODE> if this object is valid.<P>
    * Implementation for <CODE>IPersistable</CODE>.
    */
   public synchronized boolean isValid()
   {
      return _name != null
                 && _name.length() > 0 
                 && _driverId != null
                 && _url != null
                 && _url.length() > 0;
   }

   public IIdentifier getIdentifier()
   {
      return _id;
   }

   public String getName()
   {
      return _name;
   }

   public IIdentifier getDriverIdentifier()
   {
      return _driverId;
   }

   public String getUrl()
   {
      return _url;
   }

   public String getUserName()
   {
      return _userName;
   }

   public String getPassword()
   {
      return _password;
   }

   public void setPassword(String password)
   {
      String data = getString(password);
      _versioner.trigger(_password, data);
      _password = data;
   }

   @Override
   public boolean isEncryptPassword()
   {
      return _encryptPassword;
   }

   @Override
   public void setEncryptPassword(boolean b)
   {
      _versioner.trigger(_encryptPassword, b);
      _encryptPassword = b;
   }


   public long getAliasVersionTimeMills()
   {
      return _aliasVersionTimeMills;
   }

   /**
    * To be used by xml serialization and {@link SQLAliasVersioner} only.
    */
   public void setAliasVersionTimeMills(long aliasVersionTimeMills)
   {
      _aliasVersionTimeMills = aliasVersionTimeMills;
   }


   /**
    * Should this alias be logged on automatically.
    *
    * @return	<TT>true</TT> is this alias should be logged on automatically
    * 			else <TT>false</TT>.
    */
   public boolean isAutoLogon()
   {
      return _autoLogon;
   }

   /**
    * Set whether this alias should be logged on automatically.
    *
    * @param	value	<TT>true</TT> if alias should be autologged on
    * 					else <TT>false</TT.
    */
   public void setAutoLogon(boolean value)
   {
      _versioner.trigger(_autoLogon, value);
      _autoLogon = value;
   }

   /**
    * Should this alias be connected when the application is started up.
    *
    * @return	<TT>true</TT> if this alias should be connected when the
    *			application is started up.
    */
   public boolean isConnectAtStartup()
   {
      return _connectAtStartup;
   }

   /**
    * Set whether alias should be connected when the application is started up.
    *
    * @param	value	<TT>true</TT> if alias should be connected when the
    *					application is started up.
    */
   public void setConnectAtStartup(boolean value)
   {
      _versioner.trigger(_connectAtStartup, value);
      _connectAtStartup = value;
   }

   /**
    * Returns whether this alias uses driver properties.
    */
   public boolean getUseDriverProperties()
   {
      return _useDriverProperties;
   }

   public void setIdentifier(IIdentifier id)
   {
      _id = id;
   }

   public void setName(String name) throws ValidationException
   {
      String data = getString(name);
      if (data.length() == 0)
      {
         throw new ValidationException(IStrings.ERR_BLANK_NAME);
      }
      _versioner.trigger(_name, data);
      _name = data;
   }

   public void setDriverIdentifier(IIdentifier data)
      throws ValidationException
   {
      if (data == null)
      {
         throw new ValidationException(IStrings.ERR_BLANK_DRIVER);
      }
      _versioner.trigger(_driverId, data);
      _driverId = data;
   }

   public void setUrl(String url) throws ValidationException
   {
      String data = getString(url);
      if (data.length() == 0)
      {
         throw new ValidationException(IStrings.ERR_BLANK_URL);
      }
      _versioner.trigger(_url, data);
      _url = data;
   }

   public void setUserName(String userName)
   {
      String data = getString(userName);
      _versioner.trigger(_userName, data);
      _userName = data;
   }

   public void setUseDriverProperties(boolean value)
   {
      _versioner.trigger(_useDriverProperties, value);
      _useDriverProperties = value;
   }

   /**
    * Retrieve a copy of the SQL driver properties.
    *
    * @return	the SQL driver properties.
    */
   public synchronized SQLDriverPropertyCollection getDriverPropertiesClone()
   {
      return getDriverPropertiesClone(false);
   }

   public synchronized SQLDriverPropertyCollection getDriverPropertiesClone(boolean includeVersioner)
   {
      final int count = _driverProps.size();
      SQLDriverProperty[] newar = new SQLDriverProperty[count];
      for (int i = 0; i < count; ++i)
      {
         newar[i] = (SQLDriverProperty)_driverProps.getDriverProperty(i).clone();
      }
      SQLDriverPropertyCollection coll = new SQLDriverPropertyCollection();
      coll.setDriverProperties(newar);

      if(includeVersioner)
      {
         coll.acceptAliasVersioner(_versioner);
      }
      return coll;
   }

   public synchronized void setDriverProperties(SQLDriverPropertyCollection value)
   {
      _driverProps.clear();
      if (value != null)
      {
         synchronized (value)
         {
            final int count = value.size();
            SQLDriverProperty[] newar = new SQLDriverProperty[count];
            for (int i = 0; i < count; ++i)
            {
               newar[i] = (SQLDriverProperty)value.getDriverProperty(i).clone();

            }
            _driverProps.setDriverProperties(newar);
         }
      }
   }


   private String getString(String data)
   {
      return data != null ? data.trim() : "";
   }


   public SQLAliasSchemaProperties getSchemaProperties()
   {
      return _schemaProperties;      
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt#setSchemaProperties(net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties)
    */
   public void setSchemaProperties(SQLAliasSchemaProperties schemaProperties)
   {
      _schemaProperties = schemaProperties;
      _schemaProperties.acceptAliasVersioner(_versioner);
   }

	@Override
	public SQLAliasColorProperties getColorProperties()
	{
		return _colorProperties;
	}

	@Override
	public void setColorProperties(SQLAliasColorProperties colorProperties)
	{
		_colorProperties = colorProperties;
      _colorProperties.acceptAliasVersioner(_versioner);
	}

	@Override
	public SQLAliasConnectionProperties getConnectionProperties()
	{
		return _connectionProperties;
	}

	@Override
	public void setConnectionProperties(SQLAliasConnectionProperties connectionProperties)
	{
		_connectionProperties = connectionProperties;
      _connectionProperties.acceptAliasVersioner(_versioner);
	}
}
