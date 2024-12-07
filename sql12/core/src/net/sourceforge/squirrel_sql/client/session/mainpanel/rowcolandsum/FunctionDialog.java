package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FunctionDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FunctionDialog.class);

   final JCheckBox chkMean;
   final JCheckBox chkDeviation;
   final DataSetViewerTablePanel functionValuesTablePanel;

   public FunctionDialog(Frame owningFrame, String title)
   {
      super(owningFrame, title);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      chkMean = new JCheckBox(s_stringMgr.getString("FunctionDialog.show.mean"));
      getContentPane().add(chkMean, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      chkDeviation = new JCheckBox(s_stringMgr.getString("FunctionDialog.show.deviation"));
      getContentPane().add(chkDeviation, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,0,0), 0,0);
      getContentPane().add(createDefinitionsLinkLabel(), gbc);

      gbc = new GridBagConstraints(0,1,GridBagConstraints.REMAINDER, 1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0,0);
      functionValuesTablePanel = new DataSetViewerTablePanel();
      functionValuesTablePanel.init(null, null);
      getContentPane().add(new JScrollPane(functionValuesTablePanel.getComponent()), gbc);

   }

   private JLabel createDefinitionsLinkLabel()
   {
      JLabel lblDefinitionsLink = new JLabel(s_stringMgr.getString("FunctionDialog.definitions.link.html"));
      lblDefinitionsLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblDefinitionsLink.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            showDefinitionsDialog();
         }
      });
      return lblDefinitionsLink;
   }

   private void showDefinitionsDialog()
   {
      ImageIcon picDefinitions =
            Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PIC_MEAN_DEVIATION_DEFINITION);

      JDialog defDlg = new JDialog(this, s_stringMgr.getString("FunctionDialog.definitions.title"));
      defDlg.setSize(570, 260);
      defDlg.getContentPane().setLayout(new GridLayout(1,1));
      JLabel lblDefinitions = new JLabel(picDefinitions);
      lblDefinitions.setBackground(Color.WHITE);
      defDlg.add(lblDefinitions);
      GUIUtils.centerWithinParent(defDlg);
      GUIUtils.enableCloseByEscape(defDlg);
      defDlg.setVisible(true);
   }
}
