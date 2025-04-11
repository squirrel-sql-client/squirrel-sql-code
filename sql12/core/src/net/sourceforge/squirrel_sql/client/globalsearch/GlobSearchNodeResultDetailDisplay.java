package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeResultDetailDisplay
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeResultDetailDisplay.class);


   private final ResultDataSetAndCellDetailDisplayHandler _detailDisplayHandler;

   public GlobSearchNodeResultDetailDisplay(ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler)
   {
      _detailDisplayHandler = detailDisplayHandler;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeResultDetailDisplay.detail.display");
   }
}
