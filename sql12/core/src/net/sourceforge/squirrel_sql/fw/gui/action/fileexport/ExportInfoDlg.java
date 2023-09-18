package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class ExportInfoDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportInfoDlg.class);
   private final JLabel _lblInfo;

   public ExportInfoDlg(Window parent)
   {
      super(parent);
      setModal(true);

      setTitle(s_stringMgr.getString("ExportInfoDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0);
      _lblInfo = new JLabel(s_stringMgr.getString("ExportInfoDlg.info"));
      getContentPane().add(_lblInfo, gbc);
      //getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("ExportInfoDlg.info")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      getContentPane().add(createButtonsPanel(), gbc);

      //GUIUtils.initLocation(this, 400,300);
      pack();
      GUIUtils.centerWithinParent(this);
      GUIUtils.enableCloseByEscape(this);

      setVisible(true);
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,5,5));

      JButton bntOk = new JButton(s_stringMgr.getString("ExportInfoDlg.ok"));
      bntOk.addActionListener(e -> close());
      ret.add(bntOk);

      JButton bntCopy = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.COPY));
      bntCopy.setToolTipText(s_stringMgr.getString("ExportInfoDlg.copy.as.text.to.clip"));
      bntCopy.addActionListener(e -> copyAsTextToClip());
      ret.add(bntCopy);

      return ret;
   }

   private void copyAsTextToClip()
   {
      String plainText = _lblInfo.getText();
      plainText = StringUtils.replace(plainText, "<html>", "");
      plainText = StringUtils.replace(plainText, "<br>", "\n");
      plainText = StringUtils.replace(plainText, "<pre>", "");
      plainText = StringUtils.replace(plainText, "</pre>", "\n");
      plainText = StringUtils.replace(plainText, "<ul>", "");
      plainText = StringUtils.replace(plainText, "</ul>", "");
      plainText = StringUtils.replace(plainText, "<li>", "- ");
      plainText = StringUtils.replace(plainText, "</li>", "\n");

      ClipboardUtil.copyToClip(plainText);
   }

   private void close()
   {
      setVisible(false);
      dispose();
   }
}
