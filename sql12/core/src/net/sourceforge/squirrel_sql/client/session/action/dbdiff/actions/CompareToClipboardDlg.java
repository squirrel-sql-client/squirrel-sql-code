package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;

public class CompareToClipboardDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CompareToClipboardDlg.class);

   public CompareToClipboardDlg(Frame owner, JMeldPanel meldPanel)
   {
      super(owner, s_stringMgr.getString("CompareToClipboardDlg.clipboard.vs.editor"), true);

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(meldPanel);
   }
}
