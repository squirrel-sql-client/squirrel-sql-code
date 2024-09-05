package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultTableType;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.DetailAttribute;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.DetailAttributeDataSet;

public class SingleResultController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SingleResultController.class);

   private DataSetViewerTablePanel _tblDetailsMetaData = new DataSetViewerTablePanel();
   private DataSetViewerTablePanel _tblDetailsData = new DataSetViewerTablePanel();


   public SingleResultController(SingleType singleType, JPanel pnlResults, ISession session, ResultControllerChannel _resultControllerChannel)
   {
      try
      {
         DetailAttribute[] attributes = DetailAttribute.createDetailtAttributes(singleType.getMappedClassInfo().getAttributes());

         _tblDetailsData.init(null, new DataModelImplementationDetails(session), session);
         _tblDetailsData.show(new ResultDataSet(singleType));
         DataSetViewerFindHandler tblDetailsDataFindHandler = new DataSetViewerFindHandler(_tblDetailsData, session, ResultTableType.ROWS_WINDOW);

         _tblDetailsMetaData.init(null, new DataModelImplementationDetails(session), session);
         _tblDetailsMetaData.show(new DetailAttributeDataSet(attributes));

         pnlResults.removeAll();
         JTabbedPane tabDataAndMetaData = createTabbedPane(tblDetailsDataFindHandler, _tblDetailsMetaData, session);
         pnlResults.add(tabDataAndMetaData);

         _resultControllerChannel.setActiveControllersListener(new ResultControllerChannelListener()
         {
            @Override
            public boolean findIfTableDisplay()
            {
               tabDataAndMetaData.setSelectedComponent(tblDetailsDataFindHandler.getComponent());
               tblDetailsDataFindHandler.toggleShowFindPanel();
               return true;
            }
         });
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   private JTabbedPane createTabbedPane(DataSetViewerFindHandler tblDetailsDataFindHandler, DataSetViewerTablePanel tblDetailsMetaData, ISession session)
   {
      SessionProperties props = session.getProperties();
      JTabbedPane ret = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement());

      ret.addTab(s_stringMgr.getString("SingleResultController.data"), tblDetailsDataFindHandler.getComponent());
      ret.addTab(s_stringMgr.getString("SingleResultController.metaData"), new JScrollPane(tblDetailsMetaData.getComponent()));

      ret.setSelectedIndex(0);

      return ret;
   }
}
