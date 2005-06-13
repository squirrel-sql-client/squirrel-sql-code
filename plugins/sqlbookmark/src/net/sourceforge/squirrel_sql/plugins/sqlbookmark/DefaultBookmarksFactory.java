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

      {"addpk", "alter table add PK",
         "ALTER TABLE MyTable ADD CONSTRAINT MyTable_PK PRIMARY KEY (MyID1,MyID2) \n"},

      {"addcol", "alter table add ...",
         "ALTER TABLE MyTable ADD COLUMN MyCol Integer\n"},

      {"crix", "create index ...",
         "CREATE [UNIQUE] INDEX MyTable_IX ON MyTable(MyCol1, MyCol2)\n"},

      {"addconst", "alter table ... add constraint ...",
         "ALTER TABLE MyChild\n" +
         "ADD CONSTRAINT FK_MyParent\n" +
         "FOREIGN KEY (ParentPK1InChild, ParentPK2InChild)\n" +
         "REFERENCES MyParent (ParentPK1, ParentPK2)\n"},

      {"selwhere", "select where",
         "SELECT MyTable.MyTEXT, MyTable.*\n" +
         "FROM MyTable\n" +
         "WHERE MyID1 = 1\n" +
         "  AND MyID2 = 3\n"},

      {"join", "select join",
         "SELECT MyChild.*\n" +
         "FROM MyParent\n" +
         "[INNER | LEFT | RIGHT] JOIN MyChild ON MyParent.ParentPK1 = MyChild.ParentPK1InChild AND MyParent.ParentPK2 = MyChild.ParentPK2InChild\n" +
         "WHERE MyParent.Name = 'Mom'\n"},

      {"group", "group by",
         "SELECT SUM(price), Author\n" +
         "FROM Books\n" +
         "GROUP BY Author\n"},

      {"grouphaving", "group by having",
         "SELECT SUM(price), Author\n" +
         "FROM Books\n" +
         "GROUP BY Author HAVING SUM(price) > 100\n"},

      {"insertval", "insert values",
         "INSERT INTO MyTable\n" +
         "(MyID1, MyID2, MyTEXT , MyDate) VALUES\n" +
         "(1    , 100  , 'Hello', {ts '2005-06-13 23:25:00'})\n"},

      {"insertsel", "insert select",
         "INSERT INTO MyTable (MyID1, MyID2, MyTEXT, MyDate) \n" +
         "SELECT MyID1 + 100, MyID2 + 200 , MyTEXT || ' world', MyDate FROM MyTable\n"},

      {"update", "update",
         "UPDATE MyTable set MyTEXT = 'Hello big world', MyDate = {ts '2005-06-13 23:36:00'}\n" +
         "WHERE MyID1 = 1\n"},

      {"delete", "delete",
         "DELETE FROM MyTable WHERE MyID1 = 1\n"},

      {"paramexample", "bookmark with parameters",
         "SELECT * FROM MyTable WHERE MyID1 = ${Value of MyID1}\n"}
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
