package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.io.IOException;

public class I18nPanelController implements IGlobalPreferencesPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanelController.class);

   I18nPanel _panel;
   private IApplication _app;
   private BundlesTableModel _bundlesTableModel;

   private JPopupMenu _popUp = new JPopupMenu();
   private JMenuItem _mnuGenerateTemplateComments = new JMenuItem(s_stringMgr.getString("I18n.generateTemplateComments"));
   // i18n[I18n.generateTemplateComments=Generate template comments for missing translations]

   private JMenuItem _mnuOpenInEditor = new JMenuItem(s_stringMgr.getString("I18n.openIOnEditor"));
   // i18n[I18n.openIOnEditor=Open in Editor]

   I18nPanelController(PluginResources resources)
   {
      _panel = new I18nPanel(resources);

      _bundlesTableModel = new BundlesTableModel();

      _panel.tblBundels.setModel(_bundlesTableModel);


      _panel.tblBundels.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            maybeShowPopup(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            maybeShowPopup(e);
         }
      });

      _popUp.add(_mnuGenerateTemplateComments);
      _popUp.add(_mnuOpenInEditor);

      _mnuGenerateTemplateComments.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onGenerate();
         }
      });

      _mnuOpenInEditor.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            System.out.println("_mnuOpenInEditor");
         }
      });


      Locale[] availableLocales = Locale.getAvailableLocales();

      Arrays.sort(availableLocales, new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            return o1.toString().compareTo(o2.toString());
         }
      });


      for (int i = 0; i < availableLocales.length; i++)
      {
         _panel.cboLocales.addItem(availableLocales[i]);
      }

      _panel.cboLocales.setSelectedItem(Locale.getDefault());

      _panel.btnLoad.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLoadBundels(_app);
         }
      });


   }

   public void initialize(final IApplication app)
   {
      _app = app;

   }

   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         _popUp.show(e.getComponent(), e.getX(), e.getY());
      }
   }


   private void onGenerate()
   {
      String buf = _panel.txtWorkingDir.getText();
      if(null == buf || 0 == buf.trim().length())
      {
         String msg = s_stringMgr.getString("I18n.NoWorkDir");
         // I18n[I18n.NoWorkDir=Please choose a work dir to store your translations.]

         JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
         return;

      }


      File workDir = new File(buf);
      if(false == workDir.isDirectory())
      {
         String msg = s_stringMgr.getString("I18n.WorkDirIsNotADirectory", workDir.getPath());
         // I18n[I18n.WorkDirIsNotADirectory=Working directory {0} is not a directory]

         JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
         return;
      }

      if(false == workDir.exists())
      {
         String msg = s_stringMgr.getString("I18n.WorkDirDoesNotExistQuestionCreate", workDir.getPath());
         // I18n[I18n.WorkDirDoesNotExistQuestionCreate=Working directory {0} does not exist.\nDo you want to create it?]

         if(JOptionPane.YES_OPTION ==  JOptionPane.showConfirmDialog(_app.getMainFrame(), msg))
         {
            if(false == workDir.mkdirs())
            {
               msg = s_stringMgr.getString("I18n.CouldNotCreateWorkDir", workDir.getPath());
               // I18n[I18n.CouldNotCreateWorkDir=Could not create Working directory {0}]
               JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
               return;

            }
         }
      }


      int[] selRows = _panel.tblBundels.getSelectedRows();
      I18nBundle[] selBundles = _bundlesTableModel.getBundlesForRows(selRows);

      for (int i = 0; i < selBundles.length; i++)
      {
         selBundles[i].writeMissingProps(_app, workDir);
      }
   }


   private void onLoadBundels(IApplication app)
   {
      Locale selLocale = (Locale) _panel.cboLocales.getSelectedItem();

      URL[] urLs = ((URLClassLoader) getClass().getClassLoader()).getURLs();

      String pluginDir = new ApplicationFiles().getPluginsDirectory().getPath();

      ArrayList defaultI18nProps = new ArrayList();
      ArrayList localizedI18nProps = new ArrayList();

      String i18nPropsFileName = "I18NStrings.properties";
      String localizedI18nPropsFileName = "I18NStrings_" + selLocale + ".properties";

      String workDir = _panel.txtWorkingDir.getText();
      if(null != workDir && 0 < workDir.trim().length())
      {
         File f = new File(workDir);
         if(false == f.exists())
         {
            String msg = s_stringMgr.getString("I18n.noWorkdir");
            // i18n[I18n.noWorkdir=Working directory doesn't exist.\nDo you want to create it?]
            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(app.getMainFrame(), msg))
            {
               f.mkdirs();
            }
         }
         else if(false == f.isDirectory())
         {
            String msg = s_stringMgr.getString("I18n.WorkdirIsNoDir", f.getAbsolutePath());
            // i18n[I18n.WorkdirIsNoDir=The working directory is not a directory.\nNo bundles will be loaded from {0}]
            JOptionPane.showMessageDialog(app.getMainFrame(), msg);

         }
         else
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, f, defaultI18nProps, localizedI18nProps);
         }
      }

      for (int i = 0; i < urLs.length; i++)
      {
         File file = new File(urLs[i].getFile());

         if(file.isDirectory())
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
         else if (file.getName().equalsIgnoreCase("squirrel-sql.jar") || file.getName().equalsIgnoreCase("fw.jar"))
         {
            findI18nInArchive(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
         else if(file.getPath().startsWith(pluginDir))
         {
            findI18nInArchive(i18nPropsFileName, localizedI18nPropsFileName, file, defaultI18nProps, localizedI18nProps);
         }
      }

      Hashtable i18nBundlesByName = new Hashtable();

      for (int i = 0; i < defaultI18nProps.size(); i++)
      {
         I18nProps i18nProps = (I18nProps) defaultI18nProps.get(i);
         I18nBundle pack = new I18nBundle(i18nProps, localizedI18nPropsFileName);
         i18nBundlesByName.put(i18nProps.getPath(), pack);
      }

      for (int i = 0; i < localizedI18nProps.size(); i++)
      {
         I18nProps locI18nProps = (I18nProps) localizedI18nProps.get(i);
         String path = locI18nProps.getPath();
         String key = path.substring(0, path.lastIndexOf(File.separator)) + File.separator + i18nPropsFileName;

         I18nBundle pack = (I18nBundle) i18nBundlesByName.get(key);
         if(null != pack)
         {
            pack.setLocalizedProp(locI18nProps);
         }
      }

      I18nBundle[] bundles = (I18nBundle[]) i18nBundlesByName.values().toArray(new I18nBundle[0]);

      _bundlesTableModel.setBundles(bundles);
   }

   private void findI18nInArchive(String i18nPropsFileName, String localizedI18nPropsFileName, File file, ArrayList defaultI18nProps, ArrayList localizedI18nProps)
   {
      try
      {
         ZipFile zf = new ZipFile(file);

         for(Enumeration e=zf.entries(); e.hasMoreElements();)
         {
            ZipEntry entry = (ZipEntry) e.nextElement();

            if(entry.getName().endsWith(i18nPropsFileName))
            {
               defaultI18nProps.add(new I18nProps(file, entry.getName()));
            }
            else if(entry.getName().endsWith(localizedI18nPropsFileName))
            {
               localizedI18nProps.add(new I18nProps(file, entry.getName()));
            }

         }


      }
      catch (IOException e)
      {
         String msg = s_stringMgr.getString("I18n.failedToOpenZip", file.getAbsolutePath());
         // i18n[I18n.failedToOpenZip=Failed to open zip/jar {0}]
         _app.getMessageHandler().showMessage(msg);
      }

   }

   private void findI18nInDir(String i18nPropsFileName, String localizedI18nPropsFileName, File dir, ArrayList defaultI18nProps, ArrayList localizedI18nProps)
   {
      File[] files = dir.listFiles();

      for (int i = 0; i < files.length; i++)
      {
         if(files[i].isDirectory())
         {
            findI18nInDir(i18nPropsFileName, localizedI18nPropsFileName, files[i], defaultI18nProps, localizedI18nProps);
         }
         else if(files[i].getName().equals(i18nPropsFileName))
         {
            defaultI18nProps.add(new I18nProps(files[i]));
         }
         else if(files[i].getName().equals(localizedI18nPropsFileName))
         {
            localizedI18nProps.add(new I18nProps(files[i]));
         }
      }

   }


   public void applyChanges()
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public String getTitle()
   {
      return s_stringMgr.getString("I18n.title");
   }

   public String getHint()
   {
      return s_stringMgr.getString("I18n.hint");
   }

   public Component getPanelComponent()
   {
      return _panel;
   }

}
