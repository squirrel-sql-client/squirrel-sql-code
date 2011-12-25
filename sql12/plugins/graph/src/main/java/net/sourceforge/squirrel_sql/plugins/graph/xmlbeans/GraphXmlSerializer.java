package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.GraphUtil;
import net.sourceforge.squirrel_sql.plugins.graph.link.LinkXmlBean;


public class GraphXmlSerializer
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphXmlSerializer.class);

   private final static ILogger s_log = LoggerController.createLogger(GraphXmlSerializer.class);


   private static final String XML_BEAN_POSTFIX = ".graph.xml";
   public static final String LINK_PREFIX = "lnk_";

   private GraphPlugin _plugin;
   private ISession _session;
   private String _graphFile;
   private LinkXmlBean _linkXmlBean;
   private String _pathOfLinkXmlFile;

   public GraphXmlSerializer(GraphPlugin plugin, ISession session, String title, String graphFileName)
   {
      _init(plugin, session, title, graphFileName);
   }

   public GraphXmlSerializer(GraphPlugin plugin, ISession session, LinkXmlBean linkXmlBean, String pathOfLinkXmlFile)
   {
      _linkXmlBean = linkXmlBean;
      _pathOfLinkXmlFile = pathOfLinkXmlFile;
      _init(plugin, session, _linkXmlBean.getLinkName(), _linkXmlBean.getFilePathOfLinkedGraph());
   }

   private void _init(GraphPlugin plugin, ISession session, String title, String graphFileName)
   {
      try
      {
         _plugin = plugin;
         _session = session;
         String url = _session.getAlias().getUrl();

         if(null == graphFileName)
         {
            _graphFile = getFileName(plugin.getPluginUserSettingsFolder().getPath(), url, title);
         }
         else
         {
            _graphFile = graphFileName;
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void write(GraphControllerXmlBean xmlBean)
   {
      try
      {
         if(null != _linkXmlBean)
         {
            if(false == new File(_graphFile).canWrite())
            {
               String msg = s_stringMgr.getString("graph.link.cannotWrite", _linkXmlBean.getNameOfLinkedGraph(), _graphFile);
               _session.getApplication().getMessageHandler().showErrorMessage(msg);
               throw new IllegalStateException(msg);
            }
         }

         Version32Converter.markConverted(xmlBean);

         XMLBeanWriter bw = new XMLBeanWriter(xmlBean);
         bw.save(_graphFile);

			String[] params = {xmlBean.getTitle(), _graphFile};
			// i18n[graph.graphSaved=Graph "{0}" saved to "{1}"]
			String msg = s_stringMgr.getString("graph.graphSaved", params);

			_session.showMessage(msg);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void saveLinkAsLocalCopy(GraphControllerXmlBean xmlBean)
   {
      if(false == isLink())
      {
         throw new IllegalStateException("Not a link");
      }

      transformMeFromLinkToLocalCopy();
      write(xmlBean);
   }

   private void transformMeFromLinkToLocalCopy()
   {
      try
      {
         String linkName = new File(_pathOfLinkXmlFile).getName();
         String newGraphsName = linkName.substring(LINK_PREFIX.length(), linkName.length());
         _graphFile = new File(_plugin.getPluginUserSettingsFolder().getPath(), newGraphsName).getAbsolutePath();
         _linkXmlBean = null;
         new File(_pathOfLinkXmlFile).delete();
         _pathOfLinkXmlFile = null;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   public GraphControllerXmlBean read()
   {
      try
      {
         XMLBeanReader br = new XMLBeanReader();
         br.load(_graphFile, this.getClass().getClassLoader());
         GraphControllerXmlBean ret = (GraphControllerXmlBean) br.iterator().next();
         Version32Converter.convert(ret);
         
         if(null != _linkXmlBean)
         {
            ret.setTitle(_linkXmlBean.getLinkName());
         }

         return ret;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   private String getFileName(String path, String url, String title)
   {
      final String filePrefix = GraphUtil.createGraphFileName(url, title);
      File p = new File(path);

      String buf = filePrefix;
      for(int i=1; prefixExists(p, buf); ++i)
      {
         buf = filePrefix + "_" + i ;
      }

      return path + File.separator +  buf + XML_BEAN_POSTFIX;
   }

   private boolean prefixExists(File path, final String filePrefix)
   {
      File[] files = path.listFiles(new FilenameFilter()
               {
                  public boolean accept(File dir, String name)
                  {
                     if(name.toLowerCase().equals(filePrefix.toLowerCase() + XML_BEAN_POSTFIX))
                     {
                        return true;
                     }
                     return false;
                  }
               });
      return 0 < files.length;
   }

   public static GraphXmlSerializer[] getGraphXmSerializers(GraphPlugin plugin, ISession session)
   {
      try
      {
         final String urlPrefix = GraphUtil.javaNormalize(session.getAlias().getUrl()) + ".";

         FileWrapper[] graphXmlFiles = plugin.getPluginUserSettingsFolder().listFiles(new FilenameFilter()
         {
            public boolean accept(File dir, String name)
            {
               if (name.startsWith(urlPrefix))
               {
                  return true;
               }
               return false;
            }
         });

         return createSerializers(plugin, session, graphXmlFiles);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static GraphXmlSerializer[] getLinkedGraphXmSerializers(GraphPlugin plugin, ISession session)
   {
      try
      {
         final String linkPrefix = LINK_PREFIX + GraphUtil.javaNormalize(session.getAlias().getUrl()) + ".";

         FileWrapper[] linkXmlFiles = plugin.getPluginUserSettingsFolder().listFiles(new FilenameFilter()
         {
            public boolean accept(File dir, String name)
            {
               if (name.startsWith(linkPrefix))
               {
                  return true;
               }
               return false;
            }
         });

         ArrayList<GraphXmlSerializer> xmlSerializers = new ArrayList<GraphXmlSerializer>();
         for (FileWrapper linkXmlFile : linkXmlFiles)
         {
            XMLBeanReader br = new XMLBeanReader();
            br.load(linkXmlFile, GraphXmlSerializer.class.getClassLoader());
            LinkXmlBean linkXmlBean = (LinkXmlBean) br.iterator().next();

            if (new File(linkXmlBean.getFilePathOfLinkedGraph()).canRead())
            {
               xmlSerializers.add(new GraphXmlSerializer(plugin, session, linkXmlBean, linkXmlFile.getAbsolutePath()));
            }
            else
            {
               String msg = s_stringMgr.getString("graph.link.cannotRead", linkXmlBean.getNameOfLinkedGraph(), linkXmlBean.getFilePathOfLinkedGraph());
               session.getApplication().getMessageHandler().showErrorMessage(msg);
               s_log.error(msg, new IllegalStateException(msg));
            }
         }

         return xmlSerializers.toArray(new GraphXmlSerializer[xmlSerializers.size()]);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   

   private static GraphXmlSerializer[] createSerializers(GraphPlugin plugin, ISession session, FileWrapper[] graphXmlFiles)
   {
      GraphXmlSerializer[] ret = new GraphXmlSerializer[graphXmlFiles.length];
      for (int i = 0; i < graphXmlFiles.length; i++)
      {
         ret[i] = new GraphXmlSerializer(plugin, session, (String)null, graphXmlFiles[i].getPath());
      }

      return ret;
   }

   public static GraphXmlSerializer[] getGraphXmlSerializersInPath(GraphPlugin graphPlugin, ISession session, FileWrapper path)
   {
      FileWrapper[] graphXmlFiles = path.listFiles(new FilenameFilter()
      {
         public boolean accept(File dir, String name)
         {
            if (name.toLowerCase().endsWith(".xml") && containsGraphControllerXmlBeanTag(new File(dir, name)))
            {
               return true;
            }
            return false;
         }
      });
      
      return createSerializers(graphPlugin, session, graphXmlFiles);
   }

   private static boolean containsGraphControllerXmlBeanTag(File file)
   {
      try
      {
         BufferedReader br = new FileWrapperFactoryImpl().create(file).getBufferedReader();

         String line = br.readLine();
         while(null != line)
         {
            if(-1 < line.indexOf(GraphControllerXmlBean.class.getName()))
            {
               br.close();
               return true;
            }
            line = br.readLine();
         }

         br.close();
         return false;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   
   public void rename(String newName)
   {
      try
      {
         String url = _session.getAlias().getUrl();
         String newGraphFile = getFileName(_plugin.getPluginUserSettingsFolder().getPath(), url, newName);
         (new File(_graphFile)).renameTo(new File(newGraphFile));

			String[] params = {_graphFile, newGraphFile};
			// i18n[graph.graphRenamed=Renamed "{0}" to "{1}"]
			_session.showMessage(s_stringMgr.getString("graph.graphRenamed", params));

         _graphFile = newGraphFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }


   }

   public void remove()
   {
      (new File(_graphFile)).delete();

		String[] params = {_graphFile};
		// i18n[graph.graphRemoved=Removed graph file "{0}"]
      _session.showMessage(s_stringMgr.getString("graph.graphRemoved", params));

   }

   public String getGraphFile()
   {
      return _graphFile;
   }

   public boolean isLink()
   {
      return null != _linkXmlBean;
   }

   public void removeLink()
   {
      if(false == isLink())
      {
         throw new IllegalStateException("Not a link");
      }

      new File(_pathOfLinkXmlFile).delete();
      _session.showMessage(s_stringMgr.getString("graph.graphLinkRemoved", _pathOfLinkXmlFile));
   }

   public LinkXmlBean getLinkXmlBean()
   {
      return _linkXmlBean;
   }
}
