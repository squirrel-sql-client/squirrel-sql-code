package net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;

public interface LazyTabControllerCtrl
{
   void init(ResultSetDataSet rsds);

   String getTitle();

   JComponent getPanel();
}
