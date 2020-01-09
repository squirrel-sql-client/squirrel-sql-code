package net.sourceforge.squirrel_sql.plugins.dataimport.action;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.Resources;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class ImportFileChooserDialog extends JDialog
{
   private static final String PREFS_KEY_LAST_IMPORT_DIRECTORY = "squirrelsql_dataimport_last_import_directory";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportFileChooserDialog.class);


   private JFileChooser _fileChooser;
   private JButton _btnClipBoard;
   private File _importFile;
   private boolean _importFromClipBoard;


   public ImportFileChooserDialog(IResources resources, ITableInfo table)
   {
      super(Main.getApplication().getMainFrame(),true);

      if (null == table)
      {
         setTitle(s_stringMgr.getString("ImportTableDataCommand.file.dialog.title.without.table"));
      }
      else
      {
         setTitle(s_stringMgr.getString("ImportTableDataCommand.file.dialog.title.with.table", table));
      }

      buildUI(resources);

      _fileChooser.addActionListener(e -> onFileChooserButtonClicked(e.getActionCommand()));

      _btnClipBoard.addActionListener(e -> onImportFromClipBoard());

      GUIUtils.enableCloseByEscape(this);
      pack();
      GUIUtils.centerWithinParent(this);

      setVisible(true);
   }

   public File getImportFile()
   {
      return _importFile;
   }

   public boolean isImportFromClipBoard()
   {
      return _importFromClipBoard;
   }

   private void onImportFromClipBoard()
   {
      _importFromClipBoard = true;
      close();
   }

   private void onFileChooserButtonClicked(String actionCommand)
   {
      if(JFileChooser.APPROVE_SELECTION.equals(actionCommand))
      {
         _importFile = _fileChooser.getSelectedFile();

         if (null != _importFile.getParent())
         {
            Props.putString(PREFS_KEY_LAST_IMPORT_DIRECTORY, _importFile.getParent());
         }
      }

      close();
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }

   private void buildUI(IResources resources)
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      _fileChooser = new JFileChooser(Props.getString(PREFS_KEY_LAST_IMPORT_DIRECTORY, System.getProperty("user.home")));
      getContentPane().add(_fileChooser, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _btnClipBoard = new JButton(s_stringMgr.getString("ImportFileChooserDialog.import.from.clipboard"), resources.getIcon(Resources.IImageNames.CLIPBOARD));
      getContentPane().add(_btnClipBoard, gbc);
   }
}
