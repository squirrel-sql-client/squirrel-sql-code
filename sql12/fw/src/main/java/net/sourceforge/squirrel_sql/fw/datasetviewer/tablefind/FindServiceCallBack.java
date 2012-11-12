package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

public interface FindServiceCallBack
{
   FindMarkColor getBackgroundColor(int viewRow, int viewColumn);

   void tableCellStructureChanged();
}
