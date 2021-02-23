package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
   JCheckBox chkObjectName = new JCheckBox(s_stringMgr.getString("FindColumnsDlg.find.in.objectName"));
   JCheckBox chkColumnName = new JCheckBox(s_stringMgr.getString("FindColumnsDlg.find.in.columnName"));
   JCheckBox chkColumnTypeName = new JCheckBox(s_stringMgr.getString("FindColumnsDlg.find.in.columnTypeName"));
   JCheckBox chkRemarks = new JCheckBox(s_stringMgr.getString("FindColumnsDlg.find.in.remarks"));

   JButton btnFind;
   JButton btnStopSearching;
   JProgressBar progressBar = new JProgressBar();

   DataSetViewerTablePanel tblSearchResult = new DataSetViewerTablePanel();

   JTextField txtStatus;
   JButton btnClose;

   public FindColumnsDlg(Window parent, String dialogTitle)
   {
      super(parent, dialogTitle);

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
      btnClose = new JButton(s_stringMgr.getString("FindColumnsDlg.close"));
      setSearching(false);
      ret.add(btnClose, gbc);

      GUIUtils.setPreferredHeight(txtStatus, btnClose.getPreferredSize().height);

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

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0 );
      btnStopSearching = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.STOP));
      btnStopSearching.setToolTipText(s_stringMgr.getString("FindColumnsDlg.stopSearching"));
      ret.add(btnStopSearching, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(0,5,0,0), 0,0 );
      ret.add(progressBar, gbc);
      GUIUtils.setPreferredWidth(progressBar, btnStopSearching.getPreferredSize().width / 2);
      GUIUtils.setMinimumWidth(progressBar, btnStopSearching.getPreferredSize().width / 2);


      gbc = new GridBagConstraints(0,3,4,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0 );
      ret.add(createConfigPanel(), gbc);

      return ret;

   }

   private JPanel createConfigPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(3,0,0,0), 0,0 );
      ret.add(new MultipleLineLabel(s_stringMgr.getString("FindColumnsDlg.place.holder.info")), gbc);


      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0,0 );
      ret.add(new JLabel(s_stringMgr.getString("FindColumnsDlg.find.in")), gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      ret.add(chkObjectName, gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      ret.add(chkColumnName, gbc);

      gbc = new GridBagConstraints(2,2,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      ret.add(new JLabel(s_stringMgr.getString("FindColumnsDlg.qualifier.info")), gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0 );
      ret.add(chkColumnTypeName, gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0 );
      ret.add(chkRemarks, gbc);

      return ret;
   }

   void setSearching(boolean b)
   {
      btnStopSearching.setEnabled(b);
      progressBar.setIndeterminate(b);
      progressBar.setString("");
   }

   public boolean isSearching()
   {
      return btnStopSearching.isEnabled();
   }
}
