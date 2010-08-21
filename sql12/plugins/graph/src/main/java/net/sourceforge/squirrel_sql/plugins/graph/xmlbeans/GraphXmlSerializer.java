package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphMainPanelTab;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;


public class GraphXmlSerializer
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphXmlSerializer.class);

   private static final String XML_BEAN_POSTFIX = ".graph.xml";

   private GraphPlugin _plugin;
   private ISession _session;
   private String _graphFile;

   /**
    * Either graphPane or graphFileName might be null.
    */
   public GraphXmlSerializer(GraphPlugin plugin, ISession session, GraphMainPanelTab graphPane, String graphFileName)
   {
      try
      {
         _plugin = plugin;
         _session = session;
         String url = _session.getAlias().getUrl();

         if(null == graphFileName)
         {
            _graphFile = getFileName(plugin.getPluginUserSettingsFolder().getPath(), url, graphPane.getTitle());
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

   public GraphControllerXmlBean read()
   {
      try
      {
         XMLBeanReader br = new XMLBeanReader();
         br.load(_graphFile, this.getClass().getClassLoader());
         return (GraphControllerXmlBean) br.iterator().next();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   private String getFileName(String path, String url, String title)
   {
      final String filePrefix = javaNormalize(url) + "." + javaNormalize(title);
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

   private static String javaNormalize(String text)
   {
      StringBuffer buf = new StringBuffer(text.length());

      if(Character.isJavaIdentifierStart(text.charAt(0)) )
      {
         buf.append(text.charAt(0));
      }
      else
      {
         buf.append('_');
      }


      for(int i=1; i < text.length(); ++i)
      {
         if ( Character.isLetterOrDigit(text.charAt(i)) )
         {
            buf.append(text.charAt(i));
         }
         else
         {
            buf.append('_');
         }
      }

      String ret = buf.toString();

      return ret;
   }

   public static GraphXmlSerializer[] getGraphXmSerializers(GraphPlugin plugin, ISession session)
   {
      try
      {
         File settingsPath = plugin.getPluginUserSettingsFolder();
         final String urlPrefix = javaNormalize(session.getAlias().getUrl()) + ".";

         File[] graphXmlFiles = settingsPath.listFiles(new FilenameFilter()
                  {
                     public boolean accept(File dir, String name)
                     {
                        if(name.startsWith(urlPrefix))
                        {
                           return true;
                        }
                        return false;
                     }
                  });

         GraphXmlSerializer[] ret = new GraphXmlSerializer[graphXmlFiles.length];
         for (int i = 0; i < graphXmlFiles.length; i++)
         {
            ret[i] = new GraphXmlSerializer(plugin, session, null, graphXmlFiles[i].getPath());
         }

         return ret;

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
}
