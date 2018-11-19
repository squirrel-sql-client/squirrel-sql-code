package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandlerUtil;
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

      _titleFileHandler = new TitleFilePathHandler(() -> setTitle(_titleWithoutFile));
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

      TitleFilePathHandlerUtil.setTitle(_titleWithoutFile, _titleFileHandler, this, super::setTitle);
   }



   public void setSqlFile(File sqlFile)
   {
      _titleFileHandler.setSqlFile(sqlFile);
      setTitle(_titleWithoutFile);
   }

   public void setUnsavedEdits(boolean unsavedEdits)
   {
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