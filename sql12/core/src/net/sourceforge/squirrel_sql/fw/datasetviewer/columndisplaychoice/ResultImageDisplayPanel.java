package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.dnd.DropedFileExtractor;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BlobDescriptor;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.Types;
import java.util.Base64;
import java.util.List;

public class ResultImageDisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultImageDisplayPanel.class);
   private static final ILogger s_log = LoggerController.createLogger(ResultImageDisplayPanel.class);
   private final JScrollPane _scrImage = new JScrollPane();


   public ResultImageDisplayPanel(ColumnDisplayDefinition cdd,
                                  Object valueToDisplay,
                                  boolean tableEditable,
                                  int selRow,
                                  int selCol,
                                  DataSetViewerTable table)
   {
      setLayout(new BorderLayout(3,3));
      add(_scrImage, BorderLayout.CENTER);

      updateImageDisplay(cdd, valueToDisplay);

      if(tableEditable)
      {
         add(createUpdatePanel(selRow, selCol, table, cdd), BorderLayout.SOUTH);
      }
   }

   private void updateImageDisplay(ColumnDisplayDefinition cdd, Object valueToDisplay)
   {
      _scrImage.setViewportView(getDisplayLabel(cdd, valueToDisplay));
   }

   private JPanel createUpdatePanel(int selRow, int selCol, DataSetViewerTable table, ColumnDisplayDefinition cdd)
   {
      JPanel ret = new JPanel(new BorderLayout(0,3));

      JLabel lblDrop = new JLabel(s_stringMgr.getString("ResultImageDisplayPanel.drop.image.file.here"));
      lblDrop.setToolTipText(s_stringMgr.getString("ResultImageDisplayPanel.drop.image.file.here.tooltip"));

      DropTarget dt = new DropTarget(lblDrop, new DropTargetAdapter()
      {
         @Override
         public void drop(DropTargetDropEvent dtde)
         {
            onDrop(dtde, selRow, selCol, table, cdd);
         }
      });

      lblDrop.setDropTarget(dt);


      JPanel pnlBorder = new JPanel(new GridLayout(1,1));
      pnlBorder.add(lblDrop);
      pnlBorder.setBorder(BorderFactory.createEtchedBorder());
      ret.add(pnlBorder, BorderLayout.CENTER);

      JButton btnDel = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DELETE));
      btnDel.setToolTipText(s_stringMgr.getString("ResultImageDisplayPanel.delete.image.from.database.tooltip"));
      btnDel.addActionListener(e -> onDelete(selRow, selCol, table, cdd));
      ret.add(GUIUtils.styleAsToolbarButton(btnDel), BorderLayout.EAST);
      return ret;
   }

   private void onDelete(int selRow, int selCol, DataSetViewerTable table, ColumnDisplayDefinition cdd)
   {
      if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, s_stringMgr.getString("ResultImageDisplayPanel.delete.image")))
      {
         return;
      }

      if(null != table.getCellEditor())
      {
         table.getCellEditor().cancelCellEditing();
      }

      table.setValueAt(null, selRow, selCol);
      table.repaint();

      updateImageDisplay(cdd, null);
   }

   private void onDrop(DropTargetDropEvent dtde, int selRow, int selCol, DataSetViewerTable table, ColumnDisplayDefinition cdd)
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

         if(cdd.getSqlType() == Types.VARCHAR || cdd.getSqlType() == Types.LONGVARCHAR || cdd.getSqlType() == Types.LONGNVARCHAR || cdd.getSqlType() == Types.CLOB)
         {
            table.setValueAt(new String(Base64.getEncoder().encode(bytes)), selRow, selCol);
         }
         else
         {
            table.setValueAt(bytes, selRow, selCol);
         }

         table.repaint();
         updateImageDisplay(cdd, table.getValueAt(selRow, selCol));
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

         BufferedImage image;
         if(valueToDisplay instanceof String)
         {
            image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode((String) valueToDisplay)));
         }
         else if (valueToDisplay instanceof ClobDescriptor)
         {
            image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(((ClobDescriptor)valueToDisplay).getData())));
         }
         else if (valueToDisplay instanceof BlobDescriptor)
         {
            image = ImageIO.read(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(((BlobDescriptor)valueToDisplay).getData())));
         }
         else
         {
            image = ImageIO.read(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(valueToDisplay)));
         }

         JLabel lblImage = new JLabel();
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
