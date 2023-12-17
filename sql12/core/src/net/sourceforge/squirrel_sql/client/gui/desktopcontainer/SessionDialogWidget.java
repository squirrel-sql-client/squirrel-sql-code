package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;

public abstract class SessionDialogWidget extends DialogWidget implements ISessionWidget
{
   private ISession _session;

   public SessionDialogWidget(String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, ISession session)
   {
      super(title, resizeable, closeable, maximizeable, iconifiable, SessionUtils.getOwningFrame(session));
      _session = session;

      setupSheet();
      
   }

   public SessionDialogWidget(String title, boolean resizeable, ISession session)
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

   public boolean hasSQLPanelAPI()
   {
      return false;
   }

   private final void setupSheet()
   {
      _session.getApplication().getWindowManager().registerSessionSheet(this);
   }

}