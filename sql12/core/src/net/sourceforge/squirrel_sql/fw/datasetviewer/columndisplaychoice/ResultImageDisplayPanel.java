package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.dnd.DropedFileExtractor;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BlobDescriptor;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlobProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClobProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
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

public class ResultImageDisplayPanel extends JPanel implements CellDisplayPanelContent<ResultImageDisplayPanel>
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultImageDisplayPanel.class);
   private static final ILogger s_log = LoggerController.createLogger(ResultImageDisplayPanel.class);
   private final JScrollPane _scrImage = new JScrollPane();
   private final ImageContainerSizeProvider _imageContainerSizeProvider;
   private JLabel _lblImage = new JLabel();
   private BufferedImage _image;


   public ResultImageDisplayPanel(ColumnDisplayDefinition cdd,
                                  Object valueToDisplay,
                                  boolean tableEditable,
                                  int selRow,
                                  int selCol,
                                  ImageContainerSizeProvider imageContainerSizeProvider,
                                  DataSetViewerTable table)
   {
      _imageContainerSizeProvider = imageContainerSizeProvider;
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
      _scrImage.setViewportView(getDisplayComponent(cdd, valueToDisplay));
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

   private JComponent getDisplayComponent(ColumnDisplayDefinition cdd, Object valueToDisplay)
   {
      try
      {
         if(null == valueToDisplay)
         {
            return new JLabel(StringUtilities.NULL_AS_STRING);
         }

         if(valueToDisplay instanceof String)
         {
            _image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode((String) valueToDisplay)));
         }
         else if (valueToDisplay instanceof BlobDescriptor)
         {
            byte[] data = ((BlobDescriptor) valueToDisplay).getData();

            if(null == data)
            {
               if(false == isReadCompleteBlobs())
               {
                  String msg = s_stringMgr.getString("ResultImageDisplayPanel.null.blob.not.read", StringUtilities.NULL_AS_STRING);
                  return new MultipleLineLabel(msg);
               }
               else
               {
                  return new JLabel(StringUtilities.NULL_AS_STRING);
               }
            }
            _image = ImageIO.read(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(data)));
         }
         else if (valueToDisplay instanceof ClobDescriptor)
         {
            String data = ((ClobDescriptor) valueToDisplay).getData();
            if(null == data)
            {
               if(false == isReadCompleteClobs())
               {
                  String msg = s_stringMgr.getString("ResultImageDisplayPanel.null.clob.not.read", StringUtilities.NULL_AS_STRING);
                  return new MultipleLineLabel(msg);
               }
               else
               {
                  return new JLabel(StringUtilities.NULL_AS_STRING);
               }
            }

            _image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
         }
         else
         {
            _image = ImageIO.read(new ByteArrayInputStream(Utilities.toPrimitiveByteArray(valueToDisplay)));
         }

         if(null == _image)
         {
            String msg = s_stringMgr.getString("ResultImageDisplayPanel.could.create.image", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName());

            if(cdd.getSqlType() == Types.BLOB && false == isReadCompleteClobs())
            {
               msg = s_stringMgr.getString("ResultImageDisplayPanel.could.create.image.blob.not.read", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName());
            }
            else if(cdd.getSqlType() == Types.CLOB && false == isReadCompleteBlobs())
            {
               msg = s_stringMgr.getString("ResultImageDisplayPanel.could.create.image.clob.not.read", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName());
            }

            s_log.error(msg);
            Main.getApplication().getMessageHandler().showErrorMessage(msg);
            return new MultipleLineLabel(msg);
         }

         _lblImage.setHorizontalAlignment(SwingConstants.CENTER);
         _lblImage.setIcon(new ImageIcon(_image));
         return _lblImage;
      }
      catch(Exception e)
      {
         String msg = s_stringMgr.getString("ResultImageDisplayPanel.image.display.error", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName(), e);

         if(cdd.getSqlType() == Types.BLOB && false == isReadCompleteClobs())
         {
            msg = s_stringMgr.getString("ResultImageDisplayPanel.image.display.error.blob.not.read", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName(), e);
         }
         else if(cdd.getSqlType() == Types.CLOB && false == isReadCompleteBlobs())
         {
            msg = s_stringMgr.getString("ResultImageDisplayPanel.image.display.error.clob.not.read", ColumnDisplayUtil.getColumnName(cdd), cdd.getSqlTypeName(), e);
         }


         s_log.error(msg, e);
         Main.getApplication().getMessageHandler().showErrorMessage(msg);
         return new MultipleLineLabel(msg);
      }
   }

   private static boolean isReadCompleteClobs()
   {
      DataTypeBlobProperties props = new DataTypeBlobProperties().loadProperties();
      return props.isReadCompleteBlobs() && false == props.isReadBlobsNever();
   }

   private static boolean isReadCompleteBlobs()
   {
      DataTypeClobProperties props = new DataTypeClobProperties().loadProperties();
      return props.isReadCompleteClobs() && false == props.isReadClobsNever();
   }

   public void scaleImageToPanelSize()
   {
      if(null == _image || 0 == _image.getWidth() || 0 == _image.getWidth())
      {
         return;
      }

      Dimension parentSize = _imageContainerSizeProvider.getImageContainerSize();
      parentSize.width -= 5;
      parentSize.height -= 5;
      //if(parentSize.width > 40 && parentSize.height > 40 && (_image.getWidth() > parentSize.getWidth() || _image.getWidth() > parentSize.getWidth()))
      if(parentSize.width > 40 && parentSize.height > 40 )
      {
         int scaleToWidth;
         int scaleToHeight;

         if( parentSize.getWidth() / ((double)_image.getWidth()) > parentSize.getHeight() / ((double)_image.getHeight()) )
         {
            scaleToWidth = (int) (parentSize.getHeight() / ((double)_image.getHeight()) * ((double)_image.getWidth()) + 0.5);
            scaleToHeight = parentSize.height;
         }
         else
         {
            scaleToWidth = parentSize.width;
            scaleToHeight = (int) (parentSize.getWidth() / ((double)_image.getWidth()) * ((double)_image.getHeight()) + 0.5);
         }

         _lblImage.setIcon(new ImageIcon(GUIUtils.scaleImage(_image, scaleToWidth, scaleToHeight)));
      }
   }
}
