package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class HibernateConfiguration implements Serializable
{
   private String _name;
   private String _persistenceUnitName;
   private boolean _useProcess = false;
   private String _command;
   private boolean _endProcessOnDisconnect;
   private int _processPort;
   private ClassPathItem[] _classPathItems;


   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      this._name = name;
   }

   public String toString()
   {
      return _name;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      _persistenceUnitName = persistenceUnitName;
   }

   public String getPersistenceUnitName()
   {
      return _persistenceUnitName;
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

   public void setClassPathItems(ClassPathItem[] classPathItems)
   {
      _classPathItems = classPathItems;
   }

   public ClassPathItem[] getClassPathItems()
   {
      return _classPathItems;
   }

}
