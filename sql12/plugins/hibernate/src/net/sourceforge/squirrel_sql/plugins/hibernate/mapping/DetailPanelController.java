package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.fw.datasetviewer.EmptyDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;

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


   void clearDetail()
   {
      try
      {
         _detailPanel.tblDetails.show(new EmptyDataSet());
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void selectionChanged(Object userObject)
   {
      try
      {
         if(userObject instanceof MappingRoot)
         {
            MappingRoot root = (MappingRoot) userObject;

            if(0 == root.getMappingProperties().size())
            {
               _detailPanel.tblDetails.show(new EmptyDataSet());
            }
            else
            {
               _detailPanel.tblDetails.show(new HashtableDataSet(root.getMappingProperties()));
            }
         }
         else if (userObject instanceof MappedClassInfoTreeWrapper)
         {
            MappedClassInfoTreeWrapper mappedClassInfoTreeWrapper = (MappedClassInfoTreeWrapper) userObject;
            MappedClassInfo mci = mappedClassInfoTreeWrapper.getMappedClassInfo();
            DetailAttribute[] attributes = DetailAttribute.createDetailtAttributes(mci.getAttributes());
            _detailPanel.tblDetails.show(new DetailAttributeDataSet(attributes));
         }
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }


   }
}
