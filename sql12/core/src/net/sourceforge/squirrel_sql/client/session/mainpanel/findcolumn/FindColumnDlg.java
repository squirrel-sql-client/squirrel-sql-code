package net.sourceforge.squirrel_sql.client.session.mainpanel.findcolumn;

import net.sourceforge.squirrel_sql.client.session.mainpanel.TabButton;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class FindColumnDlg extends JDialog
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(FindColumnDlg.class);


   JTextField txtFilter = new JTextField();
   TabButton btnSortAsc = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.SORT_ASC));
   TabButton btnSortDesc = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.SORT_DESC));
   JList lstLeft = new JList(new String[]{"id", "name"});

   TabButton btnRight = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.RIGHT_ARROW));
   TabButton btnLeft = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.LEFT_ARROW));

   JList lstRight = new JList(new String[]{"id", "name"});
   JButton btnToTableBegin = new JButton(s_stringMgr.getString("FindColumnDlg.moveto.table.begin"));

   TabButton btnUp = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.TABLE_ASCENDING));
   TabButton btnDown = new TabButton(new LibraryResources().getIcon(LibraryResources.IImageNames.TABLE_DESCENDING));


   public FindColumnDlg(Frame owner)
   {
      super(owner, true);


      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0 );
      getContentPane().add(createLeftPane(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      getContentPane().add(createMiddleButtonPane(), gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(3,0,0,0), 0,0 );
      getContentPane().add(createRightPanel(), gbc);


      GUIUtils.enableCloseByEscape(this);

      setSize(new Dimension(600, 500));

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run()
         {
            GUIUtils.centerWithinParent(FindColumnDlg.this);
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


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,0,0,2), 0,0 );
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

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0 );
      ret.add(btnSortAsc, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,3,0,0), 0,0 );
      ret.add(btnSortDesc, gbc);

      return ret;
   }

   private JPanel createMiddleButtonPane()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,3,3,3), 0,0 );
      ret.add(btnRight, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(0,3,0,3), 0,0 );
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

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,3,3,3), 0,0 );
      ret.add(btnUp, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,3,0,3), 0,0 );
      ret.add(btnDown, gbc);

      return ret;
   }

}
