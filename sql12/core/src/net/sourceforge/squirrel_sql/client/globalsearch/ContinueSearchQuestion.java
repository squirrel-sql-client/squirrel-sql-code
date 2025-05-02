package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class ContinueSearchQuestion
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ContinueSearchQuestion.class);

   private final long _askContinueSearchTimeMillis;
   private final JDialog _parentDlg;
   private long _searchStartMillis;

   public ContinueSearchQuestion(int askContinueSearchTime, JDialog parentDlg)
   {
      _askContinueSearchTimeMillis = askContinueSearchTime * 1000;
      _parentDlg = parentDlg;
      _searchStartMillis = System.currentTimeMillis();
   }

   public boolean isCancel()
   {
      if(0 == _askContinueSearchTimeMillis)
      {
         return false;
      }

      if((System.currentTimeMillis() - _searchStartMillis) > _askContinueSearchTimeMillis)
      {
         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(_parentDlg, s_stringMgr.getString("ContinueSearchQuestion.continue.searching")))
         {
            return true;
         }

         _searchStartMillis = System.currentTimeMillis();
      }

      return false;
   }
}
