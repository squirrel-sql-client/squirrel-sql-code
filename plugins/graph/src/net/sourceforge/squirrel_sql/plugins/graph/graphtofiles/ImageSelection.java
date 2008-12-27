package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;


/**
 * Unused at the moment.
 * Might help one day to copy images to clipboard.
 * See usage example commented at the bottom of this file.
 */
public class ImageSelection extends TransferHandler
   implements Transferable
{
   private static final long serialVersionUID = 1L;

	private static final DataFlavor flavors[] =
      {
         DataFlavor.imageFlavor,
         new DataFlavor("image/jpeg","JPEG image"),
         new DataFlavor("image/gif","GIF Image"),
         new DataFlavor("mage/x-pict", "X Pict")
      };

   private Image image;

   public int getSourceActions(JComponent c)
   {
      return TransferHandler.COPY;
   }

   public boolean canImport(JComponent comp, DataFlavor flavor[])
   {
      if (!(comp instanceof JLabel) || (comp instanceof AbstractButton))
      {
         return false;
      }
      for (int i = 0, n = flavor.length; i < n; i++)
      {
         if (flavor[i].equals(flavors[0]))
         {
            return true;
         }
      }
      return false;
   }

   public Transferable createTransferable(JComponent comp)
   {
      // Clear
      image = null;
      Icon icon = null;

      if (comp instanceof JLabel)
      {
         JLabel label = (JLabel) comp;
         icon = label.getIcon();
      }
      else if (comp instanceof AbstractButton)
      {
         AbstractButton button = (AbstractButton) comp;
         icon = button.getIcon();
      }
      if (icon instanceof ImageIcon)
      {
         image = ((ImageIcon) icon).getImage();
         return this;
      }
      return null;
   }

   public boolean importData(JComponent comp, Transferable t)
   {
      ImageIcon icon = null;
      try
      {
         if (t.isDataFlavorSupported(flavors[0]))
         {
            image = (Image) t.getTransferData(flavors[0]);
            icon = new ImageIcon(image);
         }
         if (comp instanceof JLabel)
         {
            JLabel label = (JLabel) comp;
            label.setIcon(icon);
            return true;
         }
         else if (comp instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) comp;
            button.setIcon(icon);
            return true;
         }
      }
      catch (UnsupportedFlavorException ignored)
      {
      }
      catch (IOException ignored)
      {
      }
      return false;
   }

   // Transferable
   public Object getTransferData(DataFlavor flavor)
   {
      if (isDataFlavorSupported(flavor))
      {
         return image;
      }
      return null;
   }

   public DataFlavor[] getTransferDataFlavors()
   {
      return flavors;
   }

   public boolean isDataFlavorSupported(DataFlavor
      flavor)
   {
      return flavor.equals(flavors[0]);
   }
}


//
// USAGE EXAMPLE FOR IMAGESELECTION CLASS
//
//import net.sourceforge.squirrel_sql.plugins.graph.graphtofiles.ImageSelection;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.datatransfer.*;
//import javax.swing.*;
//
//public class ImageCopy
//{
//
//   public static void main(String args[])
//   {
//
//      JFrame frame = new JFrame("Copy Image");
//      frame.setDefaultCloseOperation
//         (JFrame.EXIT_ON_CLOSE);
//
//      Container contentPane = frame.getContentPane();
//
//      Toolkit kit = Toolkit.getDefaultToolkit();
//      final Clipboard clipboard =
//         kit.getSystemClipboard();
//
//      // Your example pic here
//      Icon icon = new ImageIcon("/tmp/anypic.jpg");
//      final JLabel label = new JLabel(icon);
//      label.setTransferHandler(new ImageSelection());
//
//      JScrollPane pane = new JScrollPane(label);
//      contentPane.add(pane, BorderLayout.CENTER);
//
//      JButton copy = new JButton("Label Copy");
//      copy.addActionListener(new ActionListener()
//      {
//         public void actionPerformed(ActionEvent e)
//         {
//            TransferHandler handler =
//               label.getTransferHandler();
//            handler.exportToClipboard(label, clipboard,
//               TransferHandler.COPY);
//         }
//      });
//
//      JButton clear = new JButton("Label Clear");
//      clear.addActionListener(new ActionListener()
//      {
//         public void actionPerformed(ActionEvent
//            actionEvent)
//         {
//            label.setIcon(null);
//         }
//      });
//
//      JButton paste = new JButton("Label Paste");
//      paste.addActionListener(new ActionListener()
//      {
//         public void actionPerformed(ActionEvent
//            actionEvent)
//         {
//            Transferable clipData =
//               clipboard.getContents(clipboard);
//            if (clipData != null)
//            {
//               if (clipData.isDataFlavorSupported
//                  (DataFlavor.imageFlavor))
//               {
//                  TransferHandler handler =
//                     label.getTransferHandler();
//                  handler.importData(label, clipData);
//               }
//            }
//         }
//      });
//
//      JPanel p = new JPanel();
//      p.add(copy);
//      p.add(clear);
//      p.add(paste);
//      contentPane.add(p, BorderLayout.NORTH);
//
//      JPanel pasteP = new JPanel();
//      JButton pasteB = new JButton("Paste");
//
//      pasteB.setTransferHandler(new ImageSelection());
//
//      pasteB.addActionListener
//         (TransferHandler.getPasteAction());
//
//      pasteP.add(pasteB);
//      contentPane.add(pasteB, BorderLayout.SOUTH);
//
//      frame.setSize(400, 400);
//      frame.show();
//   }
//}