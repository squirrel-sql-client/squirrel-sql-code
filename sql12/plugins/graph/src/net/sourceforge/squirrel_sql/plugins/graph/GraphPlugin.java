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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.link.CopyGraphAction;
import net.sourceforge.squirrel_sql.plugins.graph.link.LinkGraphAction;
import net.sourceforge.squirrel_sql.plugins.graph.link.PasteGraphAction;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;

import javax.swing.Action;
import javax.swing.JMenu;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * The SQL Script plugin class.
 */
public class GraphPlugin extends DefaultSessionPlugin
{

   private Hashtable<IIdentifier, ArrayList<GraphController>> _grapControllersBySessionID = new Hashtable<IIdentifier, ArrayList<GraphController>>();

   /**
    * Logger for this class.
    */
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(GraphPlugin.class);

   private PluginResources _resources;

   private interface IMenuResourceKeys
   {
      String MENU = "graph";
   }



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
      return "2.0";
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
      return "doc/readme.html";
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


      createMenu();
   }

   private void createMenu()
   {
      IApplication app = getApplication();

      ActionCollection coll = app.getActionCollection();

      JMenu menu = _resources.createMenu(GraphPlugin.IMenuResourceKeys.MENU);
      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);

      addToCollectionAndMenu(coll, menu, new AddToGraphAction(app, _resources, this));
      addToCollectionAndMenu(coll, menu, new NewQueryBuilderWindowAction(app, _resources, this));
      addToCollectionAndMenu(coll, menu, new LinkGraphAction(app, _resources, this));
      addToCollectionAndMenu(coll, menu, new CopyGraphAction(app, _resources, this));
      addToCollectionAndMenu(coll, menu, new PasteGraphAction(app, _resources, this));
      addToCollectionAndMenu(coll, menu, new AddTableAtQursorToGraph(app, _resources, this));

   }

   private void addToCollectionAndMenu(ActionCollection coll, JMenu graphMenu, Action action)
   {
      coll.add(action);
      _resources.addToMenu(action, graphMenu);
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
      ArrayList<GraphController> controllers = new ArrayList<GraphController>();

      for (int i = 0; i < serializers.length; i++)
      {
         controllers.add(new GraphController(session, this, serializers[i], false, false));
      }

      GraphXmlSerializer[] linkedSerializers  = GraphXmlSerializer.getLinkedGraphXmSerializers(this, session);

      for (int i = 0; i < linkedSerializers.length; i++)
      {
         controllers.add(new GraphController(session, this, linkedSerializers[i], false, false));
      }


      _grapControllersBySessionID.put(session.getIdentifier(), controllers);


      IObjectTreeAPI objectTreeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

      ActionCollection coll = getApplication().getActionCollection();
      objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(AddToGraphAction.class));

      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(NewQueryBuilderWindowAction.class));
      session.addToToolbar(coll.get(LinkGraphAction.class));
      session.addToToolbar(coll.get(CopyGraphAction.class));
      session.addToToolbar(coll.get(PasteGraphAction.class));

      ISQLPanelAPI sqlPanelAPI = session.getSessionInternalFrame().getMainSQLPanelAPI();
      sqlPanelAPI.addToToolsPopUp("addtograph", coll.get(AddTableAtQursorToGraph.class));
      sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(AddTableAtQursorToGraph.class));

      return new PluginSessionCallbackAdaptor();
   }


   public void sessionEnding(ISession session)
   {
      ArrayList<GraphController> controllers = _grapControllersBySessionID.remove(session.getIdentifier());

      for (GraphController controller : controllers)
      {
         controller.sessionEnding();
      }
   }

   public GraphController[] getGraphControllers(ISession session)
   {
      return _grapControllersBySessionID.get(session.getIdentifier()).toArray(new GraphController[0]);
   }

   public GraphController getGraphControllerForMainTab(GraphMainPanelTab mainTab, ISession session)
   {
      GraphController[] graphControllers = getGraphControllers(session);


      GraphController graphController = null;
      for (GraphController g : graphControllers)
      {
         if(g.isMyGraphMainPanelTab(mainTab))
         {
            graphController = g;
            break;
         }
      }
      return graphController;
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

      ArrayList<GraphController> controllers = _grapControllersBySessionID.get(session.getIdentifier());

      while(true)
      {
         boolean incremented = false;
         for (int i = 0; null != controllers && i < controllers.size(); i++)
         {
            if(0 == postfix)
            {
               if(controllers.get(i).getTitle().equals(name))
               {
                  ++postfix;
                  incremented = true;
               }
            }
            else
            {
               if(controllers.get(i).getTitle().equals(name + "_" + postfix))
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

   public GraphController createNewGraphControllerForSession(ISession session, boolean showDndDesktopImageAtStartup)
   {
      return _createNewGraphControllerForSession(session, null, showDndDesktopImageAtStartup, false);
   }

   public void createNewGraphControllerForSession(ISession session, GraphXmlSerializer graphXmlSerializer, boolean selectTab)
   {
      _createNewGraphControllerForSession(session, graphXmlSerializer, false, selectTab);
   }

   private GraphController _createNewGraphControllerForSession(ISession session,
                                                               GraphXmlSerializer graphXmlSerializer,
                                                               boolean showDndDesktopImageAtStartup,
                                                               boolean selectTab)
   {
      ArrayList<GraphController> controllers = _grapControllersBySessionID.get(session.getIdentifier());

      if(null == controllers)
      {
         controllers = new ArrayList<GraphController>();
         _grapControllersBySessionID.put(session.getIdentifier(), controllers);
      }
      GraphController ret = new GraphController(session, this, graphXmlSerializer, showDndDesktopImageAtStartup, selectTab);
      controllers.add(ret);
      _grapControllersBySessionID.put(session.getIdentifier(), controllers);

      return ret;
   }



   public void removeGraphController(GraphController toRemove, ISession session)
   {
      ArrayList<GraphController> controllers = _grapControllersBySessionID.get(session.getIdentifier());

      if(null == controllers)
      {
         return;
      }


      for (int i = 0; i < controllers.size(); i++)
      {
         if(controllers.get(i).equals(toRemove))
         {
            controllers.remove(i);
            break;
         }
      }
   }

}
