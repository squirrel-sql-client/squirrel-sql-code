package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.IMainPanelTabAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphController;
import net.sourceforge.squirrel_sql.plugins.graph.GraphMainPanelTab;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphControllerXmlBean;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class CopyGraphAction extends SquirrelAction implements IMainPanelTabAction
{
   private GraphPlugin _graphPlugin;
   private ISession _session;
   private GraphMainPanelTab _selectedMainTab;

   public CopyGraphAction(IApplication app, PluginResources resources, GraphPlugin graphPlugin)
   {
      super(app, resources);
      _graphPlugin = graphPlugin;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if (null == _session || null == _selectedMainTab)
      {
         return;
      }

      GraphController graphController = _graphPlugin.getGraphControllerForMainTab(_selectedMainTab, _session);

      copyGraph(graphController, false);
   }

   public static void copyGraph(GraphController graphController, boolean selectionOnly)
   {
      try
      {
         GraphControllerXmlBean xmlBean = graphController.createXmlBean(selectionOnly);
         xmlBean.setConverted32(true);

         final StringSelection ss = new StringSelection(new XMLBeanWriter(xmlBean).getAsString());
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void setSession(ISession session)
   {
      _session = session;
   }

   @Override
   public void setSelectedMainPanelTab(IMainPanelTab selectedMainTab)
   {
      if (selectedMainTab instanceof GraphMainPanelTab)
      {
         _selectedMainTab = (GraphMainPanelTab) selectedMainTab;
         setEnabled(true);
      }
      else
      {
         _selectedMainTab = null;
         setEnabled(false);
      }
   }
}
