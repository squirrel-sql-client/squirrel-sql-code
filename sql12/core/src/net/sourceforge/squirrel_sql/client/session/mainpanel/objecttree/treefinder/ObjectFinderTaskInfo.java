package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.treefinder;

public class ObjectFinderTaskInfo
{
   private final String _descr;
   private final ObjectTreeFinderTask _task;

   public ObjectFinderTaskInfo(String descr, ObjectTreeFinderTask task)
   {
      _descr = descr;
      _task = task;
   }

   public String getDescr()
   {
      return _descr;
   }

   public ObjectTreeFinderTask getTask()
   {
      return _task;
   }
}
