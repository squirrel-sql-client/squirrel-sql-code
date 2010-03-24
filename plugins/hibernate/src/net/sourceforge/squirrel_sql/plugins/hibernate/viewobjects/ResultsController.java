package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ResultsController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultsController.class);


   private JPanel _pnlResults;
   private Class _persistenCollectionClass;
   private ArrayList<MappedClassInfo> _allMappedClassInfos;
   private ISession _session;

   private MultipleLineLabel _lblClear;

   public ResultsController(JPanel pnlResults, String hqlQuery, Class persistenCollectionClass, ArrayList<MappedClassInfo> allMappedClassInfos, ISession session)
   {
      _pnlResults = pnlResults;
      _persistenCollectionClass = persistenCollectionClass;
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
         new RootResultController((RootType)type, _pnlResults, _persistenCollectionClass, _allMappedClassInfos);
      }
      else if(type instanceof TupelType)
      {
         new TupelResultController((TupelType) type, _pnlResults, _persistenCollectionClass, _allMappedClassInfos);
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
   }

   public void clear()
   {
      _pnlResults.removeAll();
      _pnlResults.add(new JScrollPane(_lblClear));

      _lblClear.scrollRectToVisible(new Rectangle(0,0,1,1));

      _pnlResults.repaint();

   }
}
