package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.link.LinkXmlBean;


public class GraphXmlSerializer
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphXmlSerializer.class);

   private final static ILogger s_log = LoggerController.createLogger(GraphXmlSerializer.class);

   private DefaultGraphXmlSerializerConfig _cfg;
   private ISession _session;

   public GraphXmlSerializer(GraphPlugin plugin, ISession session, String graphFileName)
   {
      _session = session;
      _cfg = new DefaultGraphXmlSerializerConfig(plugin, session, graphFileName);
   }

   public GraphXmlSerializer(GraphPlugin plugin, ISession session, LinkXmlBean linkXmlBean, String pathOfLinkXmlFile)
   {
      _session = session;
      _cfg = new DefaultGraphXmlSerializerConfig(plugin, session, linkXmlBean, pathOfLinkXmlFile);
   }

   public void write(GraphControllerXmlBean xmlBean)
   {
      try
      {
         _cfg.checkTargetWritable();

         Version32Converter.markConverted(xmlBean);

         XMLBeanWriter bw = new XMLBeanWriter(xmlBean);
         bw.save(_cfg.getGraphFilePath());

			String[] params = {xmlBean.getTitle(), _cfg.getGraphFilePath()};
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

      _cfg.transformToLocalCopy();
      write(xmlBean);
   }


   public GraphControllerXmlBean read()
   {
      try
      {
         XMLBeanReader br = new XMLBeanReader();
         br.load(LegacyNanoxmlConverter.convertXml(_cfg.getGraphFilePath()), this.getClass().getClassLoader());
         GraphControllerXmlBean ret = (GraphControllerXmlBean) br.iterator().next();
         Version32Converter.convert(ret);
         
         if(_cfg.isLink())
         {
            ret.setTitle(_cfg.getTitle());
         }
         else
         {
            _cfg.setTitle(ret.getTitle());
         }

         return ret;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   public static GraphXmlSerializer[] getGraphXmSerializers(GraphPlugin plugin, ISession session)
   {
      try
      {
         final String urlPrefix = StringUtilities.javaNormalize(session.getAlias().getUrl()) + ".";

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
         final String linkPrefix = DefaultGraphXmlSerializerConfig.getLinkPrefix(session);

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
               String msg =
                     s_stringMgr.getString("graph.link.cannotRead",
                                           linkXmlBean.getNameOfLinkedGraph(),
                                           linkXmlBean.getFilePathOfLinkedGraph(),
                                           linkXmlFile.getAbsolutePath());

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
         ret[i] = new GraphXmlSerializer(plugin, session, graphXmlFiles[i].getPath());
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
         _cfg.renameGraph(newName);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }


   }

   public void remove()
   {
      _cfg.removeGraphFile();
   }

   public void detach()
   {
      _cfg.detachGraphFile();
   }

   public String getGraphFile()
   {
      return _cfg.getGraphFilePath();
   }

   public boolean isLink()
   {
      return _cfg.isLink();
   }

   public void removeLink()
   {
      _cfg.removeLink();
   }

   public LinkXmlBean getLinkXmlBean()
   {
      return _cfg.getLinkXmlBean();
   }

   public String getTitle()
   {
      return _cfg.getTitle();
   }

   public boolean isLoadable()
   {
      return _cfg.isLoadable();
   }

   public String getLinkFile()
   {
      return _cfg.getPathOfLinkXmlFile();
   }
}
