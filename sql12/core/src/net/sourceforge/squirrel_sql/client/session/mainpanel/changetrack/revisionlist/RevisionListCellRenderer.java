package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class RevisionListCellRenderer implements ListCellRenderer
{

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == value)
      {
         return null;
      }

      RevisionWrapper revisionWrapper = (RevisionWrapper) value;

      String displayString =
                  revisionWrapper.getRevisionDateString() +
                  "\n  Branches: " + revisionWrapper.getBranchesListString() +
                  "\n  User: " + revisionWrapper.getCommitterName() +
                  "\n  Revision-Id: " + revisionWrapper.getRevisionId() +
                  "\n  Msg: " + revisionWrapper.getCommitMsgBegin() +
                  "\n";

      JTextArea comp = new JTextArea(displayString);

      comp.setEditable(false);

      if(isSelected)
      {
         comp.setBackground(new JTextField().getSelectionColor());
      }

      if(cellHasFocus)
      {
         comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      }

      return comp;
   }
}
