package net.sourceforge.squirrel_sql.client.session;

public class JdbcConnectionData
{
   private String _driverClassName;
   private String _url;
   private String _user;
   private String _password;

   public JdbcConnectionData(String driverClassName, String url, String user, String password)
   {
      _driverClassName = driverClassName;
      _url = url;
      _user = user;
      _password = password;
   }

   public String getDriverClassName()
   {
      return _driverClassName;
   }

   public String getUrl()
   {
      return _url;
   }

   public String getUser()
   {
      return _user;
   }

   public String getPassword()
   {
      return _password;
   }
}
