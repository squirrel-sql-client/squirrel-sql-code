package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.ConfigurableMeldPanel;

import javax.swing.*;
import java.awt.*;

public class CompareToClipboardDlg extends JDialog
{
   public CompareToClipboardDlg(Window owner, ConfigurableMeldPanel configurableMeldPanel, String title)
   {
      super(owner, title, ModalityType.APPLICATION_MODAL);

      getContentPane().setLayout(new GridLayout(1,1));
      getContentPane().add(configurableMeldPanel);
   }
}
