package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class IntervalDetailsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(IntervalDetailsController.class);


   public IntervalDetailsController(String intervalDetailsHtml, Frame parent, Point dialogLocation)
   {
      JEditorPane txtPane = new JEditorPane();
      txtPane.setEditable(false);
      txtPane.setContentType("text/html");
      txtPane.setText(intervalDetailsHtml);

      JDialog dlg = new JDialog(parent, s_stringMgr.getString("overview.detailsDialog.title"), false);
      dlg.getContentPane().add(new JScrollPane(txtPane));

      dlg.pack();


      txtPane.setCaretPosition(0);

      dlg.setLocation(dialogLocation);

      GUIUtils.enableCloseByEscape(dlg);

      dlg.setVisible(true);

   }
}
