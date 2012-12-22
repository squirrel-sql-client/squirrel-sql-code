package net.sourceforge.squirrel_sql.client.session.properties;

public interface SQLResultConfigCtrlListener
{
   void nbrRowsToShowChanged(int newValue);

   void nbrReadOnBlockSize(int newValue);

   void readOnChkChanged(boolean newValue);

   void limitRowsChkChanged(boolean newValue);
}
