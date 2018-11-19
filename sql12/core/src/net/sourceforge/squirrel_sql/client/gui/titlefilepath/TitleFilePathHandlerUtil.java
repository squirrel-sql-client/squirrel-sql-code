package net.sourceforge.squirrel_sql.client.gui.titlefilepath;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.ButtonTabComponent;


/**
 * The logic for handling the file name in the tab-title is very similar
 * for a SessionTabWidget (i.e. SessionInternalFrame, SQLInternalFrame)
 * and a {@link net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab}.
 *
 * It seemed cumbersome to generalize the logic to be available for both cases.
 * That is why at least the logic of both is gathered here.
 */
public class TitleFilePathHandlerUtil
{
   /**
    *
    * @param sessionTabWidgetSetTitleInSuperCaller Needed to prevent stack overflow when calling sessionTabWidget.setTitle().
    *                                              The call must be delegated to the sessionTabWidget's super class.
    */
   public static void setTitle(String titleWithoutFile, TitleFilePathHandler titleFileHandler, SessionTabWidget sessionTabWidget, SessionTabWidgetSetTitleInSuperCaller sessionTabWidgetSetTitleInSuperCaller)
   {
      if(null == titleFileHandler) // happens when method is called in boostrap
      {
         sessionTabWidgetSetTitleInSuperCaller.setTitle(titleWithoutFile);
         return;
      }


      if (titleFileHandler.hasFile())
      {
         String compositetitle = titleWithoutFile + titleFileHandler.getSqlFile();

         sessionTabWidgetSetTitleInSuperCaller.setTitle(compositetitle);
         sessionTabWidget.addSmallTabButton(titleFileHandler.getFileMenuSmallButton());
      }
      else
      {
         sessionTabWidgetSetTitleInSuperCaller.setTitle(titleWithoutFile);
         sessionTabWidget.removeSmallTabButton(titleFileHandler.getFileMenuSmallButton());
      }
   }

   public static void setTitle(String titleWithoutFile, TitleFilePathHandler titleFileHandler, ButtonTabComponent tabComponent)
   {
      if(null == titleFileHandler) // happens when method is called in boostrap
      {
         tabComponent.setTitle(titleWithoutFile);
         return;
      }


      if (titleFileHandler.hasFile())
      {
         String compositetitle = titleWithoutFile + titleFileHandler.getSqlFile();

         tabComponent.setTitle(compositetitle);
         tabComponent.addSmallTabButton(titleFileHandler.getFileMenuSmallButton());
      }
      else
      {
         tabComponent.setTitle(titleWithoutFile);
         tabComponent.removeSmallTabButton(titleFileHandler.getFileMenuSmallButton());
      }
   }
}
