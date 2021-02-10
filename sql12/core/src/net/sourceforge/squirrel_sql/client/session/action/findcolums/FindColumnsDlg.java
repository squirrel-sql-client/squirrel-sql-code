package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

public class FindColumnsDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindColumnsDlg.class);

   JTextField txtFilter;
   JButton btnFind;

   DataSetViewerTablePanel tblSearchResult = new DataSetViewerTablePanel();

   JTextField txtStatus;
   JButton btnCancelClose;

   private String _closeString = s_stringMgr.getString("FindColumnsDlg.close");
   private String _cancelString = s_stringMgr.getString("FindColumnsDlg.cancel");


   public FindColumnsDlg(Window parent)
   {
      super(parent, s_stringMgr.getString("FindColumnsDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0 );
      getContentPane().add(createTopPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0 );
      tblSearchResult.init(null, null);
      getContentPane().add(new JScrollPane(tblSearchResult.getComponent()), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,2,2,2), 0,0 );
      getContentPane().add(createBottomPanel(), gbc);

   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      txtStatus = new JTextField();
      txtStatus.setEditable(false);
      ret.add(txtStatus, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0 );
      btnCancelClose = new JButton();
      switchBtnCancelCloseToClose();
      ret.add(btnCancelClose, gbc);

      GUIUtils.setPreferredHeight(txtStatus, btnCancelClose.getPreferredSize().height);

      return ret;
   }


   private JPanel createTopPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      txtFilter = new JTextField();
      ret.add(txtFilter, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0 );
      btnFind = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND));
      ret.add(btnFind, gbc);

      GUIUtils.setPreferredHeight(txtFilter, btnFind.getPreferredSize().height);

      return ret;

   }

   void switchBtnCancelCloseToClose()
   {
      btnCancelClose.setText(_closeString);
   }

   void switchBtnCancelCloseToCancel()
   {
      btnCancelClose.setText(_cancelString);
   }

   public boolean isCancelCloseOnClose()
   {
      return _closeString.equals(btnCancelClose.getText());
   }

   public boolean isCancelCloseOnCancel()
   {
      return _cancelString.equals(btnCancelClose.getText());
   }
}
