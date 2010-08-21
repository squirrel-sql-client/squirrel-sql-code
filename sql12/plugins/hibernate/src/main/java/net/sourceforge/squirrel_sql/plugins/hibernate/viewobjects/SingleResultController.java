package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.DetailAttribute;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.DetailAttributeDataSet;

import javax.swing.*;

public class SingleResultController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SingleResultController.class);

   private SingleType _singleType;

   private DataSetViewerTablePanel _tblDetailsMetaData = new DataSetViewerTablePanel();
   private DataSetViewerTablePanel _tblDetailsData = new DataSetViewerTablePanel();
   private JPanel _pnlResults;


   public SingleResultController(SingleType singleType, JPanel pnlResults, ISession session)
   {
      _pnlResults = pnlResults;

      try
      {
         _singleType = singleType;

         DetailAttribute[] attributes = DetailAttribute.createDetailtAttributes(singleType.getMappedClassInfo().getAttributes());

         _tblDetailsData.init(null);
         _tblDetailsData.show(new ResultDataSet(singleType));

         _tblDetailsMetaData.init(null);
         _tblDetailsMetaData.show(new DetailAttributeDataSet(attributes));

         pnlResults.removeAll();
         pnlResults.add(createTabbedPane(_tblDetailsData, _tblDetailsMetaData, session));

         //_scrollPanelResults.setViewportView(new JLabel("Diplaying table for " + singleType.getMappedClassInfo().getClassName()));
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   private JTabbedPane createTabbedPane(DataSetViewerTablePanel tblDetails, DataSetViewerTablePanel tblDetailsMetaData, ISession session)
   {
      SessionProperties props = session.getProperties();
      JTabbedPane ret = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement());

      ret.addTab(s_stringMgr.getString("SingleResultController.data"), new JScrollPane(tblDetails.getComponent()));
      ret.addTab(s_stringMgr.getString("SingleResultController.metaData"), new JScrollPane(tblDetailsMetaData.getComponent()));

      ret.setSelectedIndex(0);

      return ret;
   }
}
