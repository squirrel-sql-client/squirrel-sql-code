package net.sourceforge.squirrel_sql.plugins.dataimport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ImportProgressDialog extends JDialog
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(ImportProgressCtrl.class);

   JTextField txtNumberOfRowsImported = new JTextField();
   JButton btnCancel = new JButton(stringMgr.getString("ImportProgressDialog.cancel"));

   public ImportProgressDialog(String tableName)
   {
      super(Main.getApplication().getMainFrame(), stringMgr.getString("ImportProgressDialog.titel", tableName), false);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
      getContentPane().add(new JLabel(stringMgr.getString("ImportProgressDialog.label")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
      getContentPane().add(txtNumberOfRowsImported, gbc);
      txtNumberOfRowsImported.setEditable(false);
      GUIUtils.forceWidth(txtNumberOfRowsImported, 120);


      gbc = new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
      getContentPane().add(btnCancel, gbc);


      setSize(new Dimension(320, 125));


      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

   }
}
