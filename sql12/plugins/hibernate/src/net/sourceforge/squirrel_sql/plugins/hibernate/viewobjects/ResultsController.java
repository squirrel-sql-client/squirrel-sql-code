package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

public class ResultsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultsController.class);


   private JPanel _pnlResults;
   private ArrayList<MappedClassInfo> _allMappedClassInfos;
   private ISession _session;

   private MultipleLineLabel _lblClear;
   private ResultControllerChannel _resultControllerChannel = new ResultControllerChannel();

   public ResultsController(JPanel pnlResults, String hqlQuery, ArrayList<MappedClassInfo> allMappedClassInfos, ISession session)
   {
      _pnlResults = pnlResults;
      _allMappedClassInfos = allMappedClassInfos;
      _session = session;

      _lblClear = createClearLabel(hqlQuery);

      clear();
   }

   private MultipleLineLabel createClearLabel(String hqlQuery)
   {
      MultipleLineLabel ret = new MultipleLineLabel(s_stringMgr.getString("ResultsController.resultDescription", hqlQuery));
      return ret;
   }

   public void typeChanged(Object type)
   {
      if(type instanceof RootType)
      {
         new RootResultController((RootType)type, _pnlResults, _allMappedClassInfos, _resultControllerChannel);
      }
      else if(type instanceof TupelType)
      {
         new TupelResultController((TupelType) type, _pnlResults, _allMappedClassInfos);
      }
      else if(type instanceof SingleType)
      {
         new SingleResultController((SingleType) type, _pnlResults, _session);
      }
      else if(type instanceof PersistentCollectionType)
      {
         PersistentCollectionType persistentCollectionType = (PersistentCollectionType) type;
         new SingleResultController(persistentCollectionType.getSingleType(), _pnlResults, _session);
      }
      _pnlResults.validate();
      _pnlResults.repaint();


   }

   public void clear()
   {
      _pnlResults.removeAll();
      _pnlResults.add(new JScrollPane(_lblClear));

      _lblClear.scrollRectToVisible(new Rectangle(0,0,1,1));

      _pnlResults.validate();
      _pnlResults.repaint();

   }

   public void projectionDisplayModeChanged()
   {
      _resultControllerChannel.projectionDisplayModeChanged();
   }
}
