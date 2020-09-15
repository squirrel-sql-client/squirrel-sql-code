package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.csvreader;

class RawRecordBuffer
{
   public char[] buffer;

   public int position;

   public RawRecordBuffer()
   {
      buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE * StaticSettings.INITIAL_COLUMN_COUNT];
      position = 0;
   }
}
