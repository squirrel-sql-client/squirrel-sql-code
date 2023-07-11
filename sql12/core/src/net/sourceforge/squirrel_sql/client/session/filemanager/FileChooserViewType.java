package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.awt.*;

public enum FileChooserViewType
{
   DETAILS("viewTypeDetails", "Details"),
   LIST("viewTypeList", "List");

   private final static ILogger s_log = LoggerController.createLogger(FileChooserViewType.class);

   private static boolean logApplyViewTypeFailure = true;
   private static boolean logTryExtractChooserViewTypeFailure = true;

   private final String _fileChooserActionMapKey;
   private final String _fileViewName;

   FileChooserViewType(String fileChooserActionMapKey, String fileViewName)
   {
      _fileChooserActionMapKey = fileChooserActionMapKey;
      _fileViewName = fileViewName;
   }

   public static FileChooserViewType tryExtractChooserViewType(JFileChooser fileChooser)
   {
      FileChooserViewType ret = DETAILS;

      try
      {
         FileView fileView = fileChooser.getFileView();
         if (null != fileView)
         {
            if (StringUtils.containsIgnoreCase(fileView.getClass().getName(), "list"))
            {
               ret = LIST;
            }
         }
         else
         {
            for (Component component : fileChooser.getComponents())
            {
               if(component.getClass().getName().equals("sun.swing.FilePane"))
               {
                  Object viewTypeAsObject = component.getClass().getMethod("getViewType").invoke(component);
                  if(viewTypeAsObject instanceof Integer)
                  {
                     int viewType = (Integer) viewTypeAsObject;

                     if (viewType == 0) // == sun.swing.FilePane.VIEWTYPE_LIST
                     {
                        ret = LIST;
                     }
                     break;
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         if (logTryExtractChooserViewTypeFailure)
         {
            logTryExtractChooserViewTypeFailure = false;
            s_log.warn("Failed to determine FileChooserViewType. Will use " + ret.name() + " as fallback", e);
         }
      }

      return ret;
   }

   public void tryApplyViewType(JFileChooser fileChooser)
   {
      try
      {
         fileChooser.getActionMap().get(_fileChooserActionMapKey).actionPerformed(null);
      }
      catch (Exception e)
      {
         if (logApplyViewTypeFailure)
         {
            logApplyViewTypeFailure = false;
            s_log.warn("Failed to apply FileChooserViewType "  + name(), e);
         }
      }
   }
}
