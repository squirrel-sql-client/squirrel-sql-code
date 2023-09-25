package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.ExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphToFilesCtrlr
{
   GraphToFilesDlg _dlg;

   private static final String PREF_KEY_LAST_IMAGE_DIR = "SquirrelSQL.graph.lastImageDir";


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(GraphToFilesCtrlr.class);
   private BufferedImage[] _images;
   private Window _parent;

   public GraphToFilesCtrlr(BufferedImage[] images, Window parent)
   {
      _images = images;
      _parent = parent;
      _dlg = new GraphToFilesDlg(parent, images);

      _dlg.btnClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onClosing();
            _dlg.setVisible(false);
            _dlg.dispose();
         }
      });


      _dlg.btnSaveToFile.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSaveToFile();
         }
      });

      _dlg.addWindowListener(new WindowAdapter()
      {

         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });
   }

   private void onClosing()
   {
      for (int i = 0; i < _images.length; i++)
      {
         _images[i].flush();
      }
   }

   private void onSaveToFile()
   {
      try
      {
         String lastDir = Props.getString(PREF_KEY_LAST_IMAGE_DIR, System.getProperty("user.home"));
         JFileChooser fc = new JFileChooser(lastDir);
         // i18n[graphToFile.fileChooserTitle=Save image file(s)]
         fc.setDialogTitle(s_stringMgr.getString("graphToFile.fileChooserTitle"));


         if(1 < _images.length)
         {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         }
         else
         {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            ExtensionFilter ef = new ExtensionFilter();
            // i18n[graphToFile.ImageFileSpec=JPG image format]
            ef.addExtension(s_stringMgr.getString("graphToFile.ImageFileSpec.png"),"png");
            ef.addExtension(s_stringMgr.getString("graphToFile.ImageFileSpec"),"jpg");
            fc.setFileFilter(ef);
         }

         if (fc.showSaveDialog(_parent) == JFileChooser.APPROVE_OPTION)
         {
            File selectedFile = fc.getSelectedFile();
            if (null != selectedFile)
            {
               if (1 == _images.length)
               {
                  if (StringUtils.endsWithIgnoreCase(selectedFile.getPath(), ".png"))
                  {
                     ImageIO.write(_images[0], "png", selectedFile);
                  }
                  else if (StringUtils.endsWithIgnoreCase(selectedFile.getPath(), ".jpg"))
                  {
                     ImageIO.write(_images[0], "jpg", selectedFile);
                  }
                  else
                  {
                     selectedFile = new File(selectedFile.getPath() + ".png");
                     ImageIO.write(_images[0], "png", selectedFile);
                  }

                  Props.putString(PREF_KEY_LAST_IMAGE_DIR, selectedFile.getParent());

               }
               else
               {
                  selectedFile.mkdirs();

                  for (int i = 0; i < _images.length; i++)
                  {
                     File f = new File(selectedFile, "Page_" + (i+1) + ".png");
                     ImageIO.write(_images[i], "png", f);
                  }
                  Props.putString(PREF_KEY_LAST_IMAGE_DIR, selectedFile.getPath());
               }
            }

         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
