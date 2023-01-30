package net.sourceforge.squirrel_sql.client.session.filemanager;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class FileChooserWithMoveOptionDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FileChooserWithMoveOptionDialog.class);

   private JFileChooser _fileChooser;
   private JCheckBox _chkMoveFile;
   private File _selectedFile;


   public FileChooserWithMoveOptionDialog(JFileChooser fileChooser, Frame parent)
   {
      super(parent,true);

      setTitle(s_stringMgr.getString("FileChooserWithMoveOptionDialog.title"));

      _fileChooser = fileChooser;

      buildUI();


      _fileChooser.addActionListener(e -> onFileChooserButtonClicked(e.getActionCommand()));

      GUIUtils.enableCloseByEscape(this);
   }

   private void onFileChooserButtonClicked(String actionCommand)
   {
      if(JFileChooser.APPROVE_SELECTION.equals(actionCommand))
      {
         _selectedFile = _fileChooser.getSelectedFile();
      }

      close();
   }

   public void setSelectedFile(File fileToSaveAs)
   {
      _fileChooser.setSelectedFile(fileToSaveAs);
   }

   public void setCurrentDirectory(File file)
   {
      _fileChooser.setCurrentDirectory(file);
   }

   public File getSelectedFile()
   {
      return _fileChooser.getSelectedFile();
   }


   public int showSaveDialog()
   {
      if(null == _fileChooser.getSelectedFile())
      {
         // Save as was used without a previous file.
         _chkMoveFile.setEnabled(false);
      }

      _fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
      pack();
      GUIUtils.centerWithinParent(this);
      setVisible(true);
      // Waits for dialog close

      if(null != _selectedFile)
      {
         return JFileChooser.APPROVE_OPTION;
      }

      return JFileChooser.CANCEL_OPTION;
   }


   public boolean isMoveFile()
   {
      return _chkMoveFile.isSelected();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   private void buildUI()
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(_fileChooser, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,10,10,0), 0,0);
      _chkMoveFile = new JCheckBox(s_stringMgr.getString("FileChooserWithMoveOptionDialog.move.file"));
      getContentPane().add(_chkMoveFile, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,10,5), 0,0);
      getContentPane().add(new SmallToolTipInfoButton(s_stringMgr.getString("FileChooserWithMoveOptionDialog.move.file.info.html")).getButton(), gbc);
   }

}
