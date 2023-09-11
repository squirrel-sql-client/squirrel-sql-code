package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

public class ReferencesFrameStarter
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReferencesFrameStarter.class);


   public static void showReferences(RootTable rootTable, ISession session, JFrame owningFrame)
   {


      References references = ShowReferencesUtil.getReferences(rootTable.getGlobalDbTable(), session);

      // May still be good to open the dialog to allow editing data.
      //if(references.isEmpty())
      //{
      //   JOptionPane.showMessageDialog(owningFrame, s_stringMgr.getString("ReferencesFrameStarter.noForeignKeyReferences", rootTable.getGlobalDbTable().getQualifiedName()));
      //   return;
      //}

      new ShowReferencesCtrl(session, owningFrame, rootTable, references);
   }
}
