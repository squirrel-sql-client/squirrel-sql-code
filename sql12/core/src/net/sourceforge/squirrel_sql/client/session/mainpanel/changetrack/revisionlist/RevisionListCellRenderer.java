package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch.TextPaneUtil;
import org.eclipse.jgit.lib.Constants;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.awt.Component;

public class RevisionListCellRenderer implements ListCellRenderer<RevisionWrapper>
{

   @Override
   public Component getListCellRendererComponent(JList list, RevisionWrapper revisionWrapper, int index, boolean isSelected, boolean cellHasFocus)
   {
      if(null == revisionWrapper)
      {
         return null;
      }

      JTextPane comp = renderRevision(revisionWrapper);

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

   private JTextPane renderRevision(RevisionWrapper revisionWrapper)
   {
      String displayString = revisionWrapper.getDisplayString() + "\n";

      JTextPane comp = new JTextPane();

      comp.setText(null);

      if (revisionWrapper.isHeadRevision())
      {
         SimpleAttributeSet attributes = new SimpleAttributeSet(comp.getInputAttributes());

         StyleConstants.setBold(attributes, true);
         StyleConstants.setItalic(attributes, true);
         StyleConstants.setUnderline(attributes, true);

         TextPaneUtil.insert(comp, "Current/" + Constants.HEAD + " revision\n", attributes);

         StyleConstants.setBold(attributes, false);
         StyleConstants.setItalic(attributes, false);
         StyleConstants.setUnderline(attributes, false);

         TextPaneUtil.insert(comp, displayString, attributes);
      }
      else
      {
         comp.setText(displayString);
      }
      return comp;
   }
}
