package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FactoryProviderController
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(FactoryProviderController.class);

   private final static ILogger s_log =
      LoggerController.createLogger(FactoryProviderController.class);


   private HibernatePlugin _plugin;
   private String _className;
   private FactoryProviderDialog _dlg;


   public FactoryProviderController(HibernatePlugin plugin, String className)
   {
      _plugin = plugin;

      _dlg = new FactoryProviderDialog(_plugin.getApplication().getMainFrame());

      _dlg.txtClassName.setText(className);

      _dlg.btnWriteExampleFactorProvider.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onWriteExampleFactorProvider();
         }
      });

      _dlg.btnOk.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _dlg.btnCancel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCancel();
         }
      });

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(true);

   }

   private void onCancel()
   {
      close();
   }

   private void onOK()
   {
      if(null != _dlg.txtClassName.getText() && 0 < _dlg.txtClassName.getText().trim().length())

      _className = _dlg.txtClassName.getText();
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onWriteExampleFactorProvider()
   {
      String dirPath = Preferences.userRoot().get(HibernateConfigController.PERF_KEY_LAST_DIR, System.getProperty("user.home"));

      JFileChooser fc = new JFileChooser(dirPath);

      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      fc.setFileFilter(new FileFilter()
      {
         public boolean accept(File f)
         {
            if (f.isDirectory())
            {
               return true;
            }
            return false;
         }

         public String getDescription()
         {
            return null;
         }
      });

      if (JFileChooser.APPROVE_OPTION != fc.showSaveDialog(_plugin.getApplication().getMainFrame()))
      {
         return;
      }

      File dir = fc.getSelectedFile();

      File pack = new File(dir.getPath(), "pack");

      File javaFile = new File(pack, "ExampleSessionFactorImplProvider.java");

      try
      {
         pack.mkdirs();

         FileWriter fw = new FileWriter(javaFile);
         PrintWriter pw = new PrintWriter(fw);

         pw.println(CODE);

         pw.flush();
         fw.flush();
         pw.close();
         fw.close();

         // i18n[FactoryProviderController.fileCreated=File {0} has been successfully created.]
         _plugin.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("FactoryProviderController.fileCreated", javaFile));
      }
      catch (Exception e)
      {
         // i18n[FactoryProviderController.fileCreateFailed=File {0} could not be created: {1}]
         String msg = s_stringMgr.getString("FactoryProviderController.fileCreateFailed", new Object[]{javaFile, e});
         _plugin.getApplication().getMessageHandler().showErrorMessage(msg);
         s_log.error(msg, e);
      }

      Preferences.userRoot().put(HibernateConfigController.PERF_KEY_LAST_DIR, dir.getPath());
   }

   public String getClassName()
   {
      return _className;
   }


   private static final String CODE =
      "package pack;\n" +
         "\n" +
         "import org.hibernate.impl.SessionFactoryImpl;\n" +
         "import org.hibernate.cfg.Configuration;\n" +
         "import org.hibernate.SessionFactory;\n" +
         "\n" +
         "/**\n" +
         " * Example for a class that can be used by\n" +
         " * SQuirreLSQL's Hibernate Plugin to provide\n" +
         " * a Hibernate SessionFactoryImpl object.\n" +
         " *\n" +
         " */\n" +
         "public class ExampleSessionFactorImplProvider\n" +
         "{\n" +
         "   /**\n" +
         "    * The SessionFactorImpl provider class must have a method\n" +
         "    * with exactly this signature and name.\n" +
         "    */\n" +
         "   public SessionFactoryImpl getSessionFactoryImpl()\n" +
         "   {\n" +
         "      // Normally you can cast this object to org.hibernate.impl.SessionFactoryImpl as shown below.  \n" +
         "      SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();\n" +
         "\n" +
         "      return (SessionFactoryImpl) sessionFactory;\n" +
         "   }\n" +
         "\n" +
         "}";
}
