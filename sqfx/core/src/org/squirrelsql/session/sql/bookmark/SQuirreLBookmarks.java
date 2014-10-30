package org.squirrelsql.session.sql.bookmark;

public class SQuirreLBookmarks
{
   public static Bookmark[] BOOKMARKS = new Bookmark[]
   {
      new Bookmark("sf", "sf", "SELECT * FROM", false, true),
      new Bookmark("ob", "ob", "ORDER BY", false, true),


      new Bookmark("crtab", "create table ...",
            "CREATE TABLE MyTable\n" +
                        "(\n" +
                  "   MyID1 INTEGER not null,\n" +
                  "   MyID2 INTEGER not null,\n" +
                  "   MyTEXT VARCHAR(20),\n" +
                  "   MyDate TIMESTAMP,\n" +
                  "   CONSTRAINT MyTable_PK PRIMARY KEY (MyID1,MyID2)\n" +
                  ")", false, false),

      new Bookmark("addpk", "alter table add PK",
            "ALTER TABLE MyTable ADD CONSTRAINT MyTable_PK PRIMARY KEY (MyID1,MyID2)", false, false),

      new Bookmark("addcol", "alter table add ...",
                     "ALTER TABLE MyTable ADD COLUMN MyCol Integer", false, false),

      new Bookmark("crix", "create index ...",
                     "CREATE [UNIQUE] INDEX MyTable_IX ON MyTable(MyCol1, MyCol2)", false, false),

      new Bookmark("addconst", "alter table ... add constraint ...",
                     "ALTER TABLE MyChild\n" +
                           "ADD CONSTRAINT FK_MyParent\n" +
                           "FOREIGN KEY (ParentPK1InChild, ParentPK2InChild)\n" +
                           "REFERENCES MyParent (ParentPK1, ParentPK2)", false, false),

      new Bookmark("selwhere", "select where",
                     "SELECT MyTable.MyTEXT, MyTable.*\n" +
                           "FROM MyTable\n" +
                           "WHERE MyID1 = 1\n" +
                           "  AND MyID2 = 3", false, false),

      new Bookmark("join", "select join",
                     "SELECT MyChild.*\n" +
                           "FROM MyParent\n" +
                           "[INNER | LEFT | RIGHT] JOIN MyChild ON MyParent.ParentPK1 = MyChild.ParentPK1InChild AND MyParent.ParentPK2 = MyChild.ParentPK2InChild\n" +
                           "WHERE MyParent.Name = 'Mom'", false, false),

      new Bookmark("group", "group by",
                     "SELECT SUM(price), Author\n" +
                           "FROM Books\n" +
                           "GROUP BY Author", false, false),

      new Bookmark("grouphaving", "group by having",
                     "SELECT SUM(price), Author\n" +
                           "FROM Books\n" +
                           "GROUP BY Author HAVING SUM(price) > 100", false, false),

      new Bookmark("casesimple", "simple form of case",
                     "SELECT\n" +
                           "CASE MyText\n" +
                           "     WHEN 'One' THEN 1 \n" +
                           "     WHEN 'Two' THEN 2 \n" +
                           "     ELSE -1 \n" +
                           "END\n" +
                           "FROM MyTable", false, false),

      new Bookmark("casesstandard", "standard form of case",
                     "SELECT\n" +
                           "CASE\n" +
                           "     WHEN MyText = 'One' THEN 1 \n" +
                           "     WHEN MyText = 'Two' THEN 2 \n" +
                           "     ELSE -1 \n" +
                           "END\n" +
                           "FROM MyTable", false, false),

      new Bookmark("insertval", "insert values",
                     "INSERT INTO MyTable\n" +
                           "(MyID1, MyID2, MyTEXT , MyDate) VALUES\n" +
                           "(1    , 100  , 'Hello', {ts '2005-06-13 23:25:00'})", false, false),

      new Bookmark("insertsel", "insert select",
                     "INSERT INTO MyTable (MyID1, MyID2, MyTEXT, MyDate) \n" +
                           "SELECT MyID1 + 100, MyID2 + 200 , MyTEXT || ' world', MyDate FROM MyTable", false, false),

      new Bookmark("update", "update",
                     "UPDATE MyTable set MyTEXT = 'Hello big world', MyDate = {ts '2005-06-13 23:36:00'}\n" +
                           "WHERE MyID1 = 1", false, false),

      new Bookmark("delete", "delete",
                     "DELETE FROM MyTable WHERE MyID1 = 1", false, false)
   };
}
