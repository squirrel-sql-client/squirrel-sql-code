package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.csv.csvreader;

public class DataBuffer
{
   public char[] buffer;

   public int position;

   // / <summary>
   // / How much usable data has been read into the stream,
   // / which will not always be as long as Buffer.Length.
   // / </summary>
   public int count;

   // / <summary>
   // / The position of the cursor in the buffer when the
   // / current column was started or the last time data
   // / was moved out to the column buffer.
   // / </summary>
   public int columnStart;

   public int lineStart;

   public DataBuffer()
   {
      buffer = new char[StaticSettings.MAX_BUFFER_SIZE];
      position = 0;
      count = 0;
      columnStart = 0;
      lineStart = 0;
   }
}
