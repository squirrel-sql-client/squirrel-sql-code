package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.GraphUtil;
import net.sourceforge.squirrel_sql.plugins.graph.link.LinkXmlBean;

public class DefaultGraphXmlSerializerConfig
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DefaultGraphXmlSerializerConfig.class);

   
   public static final String XML_BEAN_POSTFIX = ".graph.xml";
   public static final String LINK_PREFIX = "lnk_";

   public static final String  DETACHED_GRAPH_PREFIX = "___DETACHED___";

   GraphPlugin _plugin;
   private ISession _session;


   private String _graphFilePath;
   private LinkXmlBean _linkXmlBean;
   private String _pathOfLinkXmlFile;
   private String _title;


   public DefaultGraphXmlSerializerConfig(GraphPlugin plugin, ISession session, LinkXmlBean linkXmlBean, String pathOfLinkXmlFile)
   {

      _linkXmlBean = linkXmlBean;
      _pathOfLinkXmlFile = pathOfLinkXmlFile;
      _init(plugin, session, _linkXmlBean.getFilePathOfLinkedGraph());
   }

   public DefaultGraphXmlSerializerConfig(GraphPlugin plugin, ISession session, String graphFileName)
   {
      _init(plugin, session, graphFileName);
   }

   private void _init(GraphPlugin plugin, ISession session, String graphFileName)
   {
      _plugin = plugin;
      _session = session;

      _title = _plugin.patchName(s_stringMgr.getString("graph.newGraph"), _session);


      if (null == graphFileName)
      {
         _graphFilePath = generateGraphFilePath(_title);
      }
      else
      {
         _graphFilePath = graphFileName;
      }
   }

   private String generateGraphFilePath(String title)
   {
      try
      {
         String url = _session.getAlias().getUrl();
         return getFileName(_plugin.getPluginUserSettingsFolder().getPath(), url, title);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   public boolean isLink()
   {
      return null != _linkXmlBean;
   }

   public String getGraphFilePath()
   {
      return _graphFilePath;
   }

   public LinkXmlBean getLinkXmlBean()
   {
      return _linkXmlBean;
   }

   public void renameGraph(String newGraphName)
   {
      try
      {
         String url = _session.getAlias().getUrl();
         String newGraphFile = getFileName(_plugin.getPluginUserSettingsFolder().getPath(), url, newGraphName);

         if (new File(_graphFilePath).exists())
         {
            if(false == new File(_graphFilePath).renameTo(new File(newGraphFile)))
            {
               String msg = "Failed to rename file: " + _graphFilePath;
               _session.showErrorMessage(msg);
               throw new IllegalStateException(msg);
            }

            String[] params = {_graphFilePath, newGraphFile};
            // i18n[graph.graphRenamed=Renamed "{0}" to "{1}"]
            _session.showMessage(s_stringMgr.getString("graph.graphRenamed", params));
         }

         _graphFilePath = newGraphFile;
         _title = newGraphName;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static String getLinkPrefix(ISession session)
   {
      return LINK_PREFIX + StringUtilities.javaNormalize(session.getAlias().getUrl()) + ".";
   }

   private String getFileName(String path, String url, String title)
   {
      String graphFileName = GraphUtil.createGraphFileName(url, _plugin.patchName(title, _session));
      return path + File.separator +  graphFileName + XML_BEAN_POSTFIX;
   }

   public String getTitle()
   {
      if (isLink())
      {
         return _linkXmlBean.getLinkName();
      }
      else
      {
         return _title;
      }
   }

   public void transformToLocalCopy()
   {
      try
      {
         String linkName = new File(_pathOfLinkXmlFile).getName();
         String newGraphsName = linkName.substring(LINK_PREFIX.length(), linkName.length());
         _graphFilePath = new File(_plugin.getPluginUserSettingsFolder().getPath(), newGraphsName).getAbsolutePath();
         _linkXmlBean = null;
         new File(_pathOfLinkXmlFile).delete();
         _pathOfLinkXmlFile = null;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void checkTargetWritable()
   {
      if(isLink())
      {
         if(false == new File(_graphFilePath).canWrite())
         {
            String msg =
                  s_stringMgr.getString("graph.link.cannotWrite",
                                        _linkXmlBean.getNameOfLinkedGraph(),
                                        _graphFilePath,
                                        _pathOfLinkXmlFile);

            _session.getApplication().getMessageHandler().showErrorMessage(msg);
            throw new IllegalStateException(msg);
         }
      }
   }

   public void removeLink()
   {
      if(false == isLink())
      {
         throw new IllegalStateException("Not a link");
      }


      if(false == new File(_pathOfLinkXmlFile).delete())
      {
         String msg = "Failed to remove link file: " + _pathOfLinkXmlFile;
         _session.showErrorMessage(msg);
         throw new IllegalStateException(msg);
      }


      _session.showMessage(s_stringMgr.getString("graph.graphLinkRemoved", _pathOfLinkXmlFile));
   }

   public void setTitle(String title)
   {
      _title = title;
      _graphFilePath = generateGraphFilePath(title);
   }

   public boolean isLoadable()
   {
      if(isLink() || (null != _graphFilePath && new File(_graphFilePath).exists()))
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public void detachGraphFile()
   {
      try
      {
         Path path = Path.of(_graphFilePath);
         if( path.toFile().exists())
         {
            //Files.move(path, Path.of(path.getParent() + File.separator + DETACHED_GRAPH_PREFIX + path.getFileName()));
            Files.move(path, path.resolveSibling(DETACHED_GRAPH_PREFIX + path.getFileName()));
         }
      }
      catch(IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public void removeGraphFile()
   {
      if (new File(_graphFilePath).exists())
      {
         if(false == new File(_graphFilePath).delete())
         {
            String msg = "Failed to remove file: " + _graphFilePath;
            _session.showErrorMessage(msg);
            throw new IllegalStateException(msg);
         }

         String[] params = {_graphFilePath};
         // i18n[graph.graphRemoved=Removed graph file "{0}"]
         _session.showMessage(s_stringMgr.getString("graph.graphRemoved", params));
      }
   }

   public String getPathOfLinkXmlFile()
   {
      if(false == isLink())
      {
         throw new IllegalStateException("Not a link");
      }
      return _pathOfLinkXmlFile;
   }
}