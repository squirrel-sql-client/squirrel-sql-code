package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.dnd.DropedFileExtractor;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class ResultImageDisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultImageDisplayPanel.class);
   private static final ILogger s_log = LoggerController.createLogger(ResultImageDisplayPanel.class);


   public ResultImageDisplayPanel(ColumnDisplayDefinition cdd,
                                  Object valueToDisplay,
                                  boolean tableEditable,
                                  int selRow,
                                  int selCol,
                                  DataSetViewerTable table)
   {
      setLayout(new BorderLayout(3,0));
      add(getDisplayLabel(cdd, valueToDisplay), BorderLayout.CENTER);

      if(tableEditable)
      {
         add(createUpdatePanel(selRow, selCol, table), BorderLayout.SOUTH);
      }
   }
   private JPanel createUpdatePanel(int selRow, int selCol, DataSetViewerTable table)
   {
      JPanel ret = new JPanel(new BorderLayout(0,3));

      JLabel lblDrop = new JLabel(s_stringMgr.getString("ResultImageDisplayPanel.drop.image.file.here"));

      DropTarget dt = new DropTarget(lblDrop, new DropTargetAdapter()
      {
         @Override
         public void drop(DropTargetDropEvent dtde)
         {
            onDrop(dtde, selRow, selCol, table);
         }
      });

      lblDrop.setDropTarget(dt);


      JPanel pnlBorder = new JPanel(new GridLayout(1,1));
      pnlBorder.add(lblDrop);
      pnlBorder.setBorder(BorderFactory.createEtchedBorder());
      ret.add(pnlBorder, BorderLayout.CENTER);

      JButton btnDel = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DELETE));
      ret.add(GUIUtils.styleAsToolbarButton(btnDel), BorderLayout.EAST);
      return ret;
   }

   private void onDrop(DropTargetDropEvent dtde, int selRow, int selCol, DataSetViewerTable table)
   {
      File fileBuf = null;
      try
      {
         List<File> files = DropedFileExtractor.getFiles(dtde);

         if(files.isEmpty())
         {
            Main.getApplication().getMessageHandler().showWarningMessage("ResultImageDisplayPanel.no.file.dropped");
            return;
         }

         if(null != table.getCellEditor())
         {
            table.getCellEditor().cancelCellEditing();
         }

         fileBuf = files.get(0);
         byte[] bytes = Files.readAllBytes(fileBuf.toPath());
         table.setValueAt(bytes, selRow, selCol);
         table.repaint();
      }
      catch(Exception e)
      {
         String msg = s_stringMgr.getString("ResultImageDisplayPanel.failed.to.read.file", fileBuf, e);
         s_log.error(msg, e);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
      }
   }

   private JLabel getDisplayLabel(ColumnDisplayDefinition cdd, Object valueToDisplay)
   {
      try
      {
         if(null == valueToDisplay)
         {
            return new JLabel(StringUtilities.NULL_AS_STRING);
         }

         //Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(valueToDisplay);

         //Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(valueToDisplay)));

         //ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(valueToDisplay)));
         //Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);

         //ImageInputStream imageInputStream = ImageIO.createImageInputStream(new File("/home/gerd/Bilder/Screenshot_20230227_195224-1.png"));
         //Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
         //
         //if(false == imageReaders.hasNext())
         //{
         //   String msg = s_stringMgr.getString("ResultImageDisplayPanel.dont.know.how.to.read.image", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName());
         //   s_log.error(msg);
         //   Main.getApplication().getMessageHandler().showErrorMessage(msg);
         //   return new JLabel(msg);
         //}

         BufferedImage image = ImageIO.read(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(valueToDisplay)));

         JLabel lblImage = new JLabel();
         //lblImage.setIcon(new ImageIcon(imageReaders.next().read(0)));
         lblImage.setIcon(new ImageIcon(image));
         return lblImage;
      }
      catch(Exception e)
      {
         String msg = s_stringMgr.getString("ResultImageDisplayPanel.image.display.error", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName(), e);
         s_log.error(msg, e);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         return new JLabel(msg);
      }
   }
}
