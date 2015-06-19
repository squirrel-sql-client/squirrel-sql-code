package net.sourceforge.squirrel_sql.client.session.mainpanel.textresult;

import net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab.LazyTabControllerCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.DataSetTextAreaController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class TextResultCtrl implements LazyTabControllerCtrl
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(TextResultCtrl.class);


   private final DataSetTextAreaController _dataSetTextAreaController;
   private final TextResultPanel _textResultPanel;

   public TextResultCtrl()
   {
      _dataSetTextAreaController = new DataSetTextAreaController();
      _textResultPanel = new TextResultPanel();
   }

   @Override
   public void init(ResultSetDataSet rsds)
   {
      _dataSetTextAreaController.init(rsds.getDataSetDefinition().getColumnDefinitions(), true);

      for (Object[] row : rsds.getAllDataForReadOnly())
      {
         _dataSetTextAreaController.addRow(row);
      }

      _dataSetTextAreaController.moveToTop();
   }

   @Override
   public String getTitle()
   {
      return s_stringMgr.getString("TextResultCtrl.title");
   }

   @Override
   public JComponent getPanel()
   {
      _textResultPanel._scrollPane.setViewportView(_dataSetTextAreaController.getComponent());

      return _textResultPanel;
   }

   public static boolean isTextResultPanel(Component comp)
   {
      return comp instanceof TextResultPanel;
   }
}
