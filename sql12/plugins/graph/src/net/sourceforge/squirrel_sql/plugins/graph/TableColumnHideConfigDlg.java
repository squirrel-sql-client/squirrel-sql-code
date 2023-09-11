package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class TableColumnHideConfigDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableColumnHideConfigDlg.class);

   JTextField txtFilterLeft = new JTextField();
   JTextField txtFilterRight = new JTextField();

   JList<ColumnInfo> lstLeft = new JList<>();

   TabButton btnRight = new TabButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.RIGHT_ARROW));
   TabButton btnLeft = new TabButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.LEFT_ARROW));

   JList<ColumnInfo> lstRight = new JList<>();

   JButton btnApplyHiding = new JButton(s_stringMgr.getString("TableColumnHideConfigDlg.apply.hiding"));


   public TableColumnHideConfigDlg(Window owningWindow, String tableName)
   {
      super(owningWindow);
      setModal(true);
      setTitle(s_stringMgr.getString("TableColumnHideConfigDlg.title", tableName));

      getContentPane().setLayout(new BorderLayout());

      getContentPane().add(createCentralPanel(),BorderLayout.CENTER);

      getContentPane().add(createBottomPanel(),BorderLayout.SOUTH);


      GUIUtils.enableCloseByEscape(this);

      setSize(new Dimension(600, 500));

      SwingUtilities.invokeLater(() -> GUIUtils.centerWithinParent(TableColumnHideConfigDlg.this));

   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,3,3,0), 0,0 );
      ret.add(btnApplyHiding, gbc);

      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,3,0), 0,0 );
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createCentralPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,3,3,0), 0,0 );
      ret.add(createLeftPane(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,0,0,3), 0,0 );
      ret.add(createMiddleButtonPane(), gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5,0,3,3), 0,0 );
      ret.add(createRightPanel(), gbc);

      return ret;
   }

   private JPanel createLeftPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      JLabel lblFilter = new JLabel(s_stringMgr.getString("TableColumnHideConfigDlg.filter.visible"));
      ret.add(lblFilter, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      ret.add(txtFilterLeft, gbc);

      JScrollPane scrLeft = new JScrollPane(lstLeft);
      gbc = new GridBagConstraints(0,2,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,0,0,0), 0,0 );
      ret.add(scrLeft, gbc);

      return GUIUtils.setPreferredWidth(ret, 0);
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
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      JLabel lblFilter = new JLabel(s_stringMgr.getString("TableColumnHideConfigDlg.filter.hidden"));
      ret.add(lblFilter, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0 );
      ret.add(txtFilterRight, gbc);

      JScrollPane scrLeft = new JScrollPane(lstRight);
      gbc = new GridBagConstraints(0,2,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,0,0,0), 0,0 );
      ret.add(scrLeft, gbc);

      return GUIUtils.setPreferredWidth(ret, 0);
   }

}
