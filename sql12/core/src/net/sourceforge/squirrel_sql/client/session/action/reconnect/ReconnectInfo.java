package net.sourceforge.squirrel_sql.client.session.action.reconnect;

public class ReconnectInfo
{
   private boolean _reconnectRequested;


   ////////////////////////////////////////////////////////////////////
   // These fields are null when unchanged in the reconnect dialog
   private String _user;
   private String _password;
   private String _url;
   //
   ////////////////////////////////////////////////////////////////////

   public boolean isReconnectRequested()
   {
      return _reconnectRequested;
   }

   public void setReconnectRequested(boolean reconnectRequested)
   {
      _reconnectRequested = reconnectRequested;
   }

   public String getUser()
   {
      return _user;
   }

   public String getPassword()
   {
      return _password;
   }

   public String getUrl()
   {
      return _url;
   }

   public void setUser(String user)
   {
      _user = user;
   }

   public void setPassword(String password)
   {
      _password = password;
   }

   public void setUrl(String url)
   {
      _url = url;
   }
}
