package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.csv.csvreader;

class CsvReaderSettings
{
   // having these as publicly accessible members will prevent
   // the overhead of the method call that exists on properties
   public boolean caseSensitive;

   public char textQualifier;

   public boolean trimWhitespace;

   public boolean useTextQualifier;

   public Character delimiter;

   public char recordDelimiter;

   public char comment;

   public boolean useComments;

   public int escapeMode;

   public boolean safetySwitch;

   public boolean skipEmptyRecords;

   public CsvReaderSettings()
   {
      caseSensitive = true;
      textQualifier = Letters.QUOTE;
      trimWhitespace = true;
      useTextQualifier = true;
      delimiter = Letters.COMMA;
      recordDelimiter = Letters.NULL;
      comment = Letters.POUND;
      useComments = false;
      escapeMode = CsvReader.ESCAPE_MODE_DOUBLED;
      safetySwitch = true;
      skipEmptyRecords = true;
   }
}
