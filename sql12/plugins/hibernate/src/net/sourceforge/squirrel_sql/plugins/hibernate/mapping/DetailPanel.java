package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.client.session.DefaultDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import javax.swing.*;
import java.awt.*;

public class DetailPanel extends JPanel
{
   DataSetViewerTablePanel tblDetails;

   public DetailPanel(ISession session)
   {
      super(new GridLayout(1,1));

      tblDetails = new DataSetViewerTablePanel();
      tblDetails.init(null, new DefaultDataModelImplementationDetails(session));

      add(new JScrollPane(tblDetails.getComponent()));
   }
}
