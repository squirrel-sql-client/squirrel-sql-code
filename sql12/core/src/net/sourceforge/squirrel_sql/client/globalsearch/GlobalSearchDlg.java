package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.NoWrapJTextPane;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class GlobalSearchDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobalSearchDlg.class);

   final JTree treeSearchResultNavi = new JTree();
   final JButton btnClose = new JButton(s_stringMgr.getString("GlobalSearchDlg.close"));
   final JSplitPane splitPane;
   JComboBox cboTextToSearch = new JComboBox();
   JButton btnConfig;
   JButton btnSearch;
   JButton btnStop;
   NoWrapJTextPane txtPreview;

   public GlobalSearchDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("GlobalSearchDlg.title"));
      
      setLayout(new GridLayout(1,1));
      splitPane = new JSplitPane();
      add(splitPane);
      splitPane.setLeftComponent(createNavigationPanel());
      splitPane.setRightComponent(createPreviewPanel());

   }

   private JPanel createPreviewPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1, 0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5, 0, 5), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("GlobalSearchDlg.preview")), gbc);

      gbc = new GridBagConstraints(0,1,1,1, 1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5, 5, 5), 0,0);
      txtPreview = new NoWrapJTextPane();
      Font font = new Font(Font.MONOSPACED, Font.PLAIN, txtPreview.getFont().getSize());
      txtPreview.setFont(font);
      txtPreview.setEditable(false);
      ret.add(new JScrollPane(txtPreview), gbc);

      return ret;
   }

   private JPanel createNavigationPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,0), 0,0);
      ret.add(createNavigationTopPanel(), gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      ret.add(new JScrollPane(treeSearchResultNavi), gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      ret.add(btnClose, gbc);

      return ret;
   }

   private JPanel createNavigationTopPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("GlobalSearchDlg.text.fo.find")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0,0);
      ret.add(cboTextToSearch, gbc);


      gbc = new GridBagConstraints(1,1,1,0,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      btnSearch = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND));
      btnSearch.setToolTipText(s_stringMgr.getString("GlobalSearchDlg.search.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnSearch), gbc);

      gbc = new GridBagConstraints(2,1,1,0,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      btnConfig = new JButton(Main.getApplication().getResourcesFw().getIcon(LibraryResources.IImageNames.CONFIGURE));
      btnConfig.setToolTipText(s_stringMgr.getString("GlobalSearchDlg.configure"));
      ret.add(GUIUtils.styleAsToolbarButton(btnConfig), gbc);

      gbc = new GridBagConstraints(3,1,1,0,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      btnStop = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.STOP));
      btnStop.setToolTipText(s_stringMgr.getString("GlobalSearchDlg.stop.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnStop), gbc);

      return ret;
   }
}
