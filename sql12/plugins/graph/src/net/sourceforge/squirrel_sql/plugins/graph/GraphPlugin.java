package net.sourceforge.squirrel_sql.plugins.graph;

/*
 * Copyright (C) 2004 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;

/**
 * The SQL Script plugin class.
 */
public class GraphPlugin extends DefaultSessionPlugin
{

   private Hashtable<IIdentifier, GraphController[]> _grapControllersBySessionID = 
       new Hashtable<IIdentifier, GraphController[]>();

   /**
    * Logger for this class.
    */
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(GraphPlugin.class);

   private PluginResources _resources;

   /**
    * Return the internal name of this plugin.
    *
    * @return the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "graph";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "Graph";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return the current version of this plugin.
    */
   public String getVersion()
   {
      return "1.0";
   }

   /**
    * Returns the authors name.
    *
    * @return the authors name.
    */
   public String getAuthor()
   {
      return "Gerd Wagner";
   }

   /**
    * Returns the name of the change log for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
    * a change log.
    */
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   /**
    * Returns the name of the Help file for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return	the Help file name or <TT>null</TT> if plugin doesn't have
    * a help file.
    */
   public String getHelpFileName()
   {
      return "readme.html";
   }

   /**
    * Returns the name of the Licence file for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return	the Licence file name or <TT>null</TT> if plugin doesn't have
    * a licence file.
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }


   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      _resources =
         new PluginResources(
            "net.sourceforge.squirrel_sql.plugins.graph.graph",
            this);



      ActionCollection coll = app.getActionCollection();
      coll.add(new AddToGraphAction(app, _resources, this));
   }

   /**
    * Application is shutting down so save data.
    */
   public void unload()
   {
      super.unload();
   }

   /**
    * Called when a session started. Add commands to popup menu
    * in object tree.
    *
    * @param session The session that is starting.
    * @return <TT>true</TT> to indicate that this plugin is
    *         applicable to passed session.
    */
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      GraphXmlSerializer[] serializers  = GraphXmlSerializer.getGraphXmSerializers(this, session);
      GraphController[] controllers = new GraphController[serializers.length];

      for (int i = 0; i < controllers.length; i++)
      {
         controllers[i] = new GraphController(session, this, serializers[i]);
      }


      _grapControllersBySessionID.put(session.getIdentifier(), controllers);


      IObjectTreeAPI api = session.getSessionInternalFrame().getObjectTreeAPI();

      ActionCollection coll = getApplication().getActionCollection();
      api.addToPopup(DatabaseObjectType.TABLE, coll.get(AddToGraphAction.class));

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            // Graphs are only supported on the main session window.
         }
      };

      return ret;
   }


   public void sessionEnding(ISession session)
   {
      GraphController[] controllers = 
          _grapControllersBySessionID.remove(session.getIdentifier());

      for (int i = 0; i < controllers.length; i++)
      {
         controllers[i].sessionEnding();
      }
   }

   public GraphController[] getGraphControllers(ISession session)
   {
      return _grapControllersBySessionID.get(session.getIdentifier());
   }

   public String patchName(String name, ISession session)
   {

      int postfix = 0;
      if("Objects".equals(name))
      {
         ++postfix;
      }

      if("SQL".equals(name))
      {
         ++postfix;
      }

      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());

      while(true)
      {
         boolean incremented = false;
         for (int i = 0; i < controllers.length; i++)
         {
            if(0 == postfix)
            {
               if(controllers[i].getTitle().equals(name))
               {
                  ++postfix;
                  incremented = true;
               }
            }
            else
            {
               if(controllers[i].getTitle().equals(name + "_" + postfix))
               {
                  ++postfix;
                  incremented = true;
               }
            }
         }

         if(false == incremented)
         {
            break;
         }
      }

      if(0 == postfix)
      {
         return name;
      }
      else
      {
         return name + "_" + postfix;
      }




   }

   public GraphController createNewGraphControllerForSession(ISession session)
   {
      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());

      Vector<GraphController> v = new Vector<GraphController>();
      if(null != controllers)
      {
         v.addAll(Arrays.asList(controllers));
      }
      GraphController ret = new GraphController(session, this, null);
      v.add(ret);

      controllers = v.toArray(new GraphController[v.size()]);
      _grapControllersBySessionID.put(session.getIdentifier(), controllers);

      return ret;
   }

   public void removeGraphController(GraphController toRemove, ISession session)
   {
      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());
      Vector<GraphController> v = new Vector<GraphController>();
      for (int i = 0; i < controllers.length; i++)
      {
         if(false == controllers[i].equals(toRemove))
         {
            v.add(controllers[i]);
         }
      }

      controllers = v.toArray(new GraphController[v.size()]);
      _grapControllersBySessionID.put(session.getIdentifier(), controllers);

   }
}
