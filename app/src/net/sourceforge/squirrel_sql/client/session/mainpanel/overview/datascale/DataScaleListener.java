package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

public interface DataScaleListener
{
   void intervalSelected(Interval interval);

   void showInTableWin(Interval interval);

   void showInTable(Interval interval);
}
