package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandlerUtil;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ModificationAwareSessionTitle;
import net.sourceforge.squirrel_sql.client.session.ModificationAwareSessionTitleChangeListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;

public abstract class SessionTabWidget extends TabWidget implements ISessionWidget
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionTabWidget.class);
   private final ModificationAwareSessionTitleChangeListener _titleChangeListener;


   private ISession _session;
   private ModificationAwareSessionTitle _titleWithoutFile;
   private TitleFilePathHandler _titleFileHandler;
   private String _titlePostFix = "";

   public SessionTabWidget(ModificationAwareSessionTitle title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, ISession session)
   {
      super(title.getTitle(), resizeable, closeable, maximizeable, iconifiable, session.getApplication());
      _session = session;
      _titleWithoutFile = title;
      setupSheet();

      _titleChangeListener = (oldTitle, newTitle) -> setTitle(_titleWithoutFile.getTitle());
      _titleWithoutFile.addListener(_titleChangeListener);
      _titleFileHandler = new TitleFilePathHandler(() -> setTitle(_titleWithoutFile.getTitle()));
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

      _titleWithoutFile.removeListener(_titleChangeListener);
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
      _titleWithoutFile.setTitle(title);
      updateTitle();
   }

   @Override
   public void setTitlePostFix(String titlePostFix)
   {
      if (StringUtilities.isEmpty(titlePostFix, true))
      {
         _titlePostFix = "";
      }
      else
      {
         _titlePostFix = titlePostFix;
      }

      updateTitle();
   }

   private void updateTitle()
   {
      TitleFilePathHandlerUtil.setTitle(_titleWithoutFile.getTitle() + _titlePostFix, _titleFileHandler, this, super::setTitle);
   }


   public void setMainSqlFile(File sqlFile)
   {
      _titleFileHandler.setSqlFile(sqlFile);
   }

   public void displayUnsavedEditsInTabComponent(boolean unsavedEdits)
   {
      _titleFileHandler.setUnsavedEdits(unsavedEdits);
   }

   protected TitleFilePathHandler getTitleFileHandler()
   {
      return _titleFileHandler;
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

   public String getTitleWithoutFile()
   {
      return _titleWithoutFile.getTitle();
   }
}