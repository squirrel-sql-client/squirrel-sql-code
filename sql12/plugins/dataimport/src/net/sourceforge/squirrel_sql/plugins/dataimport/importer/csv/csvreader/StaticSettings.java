package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.csvreader;

class StaticSettings
{
   // these are static instead of final so they can be changed in unit test
   // isn't visible outside this class and is only accessed once during
   // CsvReader construction
   public static final int MAX_BUFFER_SIZE = 1024;

   public static final int INITIAL_COLUMN_COUNT = 10;

   public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;
}
