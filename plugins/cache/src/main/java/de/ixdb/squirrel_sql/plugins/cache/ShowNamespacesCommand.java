package de.ixdb.squirrel_sql.plugins.cache;

import com.intersys.cache.jbind.JBindDatabase;
import com.intersys.objects.CacheDatabase;
import com.intersys.objects.CacheQuery;
import com.intersys.objects.Database;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.*;


public class ShowNamespacesCommand
{
   private ISession _session;

   public ShowNamespacesCommand(ISession session)
   {
      _session = session;
   }

   public void execute()
   {
      try
      {
         String curServerName = getUrlBegin(_session.getSQLConnection().getConnection().getMetaData().getURL());

         Database conn = (JBindDatabase) CacheDatabase.getDatabase(_session.getSQLConnection().getConnection());
         CacheQuery qry = new CacheQuery(conn, "%Library.RoutineMgr", "NamespaceList");
         ResultSet resNamespaces = qry.execute();

         Hashtable aliasesByNamespaces = new Hashtable();
         while (resNamespaces.next())
         {
            aliasesByNamespaces.put(resNamespaces.getString(1).toUpperCase(), "");
         }
         resNamespaces.close();


         ISQLDriver cacheDriver = null;
         for (Iterator i = _session.getApplication().getDataCache().drivers(); i.hasNext();)
         {
            ISQLDriver drv = (ISQLDriver) i.next();
            if (drv.getDriverClassName().equals("com.intersys.jdbc.CacheDriver"))
            {
               cacheDriver = drv;
            }
         }

         if (null == cacheDriver)
         {
            String msg = "Could not find driver com.intersys.jdbc.CacheDriver";
            _session.showErrorMessage(msg);
            throw new IllegalStateException(msg);
         }


         for (Iterator i = _session.getApplication().getDataCache().getAliasesForDriver(cacheDriver); i.hasNext();)
         {
            ISQLAlias alias = (ISQLAlias) i.next();

            String serverName = getUrlBegin(alias.getUrl());

            if (false == curServerName.equalsIgnoreCase(serverName))
            {
               continue;
            }

            String nameSpaceOfAlias = alias.getUrl().substring(alias.getUrl().lastIndexOf('/') + 1);

            String aliasNames = (String) aliasesByNamespaces.get(nameSpaceOfAlias.toUpperCase());

            if (null != aliasNames)
            {
               if (0 == aliasNames.length())
               {
                  aliasNames += alias.getName();
               }
               else
               {
                  aliasNames += ";" + alias.getName();
               }
               aliasesByNamespaces.put(nameSpaceOfAlias, aliasNames);

            }
         }

         String[][] nameSpacesAndAliases = new String[aliasesByNamespaces.size()][2];
         int index = 0;
         for (Enumeration e = aliasesByNamespaces.keys(); e.hasMoreElements();)
         {
            nameSpacesAndAliases[index][0] = (String) e.nextElement();
            nameSpacesAndAliases[index][1] = (String) aliasesByNamespaces.get(nameSpacesAndAliases[index][0]);
            ++index;
         }

         NamespaceCtrlListener nl = new NamespaceCtrlListener()
         {
            public String nameSpaceSelected(ISession session, String nameSpace, String aliasNameTemplate)
            {
               return onNameSpaceSelected(session, nameSpace, aliasNameTemplate);
            }
         };

         Arrays.sort(nameSpacesAndAliases, new Comparator()
         {
            public int compare(Object o1, Object o2)
            {
               return ((String[]) o1)[0].compareTo(((String[]) o2)[0]);
            }
         });

         new NamespaceCtrl(_session, nameSpacesAndAliases, nl);

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getUrlBegin(String url)
   {
      //jdbc:Cache://cachensw-and:1972/SHDTest

      return url.substring(0, url.lastIndexOf('/'));

   }

   private String onNameSpaceSelected(ISession session, String nameSpace, String aliasNameTemplate)
   {

      try
      {
         ISQLAlias curAlias = session.getAlias();

         String urlPrefix = "jdbc:Cache://";

         if (false == curAlias.getUrl().startsWith(urlPrefix))
         {
            String msg = "URL of this session does not start with " + urlPrefix + ".\nCannot create alias";
            JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
            return "";
         }

         if (urlPrefix.length() >= curAlias.getUrl().lastIndexOf(':'))
         {
            String msg = "Could not find server in this session's URL " + curAlias.getUrl() + ".\nURL must match jdbc:Cache://<server>:<port>/<namespace>\n" +
               "Cannot create alias.";
            JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), msg);
            return "";
         }


         String server = curAlias.getUrl().substring(urlPrefix.length(), curAlias.getUrl().lastIndexOf(':'));

         ISQLAlias alias = session.getApplication().getDataCache().createAlias(IdentifierFactory.getInstance().createIdentifier());

         alias.setName(aliasNameTemplate.replaceAll("%server", server).replaceAll("%namespace", nameSpace));
         alias.setDriverIdentifier(curAlias.getDriverIdentifier());
         alias.setUserName(curAlias.getUserName());
         alias.setPassword(curAlias.getPassword());
         alias.setUrl(curAlias.getUrl().substring(0, curAlias.getUrl().lastIndexOf('/') + 1) + nameSpace);
         alias.setAutoLogon(curAlias.isAutoLogon());

         session.getApplication().getDataCache().addAlias(alias);

         return alias.getName();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
