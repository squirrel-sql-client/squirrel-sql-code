package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockHandleEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandleEvent;

import javax.swing.event.InternalFrameEvent;
import java.awt.event.WindowEvent;

public class WidgetEvent
{
   private IWidget _widget;
   private InternalFrameEvent _ife;
   private WindowEvent _we;
   private DockHandleEvent _dhe;
   private TabHandleEvent _the;

   public WidgetEvent(InternalFrameEvent ife, IWidget widget)
   {
      _ife = ife;
      _widget = widget;
   }

   public WidgetEvent(WindowEvent we, IWidget widget)
   {
      _we = we;
      _widget = widget;
   }

   public WidgetEvent(DockHandleEvent dhe, IWidget widget)
   {
      _dhe = dhe;
      _widget = widget;
   }

   public WidgetEvent(TabHandleEvent the, TabWidget widget)
   {
      _the = the;
      _widget = widget;
   }

   public IWidget getWidget()
   {
      return _widget;
   }
}
