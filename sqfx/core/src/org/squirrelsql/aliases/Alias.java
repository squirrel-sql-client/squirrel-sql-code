package org.squirrelsql.aliases;


import java.io.Serializable;
import java.util.UUID;

public class Alias implements AliasTreeNode, Serializable
{
   private String _id = UUID.randomUUID().toString();
   private String _name;
   private String _driverId;
   private String _url;
   private String _userName;
   private boolean _connectAtStartUp;
   private boolean _autoLogon;
   private boolean _savePassword;
   private String _password;
   private boolean _userNull;
   private boolean _userEmptyString;
   private boolean _passwordNull;
   private boolean _passwordEmptyString;


   public String getId()
   {
      return _id;
   }

   public void setId(String id)
   {
      this._id = id;
   }

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      this._name = name;
   }

   public String getDriverId()
   {
      return _driverId;
   }

   public void setDriverId(String driverId)
   {
      this._driverId = driverId;
   }

   public String getUrl()
   {
      return _url;
   }

   public void setUrl(String url)
   {
      this._url = url;
   }

   public String getUserName()
   {
      return _userName;
   }

   public void setUserName(String userName)
   {
      this._userName = userName;
   }

   public boolean isConnectAtStartUp()
   {
      return _connectAtStartUp;
   }

   public void setConnectAtStartUp(boolean connectAtStartUp)
   {
      this._connectAtStartUp = connectAtStartUp;
   }

   public boolean isAutoLogon()
   {
      return _autoLogon;
   }

   public void setAutoLogon(boolean autoLogon)
   {
      this._autoLogon = autoLogon;
   }

   public boolean isSavePassword()
   {
      return _savePassword;
   }

   public void setSavePassword(boolean savePassword)
   {
      this._savePassword = savePassword;
   }

   public String getPassword()
   {
      return _password;
   }

   public void setPassword(String password)
   {
      this._password = password;
   }

   public void setUserNull(boolean userNull)
   {
      _userNull = userNull;
   }

   public boolean isUserNull()
   {
      return _userNull;
   }

   public void setUserEmptyString(boolean userEmptyString)
   {
      _userEmptyString = userEmptyString;
   }

   public boolean isUserEmptyString()
   {
      return _userEmptyString;
   }

   public void setPasswordNull(boolean passwordNull)
   {
      _passwordNull = passwordNull;
   }

   public boolean isPasswordNull()
   {
      return _passwordNull;
   }

   public void setPasswordEmptyString(boolean passwordEmptyString)
   {
      _passwordEmptyString = passwordEmptyString;
   }

   public boolean isPasswordEmptyString()
   {
      return _passwordEmptyString;
   }

   @Override
   public String toString()
   {
      return _name;
   }

   public void initAfterClone()
   {
      _id = UUID.randomUUID().toString();
   }
}
