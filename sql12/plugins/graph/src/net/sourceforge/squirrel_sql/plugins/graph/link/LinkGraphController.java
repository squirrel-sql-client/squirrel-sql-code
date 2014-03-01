package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;
import net.sourceforge.squirrel_sql.plugins.graph.GraphUtil;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.DefaultGraphXmlSerializerConfig;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class LinkGraphController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LinkGraphDialog.class);

   private static final String COL_HEADER_NAME = s_stringMgr.getString("linkGraph.col.name");
   private static final String COL_HEADER_FILE = s_stringMgr.getString("linkGraph.col.graphFile");

   private static final String PREF_KEY_LINK_COL_WIDTH_NAME = "Squirrel.graph.link.dlg.col.name.width";
   private static final String PREF_KEY_LINK_COL_WIDTH_FILE = "Squirrel.graph.link.dlg.col.graphFile.width";


   private LinkGraphDialog _linkGraphDialog;
   private SessionAdapter _sessionAdapter;

   private Timer _timer;
   private ExecutorService _executorService;
   private GraphPlugin _plugin;
   private ISession _session;
   private ArrayList<GraphFileDisplayBean> _graphFileDisplayBeans;
   private final ArrayList<LinkXmlBean> _existingLinks;


   public LinkGraphController(GraphPlugin graphPlugin, final ISession session)
   {
      _plugin = graphPlugin;
      _session = session;
      try
      {
         _linkGraphDialog = new LinkGraphDialog(graphPlugin, session);

         _existingLinks = getExistingLinks(_plugin);


         loadTable(graphPlugin, session, graphPlugin.getPluginUserSettingsFolder());

         _linkGraphDialog.addWindowListener(new WindowAdapter()
         {
            @Override
            public void windowClosing(WindowEvent e)
            {
               onWindowClosing();
            }
         });

         _sessionAdapter = new SessionAdapter()
         {
            @Override
            public void sessionClosing(SessionEvent evt)
            {
               onSessionClosing(evt, session);
            }
         };
         session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);




         _linkGraphDialog.txtDir.setText(graphPlugin.getPluginUserSettingsFolder().getPath());
         
         _linkGraphDialog.btnExplore.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onExplore();
            }
         });

         _linkGraphDialog.btnChangeDir.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onChangeDir();
            }
         });

         _linkGraphDialog.btnHomeDir.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onHomeDir();
            }
         });

         _linkGraphDialog.btnCreate.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               onCreate();
            }
         });

         
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private ArrayList<LinkXmlBean> getExistingLinks(GraphPlugin plugin)
   {
      try
      {
         String[] linkFileNames = plugin.getPluginUserSettingsFolder().list(new FilenameFilter()
         {
            @Override
            public boolean accept(File dir, String name)
            {
               if (name.startsWith(DefaultGraphXmlSerializerConfig.LINK_PREFIX))
               {
                  return true;
               }
               return false;
            }
         });

         ArrayList<LinkXmlBean> ret = new ArrayList<LinkXmlBean>();

         XMLBeanReader br = new XMLBeanReader();
         for (String linkFileName : linkFileNames)
         {
            br.load(new File(plugin.getPluginUserSettingsFolder().getPath(), linkFileName), LinkGraphController.class.getClassLoader());
            ret.add((LinkXmlBean) br.iterator().next());
         }

         return ret;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onCreate()
   {
      try
      {

         int[] seletedRowIndexes = _linkGraphDialog.tblGraphFiles.getSeletedModelRows();

         for (int seletedRowIndex : seletedRowIndexes)
         {
            GraphFileDisplayBean gdb = _graphFileDisplayBeans.get(seletedRowIndex);

            String linkName = _plugin.patchName(gdb.getName().toString(), _session);

            File pathOfLinkXmlFile = new File(_plugin.getPluginUserSettingsFolder().getPath(), createLinkFileName(gdb));

            LinkXmlBean linkXmlBean =
                  new LinkXmlBean(linkName, gdb.getName().toString(), gdb.getGraphFile().getAbsolutePath());

            XMLBeanWriter bw = new XMLBeanWriter(linkXmlBean);

            bw.save(pathOfLinkXmlFile.getAbsolutePath());

            GraphXmlSerializer graphXmlSerializer =
                  new GraphXmlSerializer(_plugin, _session, linkXmlBean, pathOfLinkXmlFile.getAbsolutePath());

            _plugin.createNewGraphControllerForSession(_session, graphXmlSerializer, false);

         }

         onWindowClosing();
         _linkGraphDialog.setVisible(false);
         _linkGraphDialog.dispose();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private String createLinkFileName(GraphFileDisplayBean gdb)
   {
      return
            DefaultGraphXmlSerializerConfig.LINK_PREFIX +
            GraphUtil.createGraphFileName(_session.getAlias().getUrl(), _plugin.patchName(gdb.getLoadedName(), _session)) +
            DefaultGraphXmlSerializerConfig.XML_BEAN_POSTFIX;
   }

   private void onHomeDir()
   {
      try
      {
         _linkGraphDialog.txtDir.setText(_plugin.getPluginUserSettingsFolder().getPath());
         loadTable(_plugin, _session, _plugin.getPluginUserSettingsFolder());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void onChangeDir()
   {
      try
      {
         JFileChooser chooser = new JFileChooser(_linkGraphDialog.txtDir.getText());
         chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         int returnVal = chooser.showOpenDialog(_linkGraphDialog);
         if (returnVal == JFileChooser.APPROVE_OPTION)
         {
            _linkGraphDialog.txtDir.setText(chooser.getSelectedFile().getAbsolutePath());
            loadTable(_plugin, _session, new FileWrapperFactoryImpl().create(chooser.getSelectedFile()));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   private void onExplore()
   {
      try
      {
         Desktop desktop = Desktop.getDesktop();
         desktop.open(new File(_linkGraphDialog.txtDir.getText()));
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void loadTable(GraphPlugin graphPlugin, ISession session, FileWrapper path) throws IOException, DataSetException
   {
      _graphFileDisplayBeans = new ArrayList<GraphFileDisplayBean>();


      _timer = new Timer(500, new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            doRepaintToShowLoadedNames();
         }
      });
      _timer.setRepeats(false);


      _executorService = Executors.newFixedThreadPool(3);

      NameLoadFinishListener nameLoadFinishListener = new NameLoadFinishListener()
      {
         @Override
         public void finishedNameLoad()
         {
            _timer.restart();
         }
      };

      boolean isInUserSettingsFolder = graphPlugin.getPluginUserSettingsFolder().getPath().equals(path.getPath());

      GraphXmlSerializer[] serializers = GraphXmlSerializer.getGraphXmlSerializersInPath(graphPlugin, session, path);

      for (GraphXmlSerializer serializer : serializers)
      {
         if(isInUserSettingsFolder && belongsToUrl(serializer, session.getAlias().getUrl()))
         {
            continue;
         }

         if(linkExists(serializer))
         {
            continue;
         }

         GraphFileDisplayBean bean =
               new GraphFileDisplayBean(new File(serializer.getGraphFile()), serializer, nameLoadFinishListener, _executorService);

         _graphFileDisplayBeans.add(bean);


      }

      JavabeanArrayDataSet javabeanArrayDataSet = new JavabeanArrayDataSet(GraphFileDisplayBean.class);

      javabeanArrayDataSet.setIgnoreProperty("graphFile");
      javabeanArrayDataSet.setIgnoreProperty("loadedName");

      javabeanArrayDataSet.setColHeader("name", COL_HEADER_NAME);
      javabeanArrayDataSet.setColPos("name", 1);
      javabeanArrayDataSet.setAbsoluteWidht("name", Preferences.userRoot().getInt(PREF_KEY_LINK_COL_WIDTH_NAME, 200));

      javabeanArrayDataSet.setColHeader("graphFileName", COL_HEADER_FILE);
      javabeanArrayDataSet.setColPos("graphFileName", 2);
      javabeanArrayDataSet.setAbsoluteWidht("graphFileName", Preferences.userRoot().getInt(PREF_KEY_LINK_COL_WIDTH_FILE, 200));

      javabeanArrayDataSet.setJavaBeanList(_graphFileDisplayBeans);

      _linkGraphDialog.tblGraphFiles.show(javabeanArrayDataSet);
   }

   private boolean belongsToUrl(GraphXmlSerializer serializer, String url)
   {
      return new File(serializer.getGraphFile()).getName().startsWith(GraphUtil.createGraphFileNamePrefixForUrl(url));
   }

   private boolean linkExists(GraphXmlSerializer serializer)
   {
      for (LinkXmlBean existingLink : _existingLinks)
      {
         if(Utilities.equalsRespectNull(existingLink.getFilePathOfLinkedGraph(), serializer.getGraphFile()))
         {
            return true;
         }
      }

      return false;
   }


   private void doRepaintToShowLoadedNames()
   {
      if (_linkGraphDialog.isVisible())
      {
         _linkGraphDialog.tblGraphFiles.getComponent().repaint();
      }
   }

   private void onSessionClosing(SessionEvent evt, ISession session)
   {
      if(evt.getSession().getIdentifier().equals(session.getIdentifier()))
      {
         onWindowClosing();
         _linkGraphDialog.setVisible(false);
         _linkGraphDialog.dispose();
         evt.getSession().getApplication().getSessionManager().removeSessionListener(_sessionAdapter);

      }
   }

   private void onWindowClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_LINK_COL_WIDTH_NAME, _linkGraphDialog.tblGraphFiles.getColumnWidthForHeader(COL_HEADER_NAME));
      Preferences.userRoot().putInt(PREF_KEY_LINK_COL_WIDTH_FILE, _linkGraphDialog.tblGraphFiles.getColumnWidthForHeader(COL_HEADER_FILE));
   }
}
