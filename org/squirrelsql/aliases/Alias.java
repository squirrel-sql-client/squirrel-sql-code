package org.squirrelsql.aliases;


import java.util.UUID;

public class Alias implements AliasTreeNode
{
   private String id = UUID.randomUUID().toString();
   private String name;
   private String driverId;
   private String url;
   private String userName;
   private boolean connectAtStartUp;
   private boolean autoLogon;
   private boolean savePassword;
   private String password;
   private boolean _userNull;
   private boolean _userEmptyString;
   private boolean _passwordNull;
   private boolean _passwordEmptyString;


   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getDriverId()
   {
      return driverId;
   }

   public void setDriverId(String driverId)
   {
      this.driverId = driverId;
   }

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   public String getUserName()
   {
      return userName;
   }

   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   public boolean isConnectAtStartUp()
   {
      return connectAtStartUp;
   }

   public void setConnectAtStartUp(boolean connectAtStartUp)
   {
      this.connectAtStartUp = connectAtStartUp;
   }

   public boolean isAutoLogon()
   {
      return autoLogon;
   }

   public void setAutoLogon(boolean autoLogon)
   {
      this.autoLogon = autoLogon;
   }

   public boolean isSavePassword()
   {
      return savePassword;
   }

   public void setSavePassword(boolean savePassword)
   {
      this.savePassword = savePassword;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
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
}
