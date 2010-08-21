package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

import javax.swing.*;
import java.awt.*;

public class DetailPanel extends JPanel
{
   DataSetViewerTablePanel tblDetails;

   public DetailPanel()
   {
      super(new GridLayout(1,1));

      tblDetails = new DataSetViewerTablePanel();
      tblDetails.init(null);

      add(new JScrollPane(tblDetails.getComponent()));
   }
}
