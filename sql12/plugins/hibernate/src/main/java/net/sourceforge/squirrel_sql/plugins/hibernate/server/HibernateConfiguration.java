package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.File;
import java.io.Serializable;

public class HibernateConfiguration implements Serializable
{
   private String _provider;
   private String _name;
   private String[] _classpathEntries = new String[0];
   private boolean _userDefinedProvider;
   private boolean _jpa;
   private String _persistenceUnitName;
   private boolean _useProcess = false;
   private String _command;
   private boolean _endProcessOnDisconnect;
   private int _processPort;

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

   public String classpathAsString()
   {
      return classPathToString(_classpathEntries);
   }

   public static String classPathToString(String[] classpathEntries)
   {
      if(0 == classpathEntries.length)
      {
         return "";
      }
      else
      {
         String ret = classpathEntries[0];

         for (String _classpathEntry : classpathEntries)
         {
            ret += File.pathSeparator + _classpathEntry;
         }

         return ret;
      }
   }

   public boolean isUseProcess()
   {
      return _useProcess;
   }

   public void setUseProcess(boolean useProcess)
   {
      _useProcess = useProcess;
   }

   public String getCommand()
   {
      return _command;
   }

   public void setCommand(String command)
   {
      _command = command;
   }

   public void setEndProcessOnDisconnect(boolean endProcessOnDisconnect)
   {
      _endProcessOnDisconnect = endProcessOnDisconnect;
   }

   public boolean isEndProcessOnDisconnect()
   {
      return _endProcessOnDisconnect;
   }

   public void setProcessPort(int processPort)
   {
      _processPort = processPort;
   }

   public int getProcessPort()
   {
      return _processPort;
   }
}
