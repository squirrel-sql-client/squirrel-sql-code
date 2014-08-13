package net.sourceforge.squirrel_sql.client.session.mainpanel.rotatedtable;

import net.sourceforge.squirrel_sql.client.session.DefaultDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import javax.swing.*;
import java.awt.*;

public class RotatedTablePanel extends JPanel
{
   DataSetViewerTablePanel table;

   public RotatedTablePanel(ISession session)
   {
      setLayout(new GridLayout(1, 1));

      table = new DataSetViewerTablePanel();
      table.init(null, new DefaultDataModelImplementationDetails(session));
      add(new JScrollPane(table.getComponent()));

   }
}
