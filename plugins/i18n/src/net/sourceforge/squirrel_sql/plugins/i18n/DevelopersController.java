package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import java.io.File;

public class DevelopersController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DevelopersController.class);


   private DevelopersPanel _panel;
   private IApplication _app;
   private static final String PREF_KEY_SOURCE_DIR = "SquirrelSQL.i18n.sourceDir";

   public DevelopersController(DevelopersPanel pnlDevelopers)
   {
      _panel = pnlDevelopers;

      _panel.btnChooseSourceDir.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onChooseSourceDir();
         }

      });

      _panel.btnAppendI18nInCode.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAppendI18nInCode();
         }
      });


      String sourceDir = Preferences.userRoot().get(PREF_KEY_SOURCE_DIR, null);
      _panel.txtSourceDir.setText(sourceDir);
   }


   private void onChooseSourceDir()
   {
      JFileChooser chooser = new JFileChooser(System.getProperties().getProperty("user.home"));
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.showOpenDialog(_app.getMainFrame());

      if(null != chooser.getSelectedFile())
      {
         _panel.txtSourceDir.setText(chooser.getSelectedFile().getPath());
      }
   }

   private void onAppendI18nInCode()
   {
      File sourceDir = getSourceDir();

      if(null == sourceDir)
      {
         return;
      }

      appendProps(sourceDir);
   }

   private void appendProps(File sourceDir)
   {
      //To change body of created methods use File | Settings | File Templates.
   }


   private File getSourceDir()
   {
      String buf = _panel.txtSourceDir.getText();
      if(null == buf || 0 == buf.trim().length())
      {
            String msg = s_stringMgr.getString("I18n.NoSourceDir");
            // i18n[I18n.NoSourceDir=Please choose a source directory.]
            JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
         return null;

      }


      File sourceDir = new File(buf);
      if(false == sourceDir.isDirectory())
      {
            String msg = s_stringMgr.getString("I18n.SourceDirIsNotADirectory", sourceDir.getPath());
            // i18n[I18n.SourceDirIsNotADirectory=Source directory {0} is not a directory.]
            JOptionPane.showMessageDialog(_app.getMainFrame(), msg);
      }

      if(false == sourceDir.exists())
      {
         String msg = s_stringMgr.getString("I18n.SourceDirDoesNotExist", sourceDir.getPath());
         // i18n[I18n.SourceDirDoesNotExist=Source directory {0} does not exist.]
         return null;
      }

      return sourceDir;
   }


   public void initialize(IApplication app)
   {
      _app = app;
   }

   public void uninitialize()
   {
      Preferences.userRoot().put(PREF_KEY_SOURCE_DIR, _panel.txtSourceDir.getText());
   }



}
