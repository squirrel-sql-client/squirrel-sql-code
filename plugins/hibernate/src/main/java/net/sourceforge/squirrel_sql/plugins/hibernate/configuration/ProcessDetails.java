package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ServerMain;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ServerMainImpl;

import java.io.File;

public class ProcessDetails
{

   private String _command;
   private int _port = 23366;
   private boolean _endProcessOnDisconnect = true;
   private HibernatePlugin _plugin;

   public ProcessDetails(HibernatePlugin plugin)
   {
      _plugin = plugin;
      initCommandDefault();
   }

   private void initCommandDefault()
   {
      String osName = System.getProperty("os.name");

      if (osName.startsWith("Mac OS"))
      {
         _command = createLinuxCommand(_plugin);
      }
      else if (osName.startsWith("Windows"))
      {
         _command = createWindowsCommand(_plugin);
      }
      else // assume Linux Unix
      {
         _command = createLinuxCommand(_plugin);
      }
   }

   private String createWindowsCommand(HibernatePlugin plugin)
   {
      String java = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"";
      String command = java + " -cp " + getPluginJarFilePath(plugin) + " " + ServerMainImpl.class.getName() + " " + ServerMain.PORT_PARAM_PREFIX + _port;

      return command;
   }


   private String createLinuxCommand(HibernatePlugin plugin)
   {
      String java = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"";
      String command = "" + java + " -cp " + getPluginJarFilePath(plugin) + " " + ServerMainImpl.class.getName() + " " + ServerMain.PORT_PARAM_PREFIX + _port;
      return command;
   }

   private String getPluginJarFilePath(HibernatePlugin plugin)
   {
      return "\"" + plugin.getPluginJarFilePath() + "\"";
   }


   public void apply(HibernateConfiguration cfg)                     
   {
      cfg.setCommand(_command);
      cfg.setEndProcessOnDisconnect(_endProcessOnDisconnect);
      cfg.setProcessPort(_port);
   }

   public void setCommand(String command)
   {
      _command = command;
   }

   public void setEndProcessOnDisconnect(boolean endProcessOnDisconnect)
   {
      _endProcessOnDisconnect = endProcessOnDisconnect;
   }

   public String getCommand()                                              
   {
      return _command;
   }

   public boolean isEndProcessOnDisconnect()
   {
      return _endProcessOnDisconnect;
   }

   public void setPort(int port)
   {
      _port = port;
   }

   public String restoreDefault()
   {
      initCommandDefault();
      return _command;
   }
}
