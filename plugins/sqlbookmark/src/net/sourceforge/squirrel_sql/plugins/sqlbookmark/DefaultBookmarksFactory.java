package net.sourceforge.squirrel_sql.plugins.sqlbookmark;


public class DefaultBookmarksFactory
{
   private static final String [][] BOOKMARKS =
   {

      {"crtab", "create table ...",
         "CREATE TABLE MyTable\n" +
         "(\n" +
         "   MyID1 INTEGER not null,\n" +
         "   MyID2 INTEGER not null,\n" +
         "   MyTEXT VARCHAR(20),\n" +
         "   MyDate TIMESTAMP,\n" +
         "   CONSTRAINT MyTable_PK PRIMARY KEY (MyID1,MyID2)\n" +
         ")\n"},

      {"addcol", "alter table add ...",
         "ALTER TABLE MyTable ADD COLUMN MyCol Integer\n"},

      {"crix", "create index ...",
         "CREATE [UNIQUE] INDEX MyTable_IX ON MyTable(MyCol1, MyCol2)\n"},

      {"addconst", "alter table ... add constraint ...",
         "ALTER TABLE MyChild\n" +
         "ADD CONSTRAINT FK_MyParent\n" +
         "FOREIGN KEY (ParentPK1InChild, ParentPK2InChild)\n" +
         "REFERENCES MyParent (ParentPK1, ParentPK2)\n"}

   };

   static Bookmark[] getDefaultBookmarks()
   {
      Bookmark[] ret = new Bookmark[BOOKMARKS.length];
      for (int i = 0; i < BOOKMARKS.length; i++)
      {
         ret[i] = new Bookmark(BOOKMARKS[i][0],BOOKMARKS[i][1],BOOKMARKS[i][2]);
      }

      return ret;
   }


}
