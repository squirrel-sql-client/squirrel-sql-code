package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;
import java.awt.*;
import java.io.File;

public abstract class SessionTabWidget extends TabWidget implements ISessionWidget
{

   /**
    * Internationalized strings for this class.
    */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionTabWidget.class);


   private ISession _session;
   private String _sqlFilePath;
   private String _titleWithoutFile = "";

   public SessionTabWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, ISession session)
   {
      super(title, resizeable, closeable, maximizeable, iconifiable, session.getApplication());
      _session = session;
      _titleWithoutFile = title;
      setupSheet();
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


      if (null == _sqlFilePath)
      {
         super.setTitle(_titleWithoutFile);
      }
      else
      {
         String compositetitle =
            s_stringMgr.getString("SessionTabWidget.title",
               new String[]{_titleWithoutFile,
                  _sqlFilePath});
         super.setTitle(compositetitle);
      }
   }

   public void setSqlFile(File sqlFile)
   {
      if (sqlFile == null)
      {
         _sqlFilePath = null;
      }
      else
      {
         _sqlFilePath = sqlFile.getAbsolutePath();
      }
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
      String title = super.getTitle();

      if (unsavedEdits && !title.endsWith("*"))
      {
         super.setTitle(title + "*");
      }
      if (!unsavedEdits && title.endsWith("*"))
      {
         super.setTitle(title.substring(0, title.length() - 1));
      }
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
         _session.getApplication().getSessionManager().setActiveSession(_session);
      }
   }

}