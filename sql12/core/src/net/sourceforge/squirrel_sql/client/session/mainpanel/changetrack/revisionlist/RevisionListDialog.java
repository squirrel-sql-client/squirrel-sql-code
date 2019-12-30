package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RevisionListDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RevisionListDialog.class);

   final JList<Object> lstRevisions;

   public RevisionListDialog(JComponent parentComp, String fileName)
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("RevisionListDialog.title", fileName), DEFAULT_MODALITY_TYPE);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      lstRevisions = new JList<>();
      getContentPane().add(new JScrollPane(lstRevisions), gbc);
   }
}
