package net.sourceforge.squirrel_sql.fw.gui.texteditdlg;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TextEditDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextEditDialog.class);

   JTextArea txtMessage;
   JButton btnMessageHistory;
   JButton btnOk;
   JButton btnCancel;

   public TextEditDialog(Frame parentFrame, String frameTitle, String description)
   {
      super(parentFrame, frameTitle, true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createDescriptionPanel(description), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      txtMessage = new JTextArea();
      getContentPane().add(new JScrollPane(txtMessage), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createOkCancelPanel(), gbc);
   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      btnOk = new JButton(s_stringMgr.getString("TextEditDialog.ok"));
      getRootPane().setDefaultButton(btnOk);
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("TextEditDialog.cancel"));
      ret.add(btnCancel, gbc);

      return ret;
   }

   private JPanel createDescriptionPanel(String description)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1 ,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      ret.add(new MultipleLineLabel(description), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0), 0,0);
      btnMessageHistory = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SQL_HISTORY));
      btnMessageHistory.setToolTipText(s_stringMgr.getString("TextEditDialog.text.history.tooltip"));
      GUIUtils.styleAsToolbarButton(btnMessageHistory);
      ret.add(btnMessageHistory, gbc);

      return ret;
   }
}
