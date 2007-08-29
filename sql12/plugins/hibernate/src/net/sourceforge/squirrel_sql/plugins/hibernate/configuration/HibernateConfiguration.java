package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

public class HibernateConfiguration
{
   private String _provider;
   private String _name;
   private String[] _classpathEntries = new String[0];
   private boolean _userDefinedProvider;
   private boolean _jpa;
   private String _persistenceUnitName;

   public String getProvider()
   {
      return _provider;
   }

   public void setProvider(String provider)
   {
      this._provider = provider;
   }

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      this._name = name;
   }

   public String[] getClassPathEntries()
   {
      return _classpathEntries;
   }

   public void setClassPathEntries(String[] classPathEntries)
   {
      _classpathEntries = classPathEntries;
   }


   public String toString()
   {
      return _name;
   }

   public void setUserDefinedProvider(boolean userDefinedProvider)
   {
      _userDefinedProvider = userDefinedProvider;
   }


   public boolean isUserDefinedProvider()
   {
      return _userDefinedProvider;
   }

   public boolean isJPA()
   {
      return _jpa;
   }

   public void setJPA(boolean b)
   {
      _jpa = b;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      _persistenceUnitName = persistenceUnitName;
   }

   public String getPersistenceUnitName()
   {
      return _persistenceUnitName;
   }
}
