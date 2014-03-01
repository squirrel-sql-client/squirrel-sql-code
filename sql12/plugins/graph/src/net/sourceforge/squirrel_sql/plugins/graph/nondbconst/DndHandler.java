package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
            onMousePressed(e, comp, session);
         }
      });
   }

   private void onMousePressed(MouseEvent e, JComponent comp, ISession session)
   {
      if (1 == e.getClickCount() && false == e.isPopupTrigger() && 0 != (e.getModifiers() & MouseEvent.CTRL_MASK) )
      {
         _lastExportedMouseEvent = e;
         if(comp instanceof DndColumn)
         {
            Point lp = ((DndColumn)comp).getLocationInColumnTextArea();
            _lastExportedMouseEvent.translatePoint(lp.x, lp.y);
         }


         comp.getTransferHandler().exportAsDrag(comp, e, DnDConstants.ACTION_COPY);
      }
      else if(false == _dndMessageShown && 1 == e.getClickCount() && false == e.isPopupTrigger())
      {
         session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("graph.DndHandler.dndMessage"));
         _dndMessageShown = true;
      }
   }

   private TransferHandler createTransferHandler()
   {
      return new TransferHandler("dndEvent")
      {
         public boolean importData(TransferSupport support)
         {
            _dropPoint = support.getDropLocation().getDropPoint();

            Component comp = support.getComponent();
            if (comp instanceof DndColumn)
            {
               Point lp = ((DndColumn) comp).getLocationInColumnTextArea();
               _dropPoint.translate(lp.x, lp.y);
            }
            return super.importData(support);
         }

         @Override // Needed because of some plugin class loader problem or something like that.
         public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
         {
            if(comp instanceof DndColumn)
            {
               return true;
            }
            else
            {
               return super.canImport(comp, transferFlavors);
            }
         }

         @Override // Needed because of some plugin class loader problem or something like that.
         protected Transferable createTransferable(JComponent c)
         {
            if(c instanceof DndColumn)
            {
               DndColumn dndCol = (DndColumn) c;
               DndEvent dndEvent = dndCol.getDndEvent();
               if (null == dndEvent)
               {
                  return super.createTransferable(c);
               }
               else
               {
                  return new DataHandler(dndEvent, DataFlavor.javaJVMLocalObjectMimeType);
               }
            }
            else
            {
               return super.createTransferable(c);
            }
         }

         @Override // To bring text copy and paste back to work on Win-Platforms
         public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException
         {
            if (comp instanceof JTextComponent)
            {
               JTextComponent txtComp = (JTextComponent) comp;
               String selText = txtComp.getSelectedText();
               if (null == selText || 0 == selText.length())
               {
                  super.exportToClipboard(comp, clip, action);
               }
               else
               {
                  StringSelection data = new StringSelection(selText);
                  clip.setContents(data, data);
               }
            }
            else
            {
               super.exportToClipboard(comp, clip, action);
            }
         }

      };
   }

   public DndEvent getDndEvent()
   {
      if (null == _lastExportedMouseEvent)
      {
         // happens when text copy is done within a table frames text area
         return null;
      }

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
