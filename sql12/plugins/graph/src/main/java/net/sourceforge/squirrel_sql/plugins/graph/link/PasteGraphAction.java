package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphControllerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

public class PasteGraphAction extends SquirrelAction implements ISessionAction
{
   private final static ILogger s_log = LoggerController.createLogger(PasteGraphAction.class);

   private GraphPlugin _plugin;
   private ISession _session;

   public PasteGraphAction(IApplication app, PluginResources resources, GraphPlugin plugin)
   {
      super(app, resources);
      _plugin = plugin;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      try
      {
         if(null == _session)
         {
            return;
         }

         String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);


         File graphTmp = File.createTempFile("graphTmp_", "_.xml");

         FileWriter fw = new FileWriter(graphTmp);
         fw.write(data);
         fw.flush();
         fw.close();


         XMLBeanReader br = new XMLBeanReader();
         try
         {
            br.load(graphTmp, this.getClass().getClassLoader());
         }
         catch (Exception exc)
         {
            _session.showWarningMessage("Could not interpret clipboard as Graph: " + exc.getMessage());
            s_log.warn("Could not interpret clipboard as Graph: ", exc);
            return;
         }


         GraphControllerXmlBean bean = (GraphControllerXmlBean) br.iterator().next();

         String title = _plugin.patchName(bean.getTitle(), _session);

         if(false == title.equals(bean.getTitle()))
         {
            bean.setTitle(title);

            XMLBeanWriter xbw = new XMLBeanWriter(bean);
            xbw.save(graphTmp);
         }

         String graphFile = graphTmp.getAbsolutePath();
         GraphXmlSerializer graphXmlSerializer = new GraphXmlSerializer(_plugin, _session, graphFile);

         _plugin.createNewGraphControllerForSession(_session, graphXmlSerializer, true);
      }
      catch (Exception exc)
      {
         throw new RuntimeException(exc);
      }

   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
   }

}
