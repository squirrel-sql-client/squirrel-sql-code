package net.sourceforge.squirrel_sql.client.session.menuattic;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class AtticToFromDlg extends JDialog
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(AtticToFromDlg.class);
   JList<AtticToFromItem> lstOutAttic = new JList<>();
   JList<AtticToFromItem> lstInAttic = new JList<>();
   JButton btnMoveInAttic = new JButton(new LibraryResources().getIcon(LibraryResources.IImageNames.RIGHT_ARROW));
   JButton btnMoveOutAttic = new JButton(new LibraryResources().getIcon(LibraryResources.IImageNames.LEFT_ARROW));
   JButton btnOk = new JButton(s_stringMgr.getString("AtticToFromDlg.ok"));
   JButton btnCancel = new JButton(s_stringMgr.getString("AtticToFromDlg.cancel"));

   public AtticToFromDlg(MenuOrigin menuOrigin)
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("AtticToFromDlg.move.to.from.attic"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("AtticToFromDlg.allows.to.move.less.used")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,5,0,5), 0,0);
      getContentPane().add(createListsPanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createBottomPanel(menuOrigin), gbc);

   }

   private JPanel createBottomPanel(MenuOrigin menuOrigin)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(btnCancel, gbc);


      gbc = new GridBagConstraints(2,0,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      final MultipleLineLabel lblSessionRestart = new MultipleLineLabel(s_stringMgr.getString("AtticToFromDlg.may.take.effect.on.Session.restart"));
      lblSessionRestart.setFont(lblSessionRestart.getFont().deriveFont(Font.BOLD));
      lblSessionRestart.setForeground(Color.red);
      ret.add(lblSessionRestart, gbc);

      if(menuOrigin == MenuOrigin.SQL_EDITOR)
      {
         gbc = new GridBagConstraints(0,1,3,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0,0);
         ret.add(new MultipleLineLabel(s_stringMgr.getString("AtticToFromDlg.tools.popup.note")), gbc);
      }

      return ret;
   }

   private JPanel createListsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0,0);
      ret.add(GUIUtils.setPreferredWidth(new JLabel(s_stringMgr.getString("AtticToFromDlg.outside.attic")), 0), gbc);


      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      ret.add(GUIUtils.setPreferredWidth(new JLabel(s_stringMgr.getString("AtticToFromDlg.in.attic")),0), gbc);



      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5), 0,0);
      ret.add(new JScrollPane(lstOutAttic), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(createLeftRightButtonPanel(), gbc);

      gbc = new GridBagConstraints(2,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0,0);
      ret.add(new JScrollPane(lstInAttic), gbc);

      return ret;
   }

   private JPanel createLeftRightButtonPanel()
   {
      JPanel ret = new JPanel(new GridLayout(2,1, 5,5));
      ret.add(btnMoveInAttic);
      ret.add(btnMoveOutAttic);

      return ret;
   }
}
