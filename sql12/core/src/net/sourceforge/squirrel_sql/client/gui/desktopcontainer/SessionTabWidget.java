package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;

public abstract class SessionTabWidget extends TabWidget implements ISessionWidget
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionTabWidget.class);


   private ISession _session;
   private String _titleWithoutFile = "";
   private TitleFilePathHandler _titleFileHandler;

   public SessionTabWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, ISession session)
   {
      super(title, resizeable, closeable, maximizeable, iconifiable, session.getApplication());
      _session = session;
      _titleWithoutFile = title;
      setupSheet();

      TitleFilePathHandlerListener titleFilePathHandlerListener = new TitleFilePathHandlerListener()
      {
         @Override
         public void refreshFileDisplay()
         {
            setTitle(_titleWithoutFile);
         }
      };

      _titleFileHandler = new TitleFilePathHandler(_session.getApplication().getResources(), titleFilePathHandlerListener);
   }

   public SessionTabWidget(String title, boolean resizeable, ISession session)
   {
      this(title, resizeable, true, false, false, session);
   }

   public ISession getSession()
   {
      return _session;
   }

   public void closeFrame(boolean withEvents)
   {
      if (!_session.isfinishedLoading())
      {
         return;
      }
      if (withEvents)
      {
         fireWidgetClosing();
      }
      dispose();

      if (withEvents)
      {
         fireWidgetClosed();
      }
   }


   private final void setupSheet()
   {
      _session.getApplication().getWindowManager().registerSessionSheet(this);
      addWidgetListener(new SheetActivationListener());
   }


   @Override
   public void setTitle(String title)
   {
      _titleWithoutFile = title;

      if(null == _titleFileHandler) // happens when method is called in boostrap
      {
         super.setTitle(_titleWithoutFile);
         return;
      }


      if (_titleFileHandler.hasFile())
      {
         String compositetitle = _titleWithoutFile + _titleFileHandler.getSqlFile();

         super.setTitle(compositetitle);
         super.addSmallTabButton(_titleFileHandler.getFileMenuSmallButton());
      }
      else
      {
         super.setTitle(_titleWithoutFile);
         super.removeSmallTabButton(_titleFileHandler.getFileMenuSmallButton());
      }
   }

   public void setSqlFile(File sqlFile)
   {
      _titleFileHandler.setSqlFile(sqlFile);
      setTitle(_titleWithoutFile);
   }

   /**
    * Toggles the "*" at the end of the filename based on the value of
    * unsavedEdits.  Just to provide the user with a visual hint that they may
    * need to save their changes.
    *
    * @param unsavedEdits
    */
   public void setUnsavedEdits(boolean unsavedEdits)
   {
//      String title = super.getTitle();
//
//      if (unsavedEdits && !title.endsWith("*"))
//      {
//         super.setTitle(title + "*");
//      }
//      if (!unsavedEdits && title.endsWith("*"))
//      {
//         super.setTitle(title.substring(0, title.length() - 1));
//      }
      _titleFileHandler.setUnsavedEdits(unsavedEdits);
   }

   /**
    * Sets the session behind this sheet to the active session when the
    * frame is activated
    */
   private class SheetActivationListener extends WidgetAdapter
   {
      public void widgetActivated(WidgetEvent e)
      {
         _session.setActiveSessionWindow((ISessionWidget) e.getWidget());
         _session.getApplication().getSessionManager().setActiveSession(_session, false);
      }
   }

}