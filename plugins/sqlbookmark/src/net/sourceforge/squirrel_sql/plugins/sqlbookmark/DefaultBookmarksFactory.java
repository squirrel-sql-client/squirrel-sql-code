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
         ")"},

      {"addpk", "alter table add PK",
         "ALTER TABLE MyTable ADD CONSTRAINT MyTable_PK PRIMARY KEY (MyID1,MyID2)"},

      {"addcol", "alter table add ...",
         "ALTER TABLE MyTable ADD COLUMN MyCol Integer"},

      {"crix", "create index ...",
         "CREATE [UNIQUE] INDEX MyTable_IX ON MyTable(MyCol1, MyCol2)"},

      {"addconst", "alter table ... add constraint ...",
         "ALTER TABLE MyChild\n" +
         "ADD CONSTRAINT FK_MyParent\n" +
         "FOREIGN KEY (ParentPK1InChild, ParentPK2InChild)\n" +
         "REFERENCES MyParent (ParentPK1, ParentPK2)"},

      {"selwhere", "select where",
         "SELECT MyTable.MyTEXT, MyTable.*\n" +
         "FROM MyTable\n" +
         "WHERE MyID1 = 1\n" +
         "  AND MyID2 = 3"},

      {"join", "select join",
         "SELECT MyChild.*\n" +
         "FROM MyParent\n" +
         "[INNER | LEFT | RIGHT] JOIN MyChild ON MyParent.ParentPK1 = MyChild.ParentPK1InChild AND MyParent.ParentPK2 = MyChild.ParentPK2InChild\n" +
         "WHERE MyParent.Name = 'Mom'"},

      {"group", "group by",
         "SELECT SUM(price), Author\n" +
         "FROM Books\n" +
         "GROUP BY Author"},

      {"grouphaving", "group by having",
         "SELECT SUM(price), Author\n" +
         "FROM Books\n" +
         "GROUP BY Author HAVING SUM(price) > 100"},

      {"casesimple", "simple form of case",
         "SELECT\n" +
         "CASE MyText\n" +
         "     WHEN 'One' THEN 1 \n" +
         "     WHEN 'Two' THEN 2 \n" +
         "     ELSE -1 \n" +
         "END\n" +
         "FROM MyTable"},

      {"casesstandard", "standard form of case",
         "SELECT\n" +
         "CASE\n" +
         "     WHEN MyText = 'One' THEN 1 \n" +
         "     WHEN MyText = 'Two' THEN 2 \n" +
         "     ELSE -1 \n" +
         "END\n" +
         "FROM MyTable"},

      {"insertval", "insert values",
         "INSERT INTO MyTable\n" +
         "(MyID1, MyID2, MyTEXT , MyDate) VALUES\n" +
         "(1    , 100  , 'Hello', {ts '2005-06-13 23:25:00'})"},

      {"insertsel", "insert select",
         "INSERT INTO MyTable (MyID1, MyID2, MyTEXT, MyDate) \n" +
         "SELECT MyID1 + 100, MyID2 + 200 , MyTEXT || ' world', MyDate FROM MyTable"},

      {"update", "update",
         "UPDATE MyTable set MyTEXT = 'Hello big world', MyDate = {ts '2005-06-13 23:36:00'}\n" +
         "WHERE MyID1 = 1"},

      {"delete", "delete",
         "DELETE FROM MyTable WHERE MyID1 = 1"},

      {"paramexample", "bookmark with parameters",
         "SELECT * FROM MyTable WHERE MyID1 = ${Value of MyID1}"}
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
