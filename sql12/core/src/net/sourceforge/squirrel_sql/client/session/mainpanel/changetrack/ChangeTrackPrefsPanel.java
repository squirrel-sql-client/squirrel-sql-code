package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ChangeTrackPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackPrefsPanelController.class);

   private JCheckBox chkEnableChangeTracking;
   private JCheckBox chkGitCommitMsgManually;
   private JTextField txtGitCommitMsgDefault;

   private JButton btnDeltedForeground;
   private JToggleButton btnDeletedItalics;
   private JToggleButton btnDeletedBold;
   private JButton btnInsertBegin;
   private JButton btnInsertEnd;
   private JTextPane txtExampleChangeTrackBase;
   private JTextPane txtExampleEditorText;
   private JTextPane txtExamplePopup;


   public ChangeTrackPrefsPanel()
   {
      super(new GridBagLayout());
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ChangeTrackPrefsPanel.title")));

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      add(new MultipleLineLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.description")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      chkEnableChangeTracking = new JCheckBox(s_stringMgr.getString("ChangeTrackPrefsPanel.chk.enableChangeTracking"));
      add(chkEnableChangeTracking, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,0,5), 0,0);
      add(createGITPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,0,5), 0,0);
      add(createChangedPopupStylePanel(), gbc);

      // dist
      gbc = new GridBagConstraints(0,4,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      add(new JPanel(), gbc);
   }

   private JPanel createGITPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      chkGitCommitMsgManually = new JCheckBox(s_stringMgr.getString("ChangeTrackPrefsPanel.chk.edit.git.message.manually"));
      ret.add(chkGitCommitMsgManually, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,3,0,5), 0,0);
      txtGitCommitMsgDefault = new JTextField();
      ret.add(txtGitCommitMsgDefault, gbc);

      // dist
      gbc = new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ChangeTrackPrefsPanel.git")));
      return ret;
   }

   private JPanel createChangedPopupStylePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,3,5,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.change.popup.style")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,3,5,0), 0,0);
      ret.add(createPopUpDeleteDisplayConfig(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,3,5,0), 0,0);
      ret.add(createPopUpInsertDisplayConfigPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,3,0,0), 0,0);
      ret. add(createExamplePanel(), gbc);

      // dist
      gbc = new GridBagConstraints(0,4,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("ChangeTrackPrefsPanel.left.gutter.popup.style")));

      return ret;
   }

   private JPanel createPopUpDeleteDisplayConfig()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      SquirrelResources rsrc = Main.getApplication().getResources();

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.deletions.mark")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnDeletedBold = new JToggleButton(rsrc.getIcon(SquirrelResources.IImageNames.BOLD));
      btnDeletedBold.setToolTipText(s_stringMgr.getString("ChangeTrackPrefsPanel.delete.bold.style.toggle.tooltip"));
      ret. add(GUIUtils.styleAsToolbarButton(btnDeletedBold), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnDeletedItalics = new JToggleButton(rsrc.getIcon(SquirrelResources.IImageNames.ITALIC));
      btnDeletedItalics.setToolTipText(s_stringMgr.getString("ChangeTrackPrefsPanel.delete.italics.style.toggle.tooltip"));
      ret. add(GUIUtils.styleAsToolbarButton(btnDeletedItalics), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnDeltedForeground = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.PEN));
      btnDeltedForeground.setToolTipText(s_stringMgr.getString("ChangeTrackPrefsPanel.delete.foreground.color.tooltip"));
      ret. add(styleAsColorChooseButton(btnDeltedForeground), gbc);


      // dist
      gbc = new GridBagConstraints(0,2,4,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createPopUpInsertDisplayConfigPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      SquirrelResources rsrc = Main.getApplication().getResources();

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.insert.begin.position.background")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnInsertBegin = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.FILL));
      ret. add(styleAsColorChooseButton(btnInsertBegin), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0,0);
      ret. add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.insert.end.position.background")), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnInsertEnd = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.FILL));
      ret. add(styleAsColorChooseButton(btnInsertEnd), gbc);


      // dist
      gbc = new GridBagConstraints(0,2,4,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JButton styleAsColorChooseButton(JButton btn)
   {
//      GUIUtils.styleAsToolbarButton(btn);
//      btn.setBackground(Color.green);


      btn.setBackground(Color.green);
      btn.setBorder(BorderFactory.createEtchedBorder());
      return btn;
   }

   private JPanel createExamplePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.example.change.track.base")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,5), 0,0);
      txtExampleChangeTrackBase = new JTextPane();
      styleExampleTextPane(txtExampleChangeTrackBase);
      ret.add(txtExampleChangeTrackBase, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.example.editor.text")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtExampleEditorText = new JTextPane();
      styleExampleTextPane(txtExampleEditorText);
      ret.add(txtExampleEditorText, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("ChangeTrackPrefsPanel.example.popup")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      txtExamplePopup = new JTextPane();
      styleExampleTextPane(txtExamplePopup);
      ret.add(txtExamplePopup, gbc);


      // dist
      gbc = new GridBagConstraints(0,3,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);


      return ret;
   }

   private JTextPane styleExampleTextPane(JTextPane txtPane)
   {
      Font font = Main.getApplication().getSquirrelPreferences().getSessionProperties().getFontInfo().createFont();

      txtPane.setFont(font);
      txtPane.setEditable(false);
      txtPane.setBorder(BorderFactory.createEtchedBorder());
      GUIUtils.setPreferredHeight(txtPane, 2 * txtPane.getFontMetrics(font).getHeight() + 5);

      txtPane.setText("Bla\nBla");

      return txtPane;
   }

}
