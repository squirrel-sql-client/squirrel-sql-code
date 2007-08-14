package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import javax.swing.*;

public class DetailPanelController
{
   private DetailPanel _detailPanel;


   public DetailPanelController()
   {
      _detailPanel = new DetailPanel();
   }

   public JComponent getDetailComponent()
   {
      return _detailPanel;
   }
}
