package org.squirrelsql.aliases;


public class Alias implements AliasTreeNode
{
   private String name;
   private String driverId;
   private String url;
   private String userName;
   private boolean connectAtStartUp;
   private boolean autoLogon;
   private boolean savePassword;
   private String password;


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
}
