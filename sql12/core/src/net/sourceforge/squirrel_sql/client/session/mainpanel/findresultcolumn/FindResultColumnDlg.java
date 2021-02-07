package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FindResultColumnDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindResultColumnDlg.class);


   JTextField txtFilter = new JTextField();
   JCheckBox chkFindInTableNames;
   TabButton btnSortAsc = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.SORT_ASC));
   TabButton btnSortDesc = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.SORT_DESC));

   JList<FindColumnColWrapper> lstLeft = new JList<>();

   TabButton btnRight = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.RIGHT_ARROW));
   TabButton btnLeft = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.LEFT_ARROW));

   JList<FindColumnColWrapper> lstRight = new JList<>();
   JButton btnToTableBegin = new JButton(s_stringMgr.getString("FindColumnDlg.moveto.table.begin"));

   TabButton btnUp = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.TABLE_ASCENDING));
   TabButton btnDown = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.TABLE_DESCENDING));



   public FindResultColumnDlg(Frame owner)
   {
      super(owner, true);


      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,3,3,0), 0,0 );
      getContentPane().add(createLeftPane(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,0,0,3), 0,0 );
      getContentPane().add(createMiddleButtonPane(), gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5,0,3,3), 0,0 );
      getContentPane().add(createRightPanel(), gbc);


      GUIUtils.enableCloseByEscape(this);

      setSize(new Dimension(600, 500));

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run()
         {
            GUIUtils.centerWithinParent(FindResultColumnDlg.this);
         }
      });

      setTitle(s_stringMgr.getString("FindColumnDlg.title"));
   }

   private JPanel createLeftPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      JLabel lblFilter = new JLabel(s_stringMgr.getString("FindColumnDlg.filter"));
      ret.add(lblFilter, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      ret.add(txtFilter, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      ret.add(createSortPanel(), gbc);


      JScrollPane scrLeft = new JScrollPane(lstLeft);
      gbc = new GridBagConstraints(0,3,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,0,0,0), 0,0 );
      ret.add(scrLeft, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      JLabel lblGoto = new JLabel(s_stringMgr.getString("FindColumnDlg.goto"));
      ret.add(lblGoto, gbc);

      ret.setPreferredSize(new Dimension(0, ret.getPreferredSize().height));

      return ret;
   }

   private JPanel createSortPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      chkFindInTableNames = new JCheckBox(s_stringMgr.getString("FindColumnDlg.ckeckbox.find.in.table.names"));
      chkFindInTableNames.setToolTipText(s_stringMgr.getString("FindColumnDlg.ckeckbox.find.in.table.names.tooltip"));
      ret.add(chkFindInTableNames, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10,0,0,0), 0,0 );
      ret.add(btnSortAsc, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10,3,0,0), 0,0 );
      ret.add(btnSortDesc, gbc);

      return ret;
   }

   private JPanel createMiddleButtonPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,3,3,0), 0,0 );
      ret.add(btnRight, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0 );
      ret.add(btnLeft, gbc);

      return ret;
   }


   private JPanel createRightPanel()
   {
      JPanel lstPanel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0 );
      lstPanel.add(new JScrollPane(lstRight), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3,0,0,0), 0,0 );
      lstPanel.add(btnToTableBegin, gbc);

      lstPanel.setPreferredSize(new Dimension(0, lstPanel.getPreferredSize().height));


      JPanel ret = new JPanel(new BorderLayout());

      ret.add(lstPanel, BorderLayout.CENTER);

      ret.add(createUpDownButtonPanel(), BorderLayout.EAST);


      return ret;
   }

   private JPanel createUpDownButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,3,3,0), 0,0 );
      ret.add(btnUp, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0 );
      ret.add(btnDown, gbc);

      return ret;
   }

   public void showDialog()
   {
      GUIUtils.forceFocus(txtFilter);
      super.setVisible(true);
   }
}
