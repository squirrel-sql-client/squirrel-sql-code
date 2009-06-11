package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.dnd.DnDConstants;
import java.awt.*;

public class DndHandler
{
   /** Logger for this class. */
   private static final ILogger s_log = LoggerController.createLogger(DndHandler.class);

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DndHandler.class);


   private DndCallback _dndCallback;
   private MouseEvent _lastExportedMouseEvent;
   private Point _dropPoint;

   private static boolean _dndMessageShown = false;

   /**
    *
    * @param comp must support dndEvent Property. Means must have getDndEvent() / setDndEvent().
    * @param session
    */
   public DndHandler(DndCallback dndCallback, final JComponent comp, final ISession session)
   {
      _dndCallback = dndCallback;
      comp.setTransferHandler(createTransferHandler());

      comp.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            if (1 == e.getClickCount() && false == e.isPopupTrigger() && 0 != (e.getModifiers() & MouseEvent.CTRL_MASK) )
            {
               comp.getTransferHandler().exportAsDrag(comp, e, DnDConstants.ACTION_COPY);
               _lastExportedMouseEvent = e;
            }
            else if(false == _dndMessageShown && 1 == e.getClickCount() && false == e.isPopupTrigger())
            {
               session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("graph.DndHandler.dndMessage"));   
               _dndMessageShown = true;
            }
         }
      });
   }

   private TransferHandler createTransferHandler()
   {
      return new TransferHandler("dndEvent")
      {
         public boolean importData(TransferSupport support)
         {
            _dropPoint = support.getDropLocation().getDropPoint();
            return super.importData(support);
         }
      };
   }

   public DndEvent getDndEvent()
   {
      return _dndCallback.createDndEvent(_lastExportedMouseEvent);
   }

   public void setDndEvent(DndEvent dndEvent)
   {
      try
      {
         _dndCallback.dndImportDone(dndEvent, _dropPoint);
      }
      catch (Throwable t)
      {
         s_log.error("Error on drag and drop constraint", t);
      }
   }
}
