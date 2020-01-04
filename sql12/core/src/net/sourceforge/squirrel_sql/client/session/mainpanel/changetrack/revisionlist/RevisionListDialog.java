package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RevisionListDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RevisionListDialog.class);

   JList<RevisionWrapper> lstRevisions;
   JTextArea txtPreview = new JTextArea();

   JSplitPane splitTreePreview;


   public RevisionListDialog(JComponent parentComp, String fileName, String filePathRelativeToRepoRoot, String repoRootPath)
   {
      super(GUIUtils.getOwningFrame(parentComp), s_stringMgr.getString("RevisionListDialog.title", fileName), ModalityType.MODELESS);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("RevisionListDialog.description", filePathRelativeToRepoRoot, repoRootPath)), gbc);

      splitTreePreview = new JSplitPane();

      lstRevisions = new JList<>();
      lstRevisions.setCellRenderer(new RevisionListCellRenderer());
      splitTreePreview.setLeftComponent(new JScrollPane(lstRevisions));

      txtPreview.setEditable(false);
      splitTreePreview.setRightComponent(new JScrollPane(txtPreview));

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(splitTreePreview, gbc);


   }
}
