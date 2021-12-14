package net.sourceforge.squirrel_sql.client.gui.pastefromhistory;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class PasteFromHistoryDialog extends JDialog
{
   private static final StringManager stringManager = StringManagerFactory.getStringManager(PasteFromHistoryDialog.class);
   JList lstHistoryItems = new JList();
   JTextArea txtHistoryDetail = new JTextArea();
   JButton btnOk;
   JButton btnCancel;


   public PasteFromHistoryDialog()
   {
      super(Main.getApplication().getMainFrame());

      setTitle(stringManager.getString("paste.from.history.title"));


      final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

      splitPane.setTopComponent(new JScrollPane(lstHistoryItems));
      splitPane.setBottomComponent(new JScrollPane(txtHistoryDetail));

      SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5d));

      getContentPane().setLayout(new BorderLayout());

      getContentPane().add(splitPane, BorderLayout.CENTER);

      getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

      GUIUtils.enableCloseByEscape(this);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,0,0, 0), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(1,0,1,1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,0,5, 5), 0,0);
      btnOk = new JButton(stringManager.getString("paste.from.history.ok"));
      ret.add(btnOk, gbc);

      getRootPane().setDefaultButton(btnOk);

      gbc = new GridBagConstraints(2,0,1,1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,0,5, 5), 0,0);
      btnCancel = new JButton(stringManager.getString("paste.from.history.cancel"));
      ret.add(btnCancel, gbc);

      return ret;
   }

}
