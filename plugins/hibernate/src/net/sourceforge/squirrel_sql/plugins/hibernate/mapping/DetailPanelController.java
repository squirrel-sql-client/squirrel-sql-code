package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.EmptyDataSet;

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

   public void selectionChanged(MappedClassInfoTreeWrapper mappedClassInfoTreeWrapper)
   {
      try
      {
         if(null == mappedClassInfoTreeWrapper)
         {
            _detailPanel.tblDetails.show(new EmptyDataSet());
         }
         else
         {
            MappedClassInfo mci = mappedClassInfoTreeWrapper.getMappedClassInfo();

            DetailAttribute[] attributes = DetailAttribute.createDetailtPropertyInfoBeans(mci.getAttributes());

            _detailPanel.tblDetails.show(new DetailAttributeDataSet(attributes));
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
